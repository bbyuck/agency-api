package com.ndm.core.domain.user.service;

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

import java.util.ArrayList;

import static com.ndm.core.common.enums.FriendshipStatus.ON;
import static com.ndm.core.common.enums.UserStatus.NEW;
import static com.ndm.core.model.ErrorInfo.INVALID_MATCH_MAKER_NAME_3;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final JPAQueryFactory query;

    private final UserRepository userRepository;

    private final MatchMakerRepository matchMakerRepository;

    private final FriendshipRepository friendshipRepository;

    public UserDto join(UserDto newUserDto) {
        /**
         * User 가입 이력 확인
         */
        if (userRepository.findByKakaoId(newUserDto.getKakaoId()) != null) {
            throw new GlobalException(ErrorInfo.REGISTERED_MEMBER);
        }

        String matchMakerNameRule1 = "^[a-zA-Z0-9가-힣]*$";
        String matchMakerNameRule2 = "^[a-zA-Z0-9가-힣]{1,15}$";
        if (!newUserDto.getMatchMakerName().matches(matchMakerNameRule1)
                || !newUserDto.getMatchMakerName().matches(matchMakerNameRule2)) {
            throw new GlobalException(INVALID_MATCH_MAKER_NAME_3);
        }

        /**
         * 주선자가 가입되어 있는지 확인
         */
        MatchMaker relatedMatchMaker = matchMakerRepository.findByName(newUserDto.getMatchMakerName());
        if (relatedMatchMaker == null) {
            throw new GlobalException(INVALID_MATCH_MAKER_NAME_3);
        }

        /**
         * 새로운 유저 save
         */
        User newUser = User.builder()
                .kakaoId(newUserDto.getKakaoId())
                .friendships(new ArrayList<>())
                .status(NEW)
                .photos(new ArrayList<>())
                .build();
        userRepository.save(newUser);

        /**
         * Friendship 관계 생성해 유저와 주선자를 연결
         */
        Friendship newFriendShip = Friendship.builder()
                .matchMaker(relatedMatchMaker)
                .user(newUser)
                .status(ON)
                .build();
        friendshipRepository.save(newFriendShip);

        newFriendShip.makeFriends(relatedMatchMaker, newUser);


        return newUserDto;
    }

}
