package com.ndm.core.domain.matchmaker.service;

import com.ndm.core.common.enums.MemberType;
import com.ndm.core.common.enums.OAuthCode;
import com.ndm.core.common.enums.UserStatus;
import com.ndm.core.domain.matchmaker.dto.MatchMakerDto;
import com.ndm.core.domain.user.dto.UserDto;
import com.ndm.core.domain.user.repository.UserRepository;
import com.ndm.core.domain.user.service.UserService;
import com.ndm.core.entity.MatchMaker;
import com.ndm.core.domain.matchmaker.repository.MatchMakerRepository;
import com.ndm.core.entity.QUser;
import com.ndm.core.entity.User;
import com.ndm.core.model.Current;
import com.ndm.core.model.ErrorInfo;
import com.ndm.core.model.exception.GlobalException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ndm.core.entity.QMatchMaker.*;
import static com.ndm.core.entity.QUser.*;
import static com.ndm.core.model.ErrorInfo.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MatchMakerService {

    private final JPAQueryFactory query;
    private final MatchMakerRepository matchMakerRepository;
    private final Current current;

    private final UserRepository userRepository;

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
                .build();
    }

    public MatchMakerDto join(MatchMakerDto newMatchMakerDto) {
        // 1. 가입 이력 확인
        MatchMaker findMatchMaker = query.selectFrom(matchMaker).where(
                matchMaker.matchMakerToken.eq(newMatchMakerDto.getCredentialToken())
        ).fetchOne();

        if (findMatchMaker != null) {
            log.error("이미 가입된 주선자입니다.");
            throw new GlobalException(INVALID_CREDENTIAL_TOKEN);
        }
        // 로그인 요청시 임시생성된 유저
        User tempUser = query.selectFrom(user)
                .where(user.userToken.eq(newMatchMakerDto.getCredentialToken()))
                .fetchOne();
        if (tempUser == null || tempUser.getStatus() != UserStatus.TEMP) {
            log.error("임시 유저가 없습니다.");
            throw new GlobalException(INVALID_CREDENTIAL_TOKEN);
        }


        MatchMaker newMatchMaker = MatchMaker.builder()
                .matchMakerToken(tempUser.getUserToken())
                .oauthCode(tempUser.getOauthCode())
                .oauthId(tempUser.getOauthId())
                .accessToken(tempUser.getAccessToken())
                .refreshToken(tempUser.getRefreshToken())
                .lastLoginIp(current.getClientIp())
                .build();
        matchMakerRepository.save(newMatchMaker);

        // 임시 유저 제거
        userRepository.delete(tempUser);

        return MatchMakerDto.builder()
                .credentialToken(newMatchMaker.getMatchMakerToken())
                .accessToken(newMatchMaker.getAccessToken())
                .refreshToken(newMatchMaker.getRefreshToken())
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
}
