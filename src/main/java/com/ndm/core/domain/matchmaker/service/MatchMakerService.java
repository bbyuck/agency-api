package com.ndm.core.domain.matchmaker.service;

import com.ndm.core.common.enums.MatchMakerStatus;
import com.ndm.core.common.enums.OAuthCode;
import com.ndm.core.common.util.CommonUtil;
import com.ndm.core.common.util.RSACrypto;
import com.ndm.core.domain.agreement.service.AgreementService;
import com.ndm.core.domain.friendship.service.FriendshipService;
import com.ndm.core.domain.matchmaker.dto.MatchMakerDto;
import com.ndm.core.domain.matchmaker.repository.MatchMakerRepository;
import com.ndm.core.domain.message.dto.FCMTokenDto;
import com.ndm.core.domain.user.dto.UserProfileDto;
import com.ndm.core.domain.user.repository.UserRepository;
import com.ndm.core.entity.*;
import com.ndm.core.model.Current;
import com.ndm.core.model.exception.GlobalException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ndm.core.entity.QFriendship.*;
import static com.ndm.core.entity.QMatchMaker.matchMaker;
import static com.ndm.core.entity.QUser.user;
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
        matchMakerRepository.findByCredentialToken(matchMakerDto.getCredentialToken())
                .orElseThrow(() -> {
                    log.error(INVALID_CREDENTIAL_TOKEN.getMessage());
                    return new GlobalException(INVALID_CREDENTIAL_TOKEN);
                })
                .updateLoginInfo(matchMakerDto.getAccessToken(), matchMakerDto.getRefreshToken(), current.getClientIp());
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
                .credentialToken(result.getCredentialToken())
                .accessToken(result.getAccessToken())
                .refreshToken(result.getRefreshToken())
                .matchMakerStatus(result.getStatus())
                .build();
    }

    public MatchMakerDto join(MatchMakerDto newMatchMakerDto) {
        // 1. 가입 이력 확인
        MatchMaker findMatchMaker = query.selectFrom(matchMaker)
                .where(matchMaker.oauthId.eq(newMatchMakerDto.getOauthId())
                        .and(matchMaker.oauthCode.eq(newMatchMakerDto.getOauthCode()))
                ).fetchOne();

        if (findMatchMaker != null) {
            log.error(MEMBER_ALREADY_EXIST.getMessage());
            throw new GlobalException(MEMBER_ALREADY_EXIST);
        }

        MatchMaker newMatchMaker = MatchMaker.builder()
                .credentialToken(newMatchMakerDto.getCredentialToken())
                .oauthCode(newMatchMakerDto.getOauthCode())
                .oauthId(newMatchMakerDto.getOauthId())
                .accessToken(newMatchMakerDto.getAccessToken())
                .refreshToken(newMatchMakerDto.getRefreshToken())
                .lastLoginIp(current.getClientIp())
                .status(MatchMakerStatus.TEMP)
                .build();
        matchMakerRepository.save(newMatchMaker);


        return MatchMakerDto.builder()
                .credentialToken(newMatchMaker.getCredentialToken())
                .accessToken(newMatchMaker.getAccessToken())
                .refreshToken(newMatchMaker.getRefreshToken())
                .matchMakerStatus(newMatchMaker.getStatus())
                .build();
    }

    /**
     * ID/PW 기반 join 메소드
     * @param matchMakerName
     */
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
        MatchMaker matchMaker = matchMakerRepository.findByCredentialToken(current.getMemberCredentialToken())
                .orElseThrow(() -> new GlobalException(INVALID_CREDENTIAL_TOKEN));

        try {
            return rsaCrypto.encrypt(String.valueOf(matchMaker.getId()));
        } catch (Exception e) {
            log.error("암호화 도중 에러가 발생했습니다.");
            log.error(e.getMessage(), e);
            throw new GlobalException(INTERNAL_SERVER_ERROR);
        }
    }

    public String getUriWithCode() {
        String code = URLEncoder.encode(getCode(), StandardCharsets.UTF_8);
        return clientLocation + "/user/friend?matchmaker=" + code;
    }

    @Transactional(readOnly = true)
    public MatchMakerDto findCaller() {
        MatchMaker caller = matchMakerRepository.findByCredentialToken(current.getMemberCredentialToken())
                .orElseThrow(() -> {
                    log.error(INVALID_CREDENTIAL_TOKEN.getMessage());
                    return new GlobalException(INVALID_CREDENTIAL_TOKEN);
                });
        return MatchMakerDto.builder()
                .matchMakerStatus(caller.getStatus())
                .build();
    }

    public FCMTokenDto registerMatchMakerFCMToken(FCMTokenDto token) {
        matchMakerRepository.findByCredentialToken(current.getMemberCredentialToken())
                .orElseThrow(() -> {
                    log.error(MATCHMAKER_NOT_FOUND.getMessage());
                    return new GlobalException(MATCHMAKER_NOT_FOUND);
                }).registerFCMToken(token);

        return token;
    }

    public MatchMakerDto registerMatchMaker() {
        MatchMaker caller = matchMakerRepository.findByCredentialToken(current.getMemberCredentialToken())
                .orElseThrow(() -> {
                    log.error(MATCHMAKER_NOT_FOUND.getMessage());
                    return new GlobalException(MATCHMAKER_NOT_FOUND);
                });

        caller.changeMatchMakerStatus(MatchMakerStatus.WAIT);
        return MatchMakerDto.builder()
                .matchMakerStatus(caller.getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserProfileDto> findAllUserProfilesFromCallersPool() {
        return query.select(friendship)
                .from(friendship)
                .join(friendship.matchMaker, matchMaker).fetchJoin()
                .join(friendship.user, user).fetchJoin()
                .where(friendship.matchMaker.credentialToken.eq(current.getMemberCredentialToken()))
                .fetch().stream().map(friendshipEntity -> UserProfileDto.builder()
                        .id(friendshipEntity.getUser().getId())
                        .gender(friendshipEntity.getUser().getGender())
                        .age(friendshipEntity.getUser().getAge())
                        .address(friendshipEntity.getUser().getAddress())
                        .job(friendshipEntity.getUser().getJob())
                        .height(friendshipEntity.getUser().getHeight())
                        .userStatus(friendshipEntity.getUser().getStatus())
                        .build()).collect(Collectors.toList());
    }
}
