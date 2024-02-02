package com.ndm.core.domain.kakao.service;

import com.ndm.core.common.enums.MemberStatus;
import com.ndm.core.common.enums.MemberCode;
import com.ndm.core.common.util.OIDCHelper;
import com.ndm.core.common.util.RSACrypto;
import com.ndm.core.domain.agreement.dto.AgreementDto;
import com.ndm.core.domain.agreement.service.AgreementService;
import com.ndm.core.domain.kakao.dto.KakaoLoginDto;
import com.ndm.core.domain.kakao.dto.KakaoLogoutDto;
import com.ndm.core.domain.kakao.dto.KakaoOAuthResponseDto;
import com.ndm.core.domain.kakao.dto.KakaoUserInfoResponseDto;
import com.ndm.core.domain.kakao.exception.InvalidAuthorizationCodeException;
import com.ndm.core.domain.matchmaker.dto.MatchMakerDto;
import com.ndm.core.domain.matchmaker.service.MatchMakerService;
import com.ndm.core.domain.user.dto.UserDto;
import com.ndm.core.domain.user.service.UserService;
import com.ndm.core.model.Current;
import com.ndm.core.model.exception.GlobalException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import static com.ndm.core.common.enums.OAuthCode.*;
import static com.ndm.core.model.ErrorInfo.INTERNAL_SERVER_ERROR;
import static com.ndm.core.model.ErrorInfo.INVALID_TOKEN;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KakaoLoginService {

    @Value("${kakao.key.rest}")
    private String kakaoRestKey;

    @Value("${client.location}")
    private String clientLocation;

    private final MatchMakerService matchMakerService;

    private final Current current;

    private final OIDCHelper oidcHelper;

    private final UserService userService;

    private final AgreementService agreementService;

    private final RSACrypto rsaCrypto;

    @Transactional
    public KakaoLoginDto kakaoLogin(KakaoLoginDto loginDto) {
        /**
         * 0. authorization code가 없을 경우
         */
        if (loginDto.getCode() == null || loginDto.getCode().isEmpty()) {
            throw new InvalidAuthorizationCodeException();
        }

        /*
         * 1. oauth token 발급 요청
         * */
        log.info("request oauth token to kakao server with authorization code ====== {}", loginDto.getCode());
        KakaoOAuthResponseDto kakaoOAuthResponseDto = requestOAuthToken(loginDto.getCode(), true);
        log.debug("result ===== {}", kakaoOAuthResponseDto);
        log.debug("id token ====== {}", kakaoOAuthResponseDto.getId_token());
        log.debug("access token ====== {}", kakaoOAuthResponseDto.getAccess_token());
        log.debug("refresh token ====== {}", kakaoOAuthResponseDto.getRefresh_token());

        Jws<Claims> jws = oidcHelper.getSignedOIDCTokenJws(kakaoOAuthResponseDto.getId_token());
        String subject = jws.getBody().getSubject();
        log.debug("subject ====== {}", subject);


        UserDto userForLogin = userService.findUserByOAuth(subject, KAKAO);
        boolean isNotUser = userForLogin.getCredentialToken() == null;
        MatchMakerDto matchMakerForLogin = matchMakerService.findMatchMakerByOAuth(subject, KAKAO);
        boolean isNotMatchMaker = matchMakerForLogin.getCredentialToken() == null;

        if (isNotMatchMaker && isNotUser) {
            /**
             * MatchMaker 및 유저 DB에 존재하지 않음
             */

            /**
             * 화면으로 카카오 로그인 정보 전달해 회원 타입 설정 및 회원 가입 진행
             */

            try {
                String encryptedOAuthId = rsaCrypto.encrypt(subject);
                if (agreementService.agreeWithAllEssential(KAKAO, subject)) {
                    return KakaoLoginDto.builder()
                            .oauthId(encryptedOAuthId)
                            .oauthCode(KAKAO)
                            .accessToken(kakaoOAuthResponseDto.getAccess_token())
                            .refreshToken(kakaoOAuthResponseDto.getRefresh_token())
                            .memberCode(MemberCode.TEMP)
                            .memberStatus(MemberStatus.NEW)
                            .build();
                }

                agreementService.createAgreement(AgreementDto.builder()
                        .oauthId(subject)
                        .oauthCode(KAKAO)
                        .build());

                return KakaoLoginDto.builder()
                        .oauthId(encryptedOAuthId)
                        .oauthCode(KAKAO)
                        .accessToken(kakaoOAuthResponseDto.getAccess_token())
                        .refreshToken(kakaoOAuthResponseDto.getRefresh_token())
                        .memberCode(MemberCode.TEMP)
                        .memberStatus(MemberStatus.TEMP)
                        .build();
            }
            catch(Exception e) {
                log.error(e.getMessage(), e);
                throw new GlobalException(INTERNAL_SERVER_ERROR);
            }
        } else if (isNotUser) {
            /**
             * 로그인 요청을 한 클라이언트 === MatchMaker
             */
            log.info("{} ====== match maker login");
            matchMakerService.login(matchMakerForLogin);

            return KakaoLoginDto.builder()
                    .credentialToken(matchMakerForLogin.getCredentialToken())
                    .accessToken(matchMakerForLogin.getAccessToken())
                    .refreshToken(matchMakerForLogin.getRefreshToken())
                    .memberCode(MemberCode.MATCH_MAKER)
                    .memberStatus(matchMakerForLogin.getMemberStatus())
                    .build();

        } else {
            /**
             * 로그인 요청을 한 클라이언트 === User
             */
            log.info("{} ====== user login");
            userService.login(userForLogin);

            return KakaoLoginDto.builder()
                    .credentialToken(userForLogin.getCredentialToken())
                    .accessToken(userForLogin.getAccessToken())
                    .refreshToken(userForLogin.getRefreshToken())
                    .memberCode(MemberCode.USER)
                    .memberStatus(userForLogin.getMemberStatus())
                    .build();
        }
    }



    @Transactional
    public void kakaoLogout(KakaoLogoutDto logoutDto) {
        RestClient restClient = RestClient
                .builder()
                .messageConverters(converters -> converters.add(new FormHttpMessageConverter()))
                .build();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("target_id_type", logoutDto.getTargetIdType());
        requestBody.add("target_id", String.valueOf(logoutDto.getUserId()));

        /**
         * 시스템 로그아웃 처리 -> TO DB
         */


        restClient.post()
                .uri("https://kapi.kakao.com/v1/user/logout")
                .header("Authorization", "Bearer " + logoutDto.getAccessToken())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(requestBody)
                .retrieve();
    }




    private KakaoOAuthResponseDto requestOAuthToken(String authorizationCode, boolean forLogin) {
        log.info("requestOAuthToken(authorizationCode) ====== {}", authorizationCode);
        if (authorizationCode == null || authorizationCode.isEmpty()) {
            // 카카오 authorization code가 없음
            throw new GlobalException(INVALID_TOKEN);
        }
        RestClient restClient = RestClient
                .builder()
                .messageConverters(converters -> converters.add(new FormHttpMessageConverter()))
                .build();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", kakaoRestKey);
        requestBody.add("redirect_uri", clientLocation + (forLogin ? "/login" : "") + "/auth");
        requestBody.add("code", authorizationCode);
        try {
            KakaoOAuthResponseDto kakaoOAuthResponseDto = restClient.post()
                    .uri("https://kauth.kakao.com/oauth/token")
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .body(requestBody)
                    .retrieve()
                    .body(KakaoOAuthResponseDto.class);

            log.debug("Kakao OAuth response ====== {}", kakaoOAuthResponseDto);

            return kakaoOAuthResponseDto;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GlobalException(INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Kakao API 서버에 Access token을 전달하고, User 정보를 받아온다.
     *
     * @param accessToken
     * @return
     */
    public KakaoUserInfoResponseDto requestUserInfo(String accessToken) {
        log.info("requestUserInfo(accessToken) ====== {}", accessToken);

        RestClient restClient = RestClient
                .builder()
                .messageConverters(converters -> converters.add(new FormHttpMessageConverter()))
                .build();

        return restClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer" + " " + accessToken)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .body(KakaoUserInfoResponseDto.class);
    }

    public KakaoOAuthResponseDto requestTokenInfo(String accessToken) {
        log.info("requestTokenInfo(accessToken) ====== {}", accessToken);
        RestClient restClient = RestClient
                .builder()
                .messageConverters(converters -> converters.add(new FormHttpMessageConverter()))
                .build();

        return restClient.get()
                .uri("https://kapi.kakao.com/v1/user/access_token_info")
                .header("Authorization", "Bearer" + " " + accessToken)
                .retrieve()
                .body(KakaoOAuthResponseDto.class);
    }

    public KakaoLoginDto authentication(KakaoLoginDto requestDto) {
        /**
         * 0. authorization code가 없을 경우
         */
        if (requestDto.getCode() == null || requestDto.getCode().isEmpty()) {
            throw new InvalidAuthorizationCodeException();
        }

        /*
         * 1. oauth token 발급 요청
         * */
        log.info("request oauth token to kakao server with authorization code ====== {}", requestDto.getCode());
        KakaoOAuthResponseDto kakaoOAuthResponseDto = requestOAuthToken(requestDto.getCode(), false);
        log.debug("result ===== {}", kakaoOAuthResponseDto);
        log.debug("access token ====== {}", kakaoOAuthResponseDto.getAccess_token());
        log.debug("refresh token ====== {}", kakaoOAuthResponseDto.getRefresh_token());


        return KakaoLoginDto
                .builder()
                .accessToken(kakaoOAuthResponseDto.getAccess_token())
                .refreshToken(kakaoOAuthResponseDto.getRefresh_token())
                .build();
    }
}
