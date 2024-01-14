package com.ndm.core.domain.kakao.service;

import com.ndm.core.common.enums.MemberType;
import com.ndm.core.domain.kakao.dto.KakaoLoginDto;
import com.ndm.core.domain.kakao.dto.KakaoLogoutDto;
import com.ndm.core.domain.kakao.dto.KakaoOAuthResponseDto;
import com.ndm.core.domain.kakao.dto.KakaoUserInfoResponseDto;
import com.ndm.core.domain.kakao.exception.InvalidAuthorizationCodeException;
import com.ndm.core.domain.matchmaker.repository.MatchMakerRepository;
import com.ndm.core.domain.matchmaker.service.MatchMakerService;
import com.ndm.core.domain.user.repository.UserRepository;
import com.ndm.core.domain.user.service.UserService;
import com.ndm.core.model.Current;
import com.ndm.core.model.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

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

    private final MatchMakerRepository matchMakerRepository;

    private final UserRepository userRepository;

    private final Current current;

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
        KakaoOAuthResponseDto kakaoOAuthResponseDto = requestOAuthToken(loginDto.getCode());
        log.info("result ===== {}", kakaoOAuthResponseDto);

        /**
         * 2. 발급된 oauth token으로 유저 정보 요청
         */
        KakaoUserInfoResponseDto kakaoUserInfo = requestUserInfo(kakaoOAuthResponseDto.getAccess_token());
        log.info("kakao user info ====== {}", kakaoUserInfo);

        Long kakaoId = kakaoUserInfo.getId();

        boolean isNotUser = userRepository.findByKakaoId(kakaoId) == null;

        boolean isNotMatchMaker = matchMakerRepository.findByKakaoId(kakaoId) == null;
        if (isNotMatchMaker && isNotUser) {
            /**
             * MatchMaker 및 유저 DB에 존재하지 않음
             */
            return KakaoLoginDto.builder()
                    .kakaoId(kakaoId)
                    .accessToken(kakaoOAuthResponseDto.getAccess_token())
                    .refreshToken(kakaoOAuthResponseDto.getRefresh_token())
                    .memberType(MemberType.NEW)
                    .lastLoginIp(current.getClientIp())
                    .build();
            /**
             * 화면으로 카카오 로그인 정보 전달해 회원 타입 설정 및 회원 가입 진행
             */
        }
        else if (isNotMatchMaker) {
            /**
             * 로그인 요청을 한 클라이언트 === user
             */
            log.info("{} ====== user login", kakaoId);
            return KakaoLoginDto.builder()
                    .kakaoId(kakaoId)
                    .accessToken(kakaoOAuthResponseDto.getAccess_token())
                    .refreshToken(kakaoOAuthResponseDto.getRefresh_token())
                    .memberType(MemberType.USER)
                    .lastLoginIp(current.getClientIp())
                    .build();
        }
        else {
            /**
             * 로그인 요청을 한 클라이언트 === match_maker
             */
            log.info("{} ====== match maker login", kakaoId);

            return KakaoLoginDto.builder()
                    .kakaoId(kakaoId)
                    .accessToken(kakaoOAuthResponseDto.getAccess_token())
                    .refreshToken(kakaoOAuthResponseDto.getRefresh_token())
                    .memberType(MemberType.MATCH_MAKER)
                    .lastLoginIp(current.getClientIp())
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

    private KakaoOAuthResponseDto requestOAuthToken(String authorizationCode) {
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
        requestBody.add("redirect_uri", "http://localhost:3000/auth");
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
        }
        catch(Exception e) {
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
}
