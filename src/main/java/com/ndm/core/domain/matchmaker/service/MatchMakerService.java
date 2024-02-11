package com.ndm.core.domain.matchmaker.service;

import com.ndm.core.common.enums.MemberStatus;
import com.ndm.core.common.enums.OAuthCode;
import com.ndm.core.common.util.CommonUtil;
import com.ndm.core.common.util.RSACrypto;
import com.ndm.core.domain.agreement.service.AgreementService;
import com.ndm.core.domain.matchmaker.dto.MatchMakerDto;
import com.ndm.core.domain.matchmaker.repository.MatchMakerRepository;
import com.ndm.core.domain.user.dto.UserDto;
import com.ndm.core.domain.user.repository.UserRepository;
import com.ndm.core.entity.MatchMaker;
import com.ndm.core.entity.User;
import com.ndm.core.model.Current;
import com.ndm.core.model.ErrorInfo;
import com.ndm.core.model.exception.GlobalException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.ndm.core.entity.QMatchMaker.matchMaker;
import static com.ndm.core.model.ErrorInfo.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MatchMakerService {

    private final JPAQueryFactory query;
    private final MatchMakerRepository matchMakerRepository;
    private final UserRepository userRepository;

    private final AgreementService agreementService;

    private final Current current;
    private final RSACrypto rsaCrypto;

    @Value("${client.location}")
    public String clientLocation;

    private final CommonUtil commonUtil;


    public void login(MatchMakerDto matchMakerDto) {
        Optional<MatchMaker> optional = matchMakerRepository.findByMatchMakerToken(matchMakerDto.getCredentialToken());

        if (optional.isEmpty()) {
            log.error(INVALID_CREDENTIAL_TOKEN.getMessage());
            throw new GlobalException(INVALID_CREDENTIAL_TOKEN);
        }
        MatchMaker matchMaker = optional.get();
        matchMaker.updateLoginInfo(matchMakerDto.getAccessToken(), matchMakerDto.getRefreshToken(), current.getClientIp());
    }

    @Transactional(readOnly = true)
    public MatchMakerDto findMatchMakerByOAuth(String oauthId, OAuthCode oauthCode) {
        MatchMaker result = query.selectFrom(matchMaker).where(
                matchMaker.oauthId.eq(oauthId)
                        .and(matchMaker.oauthCode.eq(oauthCode))
        ).fetchOne();

        return result == null
                ? MatchMakerDto.builder().build()
                : MatchMakerDto.builder()
                .credentialToken(result.getMatchMakerToken())
                .accessToken(result.getAccessToken())
                .refreshToken(result.getRefreshToken())
                .memberStatus(result.getStatus())
                .build();
    }

    public MatchMakerDto join(MatchMakerDto newMatchMakerDto) {
        String decryptedOauthId = null;

        try {
            decryptedOauthId = rsaCrypto.decrypt(newMatchMakerDto.getOauthId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GlobalException(INVALID_OAUTH_ID);
        }

        // 1. 가입 이력 확인
        MatchMaker findMatchMaker = query.selectFrom(matchMaker)
                .where(matchMaker.oauthId.eq(decryptedOauthId)
                        .and(matchMaker.oauthCode.eq(newMatchMakerDto.getOauthCode()))
                ).fetchOne();

        if (findMatchMaker != null) {
            log.error(MEMBER_ALREADY_EXIST.getMessage());
            throw new GlobalException(MEMBER_ALREADY_EXIST);
        }
        // 필수 동의서 모두 동의되어있는지 확인
        if (!agreementService.agreeWithAllEssential(OAuthCode.KAKAO, decryptedOauthId)) {
            log.error(DO_NOT_AGREE.getMessage());
            throw new GlobalException(DO_NOT_AGREE);
        }

        MatchMaker newMatchMaker = MatchMaker.builder()
                .matchMakerToken(commonUtil.issueMemberToken())
                .oauthCode(newMatchMakerDto.getOauthCode())
                .oauthId(decryptedOauthId)
                .accessToken(newMatchMakerDto.getAccessToken())
                .refreshToken(newMatchMakerDto.getRefreshToken())
                .lastLoginIp(current.getClientIp())
                .status(MemberStatus.WAIT)
                .build();
        matchMakerRepository.save(newMatchMaker);


        return MatchMakerDto.builder()
                .credentialToken(newMatchMaker.getMatchMakerToken())
                .accessToken(newMatchMaker.getAccessToken())
                .refreshToken(newMatchMaker.getRefreshToken())
                .memberStatus(newMatchMaker.getStatus())
                .build();
    }


//    public MatchMakerDto join(MatchMakerDto newMatchMakerDto) {
//        /**
//         * MatchMakerName validation
//         */
//        validateMatchMakerName(newMatchMakerDto.getMatchMakerName());
//
//        /**
//         * kakaoId 가입 이력 확인
////         */
////        if (matchMakerRepository.findByKakaoId(newMatchMakerDto.getKakaoId()) != null) {
////            throw new GlobalException(REGISTERED_MEMBER);
////        }
//
//        /**
//         * 닉네임 중복 확인
//         */
//        if (matchMakerRepository.findByName(newMatchMakerDto.getMatchMakerName()) != null) {
//            throw new GlobalException(USED_MATCH_MAKER_NAME);
//        }
//
//        /**
//         * matchMaker 정보 저장
//         */
//        MatchMaker newMatchMaker = MatchMaker.builder()
//                .lastLoginIp(current.getClientIp())
//                .name(newMatchMakerDto.getMatchMakerName())
//                .kakaoId(newMatchMakerDto.getKakaoId())
//                .build();
//        matchMakerRepository.save(newMatchMaker);
//
//        return newMatchMakerDto;
//    }

    private void validateMatchMakerName(String matchMakerName) {
        String matchMakerNameRule1 = "^[a-zA-Z0-9가-힣]*$";
        String matchMakerNameRule2 = "^[a-zA-Z0-9가-힣]{1,15}$";
        if (!matchMakerName.matches(matchMakerNameRule1)) {
            throw new GlobalException(INVALID_MATCH_MAKER_NAME_1);
        }
        if (!matchMakerName.matches(matchMakerNameRule2)) {
            throw new GlobalException(INVALID_MATCH_MAKER_NAME_2);
        }
    }

    @Transactional(readOnly = true)
    public String getCode() {
        Optional<MatchMaker> optionalMatchMaker = matchMakerRepository.findByMatchMakerToken(current.getMemberCredentialToken());

        if (optionalMatchMaker.isEmpty()) {
            throw new GlobalException(INVALID_CREDENTIAL_TOKEN);
        }

        try {
            return rsaCrypto.encrypt(String.valueOf(optionalMatchMaker.get().getId()));
        } catch (Exception e) {
            log.error("암호화 도중 에러가 발생했습니다.");
            log.error(e.getMessage(), e);
            throw new GlobalException(INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public String getUriWithCode() {
        String code = URLEncoder.encode(getCode(), StandardCharsets.UTF_8);

        return clientLocation + "?matchmaker=" + code;
    }

    @Transactional(readOnly = true)
    public MatchMakerDto findCaller() {
        Optional<MatchMaker> optional = matchMakerRepository.findByMatchMakerToken(current.getMemberCredentialToken());
        if (optional.isEmpty()) {
            log.error(INVALID_CREDENTIAL_TOKEN.getMessage());
            throw new GlobalException(INVALID_CREDENTIAL_TOKEN);
        }
        MatchMaker caller = optional.get();
        return MatchMakerDto.builder()
                .memberStatus(caller.getStatus())
                .build();
    }

    public String registerMatchMakerFCMToken(String token) {
        Optional<MatchMaker> optional = matchMakerRepository.findByMatchMakerToken(current.getMemberCredentialToken());

        if (optional.isEmpty()) {
            log.error(MATCHMAKER_NOT_FOUND.getMessage());
            throw new GlobalException(MATCHMAKER_NOT_FOUND);
        }

        MatchMaker caller = optional.get();

        caller.registerFCMToken(token);

        return token;
    }
}
