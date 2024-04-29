package com.ndm.core.domain.user.service;

import com.ndm.core.common.enums.UserStatus;
import com.ndm.core.domain.user.dto.UserProfileDto;
import com.ndm.core.domain.user.dto.UserProfileSummaryDto;
import com.ndm.core.domain.user.repository.UserRepository;
import com.ndm.core.entity.Friendship;
import com.ndm.core.entity.MatchMaker;
import com.ndm.core.entity.QUser;
import com.ndm.core.entity.User;
import com.ndm.core.model.Current;
import com.ndm.core.model.exception.GlobalException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.ndm.core.entity.QFriendship.friendship;
import static com.ndm.core.entity.QMatchMaker.matchMaker;
import static com.ndm.core.entity.QMatchingRequest.matchingRequest;
import static com.ndm.core.entity.QUser.user;
import static com.ndm.core.model.ErrorInfo.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProfileService {

    private final JPAQueryFactory query;


    private final UserRepository userRepository;

    private final Current current;

    @Transactional(readOnly = true)
    public List<UserProfileSummaryDto> findMatchingCandidate() {
        List<Friendship> callersFriendships = getCallersFriendship();

        MatchMaker callersMatchMaker = callersFriendships.get(0).getMatchMaker();
        User caller = callersFriendships.get(0).getUser();


        QUser qSender = new QUser("sender");
        QUser qReceiver = new QUser("receiver");

        /**
         * 이전에 caller와 요청을 주고 받은 사이의 유저들
         */
        List<User> requestedList = query.select(matchingRequest)
                .from(matchingRequest)
                .join(matchingRequest.sender, qSender)
                .fetchJoin()
                .join(matchingRequest.receiver, qReceiver)
                .fetchJoin()
                .where(
                        matchingRequest.sender.eq(caller)
                                .or(matchingRequest.receiver.eq(caller))
                )
                .fetch()
                .stream().map(matchingRequestEntity -> {
                    if (matchingRequestEntity.getSender() == caller) {
                        return matchingRequestEntity.getReceiver();
                    } else {
                        return matchingRequestEntity.getSender();
                    }
                }).toList();


        return query.select(friendship)
                .from(friendship)
                .join(friendship.user, user)
                .fetchJoin()
                .where(friendship.matchMaker.eq(callersMatchMaker)
                        .and(friendship.user.credentialToken.ne(current.getMemberCredentialToken())
                                .and(friendship.user.gender.ne(caller.getGender())))
                        .and(friendship.user.status.eq(UserStatus.ACTIVE))
                        .and(friendship.user.notIn(requestedList))
                )
                .fetch()
                .stream().map(
                        entity -> entity.getUser().getUserProfileSummary()
                ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserProfileDto getProfileInfo(Long id) {
        List<Friendship> callersFriendship = getCallersFriendship();
        Friendship targetUserAndMatchMakerFriendship;
        /**
         * 1. 요청한 id가 내 주선자의 풀에 존재하는지 확인.
         */
        try {
            targetUserAndMatchMakerFriendship = query.select(friendship)
                    .from(friendship)
                    .join(friendship.user, user)
                    .fetchJoin()
                    .join(friendship.matchMaker, matchMaker)
                    .where(friendship.matchMaker.eq(callersFriendship.get(0).getMatchMaker())
                            .and(friendship.user.id.eq(id))).fetchOne();
        } catch (Exception e) {
            log.error(FORBIDDEN.getMessage());
            throw new GlobalException(FORBIDDEN);
        }
        assert targetUserAndMatchMakerFriendship != null;
        User targetUser = targetUserAndMatchMakerFriendship.getUser();

        /**
         * 2. 타겟 유저 status 확인
         */
        if (targetUser.getStatus() != UserStatus.ACTIVE) {
            log.info(CANNOT_RECEIVE_REQUEST_STATUS.getMessage());
            throw new GlobalException(CANNOT_RECEIVE_REQUEST_STATUS);
        }

        return targetUser.getUserProfileInfo();
    }


    private List<Friendship> getCallersFriendship() {
        List<Friendship> callersFriendships = query.select(friendship)
                .from(friendship)
                .join(friendship.user, user)
                .fetchJoin()
                .join(friendship.matchMaker, matchMaker)
                .fetchJoin()
                .where(friendship.user.credentialToken.eq(current.getMemberCredentialToken()))
                .fetch();

        if (callersFriendships.isEmpty()) {
            log.error(MATCHMAKER_NOT_FOUND.getMessage());
            throw new GlobalException(MATCHMAKER_NOT_FOUND);
        }
        return callersFriendships;
    }
}

