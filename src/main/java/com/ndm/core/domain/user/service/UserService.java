package com.ndm.core.domain.user.service;

import com.ndm.core.common.enums.FriendshipStatus;
import com.ndm.core.common.enums.MemberType;
import com.ndm.core.common.enums.OAuthCode;
import com.ndm.core.common.util.RSACrypto;
import com.ndm.core.domain.friendship.repository.FriendshipRepository;
import com.ndm.core.domain.matchmaker.repository.MatchMakerRepository;
import com.ndm.core.domain.user.dto.UserDto;
import com.ndm.core.domain.user.repository.UserRepository;
import com.ndm.core.entity.Friendship;
import com.ndm.core.entity.MatchMaker;
import com.ndm.core.entity.User;
import com.ndm.core.model.ErrorInfo;
import com.ndm.core.model.exception.GlobalException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

import static com.ndm.core.common.enums.OAuthCode.KAKAO;
import static com.ndm.core.common.enums.UserStatus.TEMP;
import static com.ndm.core.entity.QUser.user;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final JPAQueryFactory query;

    private final UserRepository userRepository;

    private final MatchMakerRepository matchMakerRepository;

    private final FriendshipRepository friendshipRepository;

    private final RSACrypto rsaCrypto;

    private String issueUserToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    @Transactional(readOnly = true)
    public UserDto findUserByOAuth(String oauthId, OAuthCode oAuthCode) {
        User result = query.selectFrom(user)
                .where( user.oauthId.eq(oauthId)
                .and(   user.oauthCode.eq(oAuthCode)
                ))
                .fetchOne();

        return result == null ?
                UserDto.builder().build() :
                UserDto.builder()
                .credentialToken(result.getUserToken())
                .accessToken(result.getAccessToken())
                .refreshToken(result.getRefreshToken())
                .build();
    }

    public UserDto kakaoTempJoin(String kakaoId, String accessToken, String refreshToken) {
        User newUser = User.builder()
                .oauthId(kakaoId)
                .oauthCode(KAKAO)
                .userToken(issueUserToken())
                .status(TEMP)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        userRepository.save(newUser);

        return UserDto.builder()
                .credentialToken(newUser.getUserToken())
                .accessToken(newUser.getAccessToken())
                .refreshToken(newUser.getRefreshToken())
                .build();
    }

    public UserDto join(UserDto userDto) {
        String matchMakerCode = userDto.getMatchMakerCode();
        /**
         * 주선자 코드 확인
         */
        if (!StringUtils.hasText(matchMakerCode)) {
            log.error("주선자 코드가 없습니다.");
            throw new GlobalException(ErrorInfo.INVALID_MATCHMAKER_CODE);
        }
        Long matchMakerId;
        try {
             matchMakerId = Long.valueOf(rsaCrypto.decrypt(matchMakerCode));
        }
        catch(Exception e) {
            log.error(e.getMessage(), e);
            throw new GlobalException(ErrorInfo.INVALID_MATCHMAKER_CODE);
        }

        Optional<MatchMaker> optionalMatchMaker = matchMakerRepository.findById(matchMakerId);
        if (optionalMatchMaker.isEmpty()) {
            log.debug("입력한 코드로 주선자를 찾지 못했습니다. 코드를 다시 확인해주세요.");
            throw new GlobalException(ErrorInfo.MATCHMAKER_NOT_FOUND);
        }

        /**
         * 임시유저 조회
         */
        User newUser = query.selectFrom(user)
                .where(user.userToken.eq(userDto.getCredentialToken())).fetchOne();

        if (newUser == null) {
            log.error("임시 유저 생성이 되지 않았습니다. Authentication 요청부터 다시 진행해주세요.");
            throw new GlobalException(ErrorInfo.INVALID_CREDENTIAL_TOKEN);
        }

        if (newUser.getStatus() != TEMP) {
            log.error("이미 가입된 유저입니다.");
            throw new GlobalException(ErrorInfo.INVALID_CREDENTIAL_TOKEN);
        }

        /**
         * 유저와 주선자간 관계 생성
         */
        Friendship friendship = Friendship.builder()
                .user(newUser)
                .matchMaker(optionalMatchMaker.get())
                .status(FriendshipStatus.ON)
                .build();
        friendshipRepository.save(friendship);

        /**
         * 정식 가입 상태로 상태 변경
         */
        newUser.officiallySignedUp();

        return UserDto.builder()
                .credentialToken(newUser.getUserToken())
                .accessToken(newUser.getAccessToken())
                .refreshToken(newUser.getRefreshToken())
                .build();
    }

//    public UserDto idJoin(UserDto newUserDto) {
//        /**
//         * User 가입 이력 확인
//         */
//        if (userRepository.findByOauthId(newUserDto.getKakaoId()) != null) {
//            throw new GlobalException(ErrorInfo.REGISTERED_MEMBER);
//        }
//
//        String matchMakerNameRule1 = "^[a-zA-Z0-9가-힣]*$";
//        String matchMakerNameRule2 = "^[a-zA-Z0-9가-힣]{1,15}$";
//        if (!newUserDto.getMatchMakerName().matches(matchMakerNameRule1)
//                || !newUserDto.getMatchMakerName().matches(matchMakerNameRule2)) {
//            throw new GlobalException(INVALID_MATCH_MAKER_NAME_3);
//        }
//
//        /**
//         * 주선자가 가입되어 있는지 확인
//         */
//        MatchMaker relatedMatchMaker = matchMakerRepository.findByName(newUserDto.getMatchMakerName());
//        if (relatedMatchMaker == null) {
//            throw new GlobalException(INVALID_MATCH_MAKER_NAME_3);
//        }
//
//        /**
//         * 새로운 유저 save
//         */
//        User newUser = User.builder()
//                .kakaoId(newUserDto.getKakaoId())
//                .friendships(new ArrayList<>())
//                .status(NEW)
//                .photos(new ArrayList<>())
//                .build();
//        userRepository.save(newUser);
//
//        /**
//         * Friendship 관계 생성해 유저와 주선자를 연결
//         */
//        Friendship newFriendShip = Friendship.builder()
//                .matchMaker(relatedMatchMaker)
//                .user(newUser)
//                .status(ON)
//                .build();
//        friendshipRepository.save(newFriendShip);
//
//        newFriendShip.makeFriends(relatedMatchMaker, newUser);
//
//
//        return newUserDto;
//    }

}
