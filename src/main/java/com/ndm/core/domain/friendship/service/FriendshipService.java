package com.ndm.core.domain.friendship.service;

import com.ndm.core.common.enums.FriendshipStatus;
import com.ndm.core.common.util.RSACrypto;
import com.ndm.core.domain.friendship.dto.FriendshipRequestDto;
import com.ndm.core.domain.friendship.dto.FriendshipResponseDto;
import com.ndm.core.domain.friendship.repository.FriendshipRepository;
import com.ndm.core.domain.matchmaker.repository.MatchMakerRepository;
import com.ndm.core.domain.user.repository.UserRepository;
import com.ndm.core.entity.*;
import com.ndm.core.model.Current;
import com.ndm.core.model.ErrorInfo;
import com.ndm.core.model.exception.GlobalException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.ndm.core.entity.QFriendship.*;
import static com.ndm.core.entity.QMatchMaker.*;
import static com.ndm.core.entity.QUser.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final MatchMakerRepository matchMakerRepository;

    private final JPAQueryFactory query;
    private final Current current;
    private final RSACrypto rsaCrypto;

    public FriendshipResponseDto createFriendship(FriendshipRequestDto requestDto) {
        /**
         * 1. 유저 확인
         */
        User caller = userRepository.findByCredentialToken(current.getMemberCredentialToken()).orElseThrow(() -> {
            log.error(ErrorInfo.USER_NOT_FOUND.getMessage());
            return new GlobalException(ErrorInfo.USER_NOT_FOUND);
        });

        /**
         * 2. 주선자 확인
         */
        Long decryptedMatchMakerId = null;
        try {
            decryptedMatchMakerId = Long.parseLong(rsaCrypto.decrypt(requestDto.getMatchMakerCode()));
        }
        catch(Exception e) {
            log.error(ErrorInfo.INVALID_MATCHMAKER_CODE.getMessage());
            throw new GlobalException(ErrorInfo.INVALID_MATCHMAKER_CODE);
        }

        MatchMaker invitingMatchMaker = matchMakerRepository.findById(decryptedMatchMakerId)
                .orElseThrow(() -> {
                    log.error(ErrorInfo.MATCHMAKER_NOT_FOUND.getMessage());
                    return new GlobalException(ErrorInfo.MATCHMAKER_NOT_FOUND);
                });

        /**
         * 3. 유저와 주선자가 서로 friendship 관계가 아닌지 확인
         */
        Long friendshipCount = query.select(friendship.count())
                .from(friendship)
                .join(friendship.user, user)
                .fetchJoin()
                .join(friendship.matchMaker, matchMaker)
                .fetchJoin()
                .where(
                        friendship.user.eq(caller)
                                .and(friendship.matchMaker.eq(invitingMatchMaker))
                )
                .fetchOne();

        if (friendshipCount > 0) {
            log.error(ErrorInfo.FRIENDSHIP_ALREAD_EXIST.getMessage());
            throw new GlobalException(ErrorInfo.FRIENDSHIP_ALREAD_EXIST);
        }

        /**
         * Friendship insert
         */
        Friendship newFriendship = Friendship.builder()
                .user(caller)
                .matchMaker(invitingMatchMaker)
                .status(FriendshipStatus.ACTIVE)
                .build();
        friendshipRepository.save(newFriendship);


        return FriendshipResponseDto.builder()
                .result(true)
                .build();
    }

}
