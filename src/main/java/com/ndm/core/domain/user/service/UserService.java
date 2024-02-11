package com.ndm.core.domain.user.service;

import com.ndm.core.common.enums.FriendshipStatus;
import com.ndm.core.common.enums.OAuthCode;
import com.ndm.core.common.util.CommonUtil;
import com.ndm.core.common.util.RSACrypto;
import com.ndm.core.domain.FCMTokenDto;
import com.ndm.core.domain.agreement.service.AgreementService;
import com.ndm.core.domain.friendship.repository.FriendshipRepository;
import com.ndm.core.domain.matchmaker.repository.MatchMakerRepository;
import com.ndm.core.domain.user.dto.UserDto;
import com.ndm.core.domain.user.dto.UserProfileDto;
import com.ndm.core.domain.user.repository.UserRepository;
import com.ndm.core.entity.Friendship;
import com.ndm.core.entity.MatchMaker;
import com.ndm.core.entity.User;
import com.ndm.core.model.Current;
import com.ndm.core.model.ErrorInfo;
import com.ndm.core.model.exception.GlobalException;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static com.ndm.core.common.enums.MemberStatus.ACTIVE;
import static com.ndm.core.common.enums.MemberStatus.PROFILE_MAKING;
import static com.ndm.core.entity.QPhoto.photo;
import static com.ndm.core.entity.QUser.user;
import static com.ndm.core.model.ErrorInfo.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final JPAQueryFactory query;

    private final UserRepository userRepository;

    private final MatchMakerRepository matchMakerRepository;

    private final FriendshipRepository friendshipRepository;

    private final AgreementService agreementService;

    private final RSACrypto rsaCrypto;

    private final Current current;

    private final CommonUtil commonUtil;


    @Transactional(readOnly = true)
    public UserDto findUserByOAuth(String oauthId, OAuthCode oAuthCode) {
        User result = query.selectFrom(user)
                .where(user.oauthId.eq(oauthId)
                        .and(user.oauthCode.eq(oAuthCode)
                        ))
                .fetchOne();

        return result == null ?
                UserDto.builder().build() :
                UserDto.builder()
                        .credentialToken(result.getUserToken())
                        .accessToken(result.getAccessToken())
                        .refreshToken(result.getRefreshToken())
                        .memberStatus(result.getStatus())
                        .build();
    }

    public void login(UserDto userDto) {
        Optional<User> optional = userRepository.findByUserToken(userDto.getCredentialToken());

        if (optional.isEmpty()) {
            log.error(ErrorInfo.INVALID_CREDENTIAL_TOKEN.getMessage());
            throw new GlobalException(ErrorInfo.INVALID_CREDENTIAL_TOKEN);
        }
        User user = optional.get();
        user.updateLoginInfo(current.getClientIp(), userDto.getAccessToken(), userDto.getRefreshToken());
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GlobalException(ErrorInfo.INVALID_MATCHMAKER_CODE);
        }

        Optional<MatchMaker> optionalMatchMaker = matchMakerRepository.findById(matchMakerId);
        if (optionalMatchMaker.isEmpty() || optionalMatchMaker.get().getStatus() != ACTIVE) {
            log.debug("입력한 코드로 주선자를 찾지 못했습니다. 코드를 다시 확인해주세요.");
            throw new GlobalException(ErrorInfo.MATCHMAKER_NOT_FOUND);
        }

        /**
         * 임시유저 조회
         */
        String decryptedOauthId = null;

        try {
            decryptedOauthId = rsaCrypto.decrypt(userDto.getOauthId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GlobalException(ErrorInfo.INVALID_OAUTH_ID);
        }

        if (!agreementService.agreeWithAllEssential(userDto.getOauthCode(), decryptedOauthId)) {
            log.error(ErrorInfo.DO_NOT_AGREE.getMessage());
            throw new GlobalException(ErrorInfo.DO_NOT_AGREE);
        }
        Optional<User> user = userRepository.findByOauthCodeAndOauthId(userDto.getOauthCode(), decryptedOauthId);
        if (user.isPresent()) {
            log.error(ErrorInfo.REGISTERED_MEMBER.getMessage());
            throw new GlobalException(ErrorInfo.REGISTERED_MEMBER);
        }

        User newUser = User.builder()
                .accessToken(userDto.getAccessToken())
                .refreshToken(userDto.getRefreshToken())
                .userToken(commonUtil.issueMemberToken())
                .oauthCode(userDto.getOauthCode())
                .oauthId(decryptedOauthId)
                .status(PROFILE_MAKING)
                .build();
        userRepository.save(newUser);

        /**
         * 유저와 주선자간 관계 생성
         */
        Friendship friendship = Friendship.builder()
                .user(newUser)
                .matchMaker(optionalMatchMaker.get())
                .status(FriendshipStatus.ON)
                .build();
        friendshipRepository.save(friendship);

        log.info("{}님 가입되었습니다.", newUser.getUserToken());

        return UserDto.builder()
                .credentialToken(newUser.getUserToken())
                .accessToken(newUser.getAccessToken())
                .refreshToken(newUser.getRefreshToken())
                .memberStatus(newUser.getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public UserProfileDto findCallersProfile() {
        User caller;
        try {
            caller = query
                    .select(user)
                    .from(user)
                    .where(user.userToken.eq(current.getMemberCredentialToken())).fetchOne();
        }
        catch(NonUniqueResultException e) {
            log.error(e.getMessage(), e);
            throw new GlobalException(ErrorInfo.INVALID_CREDENTIAL_TOKEN);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GlobalException(ErrorInfo.INTERNAL_SERVER_ERROR);
        }
        if (caller == null) {
            log.error("유저를 찾을 수 없습니다.");
            throw new GlobalException(ErrorInfo.USER_NOT_FOUND);
        }

        return caller.getUserProfileInfo();
    }


    public UserProfileDto saveCallersProfile(UserProfileDto userProfileDto) {
        Optional<User> optional = userRepository.findByUserToken(current.getMemberCredentialToken());
        if (optional.isEmpty()) {
            log.error(ErrorInfo.INVALID_CREDENTIAL_TOKEN.getMessage());
            throw new GlobalException(ErrorInfo.INVALID_CREDENTIAL_TOKEN);
        }
        User caller = optional.get();
        log.info("mbti ====== {}", userProfileDto.getMbti());
        caller.updateProfileInfo(userProfileDto);

        return userProfileDto;
    }

    public UserDto registerProfile(UserProfileDto userProfileDto) {
        if (userProfileDto.getGender() == null) {
            throw new GlobalException(GENDER_EMPTY);
        }
        if (!StringUtils.hasText(userProfileDto.getAge())) {
            throw new GlobalException(AGE_EMPTY);
        }
        if (!StringUtils.hasText(userProfileDto.getAddress())) {
            throw new GlobalException(ADDRESS_EMPTY);
        }
        if (!StringUtils.hasText(userProfileDto.getJob())) {
            throw new GlobalException(JOB_EMPTY);
        }
        if (userProfileDto.getHeight() == null) {
            throw new GlobalException(HEIGHT_EMPTY);
        }
        if (!StringUtils.hasText(userProfileDto.getHobby())) {
            throw new GlobalException(HOBBY_EMPTY);
        }
        if (userProfileDto.getMbti() == null) {
            throw new GlobalException(MBTI_EMPTY);
        }
        if (!StringUtils.hasText(userProfileDto.getIdealType())) {
            throw new GlobalException(IDEAL_TYPE_EMPTY);
        }
        if (!StringUtils.hasText(userProfileDto.getSelfDescription())) {
            throw new GlobalException(SELF_DESCRIPTION_EMPTY);
        }

        User profileOwner = query.select(user)
                .from(user).join(user.photos, photo)
                .fetchJoin()
                .where(user.userToken.eq(current.getMemberCredentialToken())).fetchOne();
        if (profileOwner.getPhotos().size() < 2 || profileOwner.getPhotos().size() > 5) {
            throw new GlobalException(PHOTO_INVALID_SIZE);
        }
        profileOwner.registerProfile(userProfileDto);


        return UserDto.builder()
                .memberStatus(profileOwner.getStatus())
                .build();
    }


    @Transactional(readOnly = true)
    public UserDto findCaller() {
        Optional<User> optional = userRepository.findByUserToken(current.getMemberCredentialToken());
        if (optional.isEmpty()) {
            log.error(INVALID_CREDENTIAL_TOKEN.getMessage());
            throw new GlobalException(INVALID_CREDENTIAL_TOKEN);
        }
        User caller = optional.get();
        return UserDto.builder()
                .memberStatus(caller.getStatus())
                .build();
    }

    public FCMTokenDto registerUserFCMToken(FCMTokenDto token) {
        Optional<User> optional = userRepository.findByUserToken(current.getMemberCredentialToken());

        if (optional.isEmpty()) {
            log.error(USER_NOT_FOUND.getMessage());
            throw new GlobalException(USER_NOT_FOUND);
        }

        User caller = optional.get();
        caller.registerFCMToken(token.getValue());

        return token;
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
