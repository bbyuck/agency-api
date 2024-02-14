package com.ndm.core.domain.matching.service;

import com.ndm.core.common.enums.ClientMessageCode;
import com.ndm.core.common.enums.MatchingStatus;
import com.ndm.core.common.enums.MemberStatus;
import com.ndm.core.domain.file.dto.FileResponseDto;
import com.ndm.core.domain.file.service.FileService;
import com.ndm.core.domain.matching.dto.*;
import com.ndm.core.domain.matching.repository.MatchingRepository;
import com.ndm.core.domain.matching.repository.MatchingRequestRepository;
import com.ndm.core.domain.message.service.ClientMessageService;
import com.ndm.core.domain.user.dto.MatchingRequestRemainDto;
import com.ndm.core.domain.user.dto.UserDto;
import com.ndm.core.domain.user.dto.UserProfileDto;
import com.ndm.core.domain.user.repository.UserRepository;
import com.ndm.core.entity.Matching;
import com.ndm.core.entity.MatchingRequest;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.ndm.core.common.enums.Gender.M;
import static com.ndm.core.common.enums.Gender.W;
import static com.ndm.core.common.enums.MatchingRequestStatus.ACTIVE;
import static com.ndm.core.common.enums.MatchingRequestStatus.CONFIRMED;
import static com.ndm.core.common.enums.MatchingStatus.READY;
import static com.ndm.core.common.enums.MemberStatus.REQUEST_CONFIRMED;
import static com.ndm.core.entity.QMatching.matching;
import static com.ndm.core.entity.QMatchingRequest.matchingRequest;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MatchingService {

    private final JPAQueryFactory query;

    private final UserRepository userRepository;

    private final MatchingRequestRepository matchingRequestRepository;

    private final MatchingRepository matchingRepository;

    private final Current current;

    private final ClientMessageService clientMessageService;

    private final FileService fileService;

    public MatchingRequestResultDto sendRequest(MatchingRequestDto requestDto) {
        log.info("requestDto ====== {}", requestDto.toString());

        // 1. 이전에 요청읇 보냈거나 매칭된 적이 있는지 여부 확인
        // 1.1. 서로가 서로에게 요청을 보낸적이 있는지 확인
        Optional<User> senderOptional = userRepository.findByUserToken(current.getMemberCredentialToken());
        if (senderOptional.isEmpty()) {
            log.error(ErrorInfo.USER_NOT_FOUND.getMessage());
            throw new GlobalException(ErrorInfo.USER_NOT_FOUND);
        }
        User sender = senderOptional.get();

        Optional<User> receiverOptional = userRepository.findById(requestDto.getId());
        if (receiverOptional.isEmpty()) {
            log.error(ErrorInfo.OPPONENT_NOT_FOUND.getMessage());
            throw new GlobalException(ErrorInfo.OPPONENT_NOT_FOUND);
        }
        User receiver = receiverOptional.get();


        List<MatchingRequest> matchingRequestLog = query.select(matchingRequest)
                .from(matchingRequest)
                .where(
                        (matchingRequest.sender.eq(sender).and(matchingRequest.receiver.eq(receiver)))
                                .or((matchingRequest.sender.eq(receiver).and(matchingRequest.receiver.eq(sender))))

                ).fetch();

        if (!matchingRequestLog.isEmpty()) {
            log.error(ErrorInfo.MATCH_REQUESTED_BEFORE.getMessage());
            throw new GlobalException(ErrorInfo.MATCH_REQUESTED_BEFORE);
        }

        // 1.2. 매칭이 되었던 적이 있는지 확인
        List<Matching> matchingLog = query.select(matching)
                .from(matching)
                .where(
                        matching.man.eq(sender.getGender() == M ? sender : receiver)
                                .and(matching.woman.eq(sender.getGender() == M ? receiver : sender)))
                .fetch();
        if (!matchingLog.isEmpty()) {
            log.error(ErrorInfo.MATCHED_BEFORE.getMessage());
            throw new GlobalException(ErrorInfo.MATCHED_BEFORE);
        }

        /**
         * 2. 유저 상태 체크
         */
        if (sender.getStatus() != MemberStatus.ACTIVE) {
            log.error(ErrorInfo.CANNOT_SEND_REQUEST_STATUS.getMessage());
            throw new GlobalException(ErrorInfo.CANNOT_SEND_REQUEST_STATUS);
        }
        if (receiver.getStatus() != MemberStatus.ACTIVE) {
            log.error(ErrorInfo.CANNOT_RECEIVE_REQUEST_STATUS.getMessage());
        }

        /**
         * 3. 새로운 매칭 요청 생성
         */
        MatchingRequest newRequest = MatchingRequest.builder()
                .sender(sender)
                .status(ACTIVE)
                .receiver(receiver)
                .build();
        matchingRequestRepository.save(newRequest);

        /**
         * 4. 유저 상태 변경
         */
        sender.changeUserStatus(MemberStatus.MATCHING_WAIT);
        receiver.changeUserStatus(MemberStatus.REQUEST_RECEIVED);

        /**
         * 5. 알림 처리
         * 5.1. WebSocket session에 들어와 있는 경우 -> Online : WebSocket Message send
         * 5.2. 들어와있지 않은 경우 -> FCM Push Message
         */
        clientMessageService.sendMessageForUser(ClientMessageCode.REQUEST_RECEIVED, receiver);


        return MatchingRequestResultDto.builder()
                .memberStatus(sender.getStatus())
                .build();
    }

    public UserDto confirmReceivedRequest() {
        MatchingRequest receivedRequest = getReceivedRequest();
        receivedRequest.confirm();
        User caller = receivedRequest.getReceiver();
        caller.changeUserStatus(REQUEST_CONFIRMED);

        return UserDto.builder()
                .memberStatus(caller.getStatus())
                .build();
    }

    public MatchingResponseDto acceptMatchingRequest(MatchingRequestDto requestDto) {

        List<MatchingRequest> findMatchingRequest = query.select(matchingRequest)
                .from(matchingRequest)
                .join(matchingRequest.sender, new QUser("sender"))
                .fetchJoin()
                .join(matchingRequest.receiver, new QUser("receiver"))
                .fetchJoin()
                .where(matchingRequest.id.eq(requestDto.getId()))
                .fetch();

        if (findMatchingRequest.isEmpty()) {
            log.error(ErrorInfo.MATCHING_REQUEST_NOT_FOUND.getMessage());
            throw new GlobalException(ErrorInfo.MATCHING_REQUEST_NOT_FOUND);
        }
        MatchingRequest receivedMatchingRequest = findMatchingRequest.get(0);

        User sender = receivedMatchingRequest.getSender();
        User receiver = receivedMatchingRequest.getReceiver();

        /**
         * 1. matching 생성
         * 2. sender 상태 변경 -> MATCHING
         * 3. receiver 상태 변경 -> MATCHING_CONFIRMED
         * 4. matching request 상태 변경
         * 5. sender에게 clientMessage 전송
         */
        Matching newMatching = Matching.builder()
                .requesterId(sender.getId())
                .man(sender.getGender() == M ? sender : receiver)
                .woman(sender.getGender() == W ? sender : receiver)
                .matchingRequest(receivedMatchingRequest)
                .matchingDate(LocalDateTime.now())
                .step(1)
                .status(READY)
                .build();
        matchingRepository.save(newMatching);
        sender.changeUserStatus(MemberStatus.MATCHING);
        receiver.changeUserStatus(MemberStatus.MATCHING_CONFIRMED);
        receivedMatchingRequest.accept();

        clientMessageService.sendMessageForUser(ClientMessageCode.REQUEST_ACCEPTED, sender);

        return MatchingResponseDto.builder()
                .memberStatus(receiver.getStatus())
                .build();
    }


    @Transactional(readOnly = true)
    public ReceivedRequestDto getReceivedRequestSender() {
        MatchingRequest receivedRequest = getReceivedRequest();
        User sender = receivedRequest.getSender();

        UserProfileDto senderProfileInfo = sender.getUserProfileInfo();
        return ReceivedRequestDto.builder()
                .senderProfileInfo(senderProfileInfo)
                .matchingRequestStatus(receivedRequest.getStatus())
                .id(receivedRequest.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public MatchingRequest getReceivedRequest() {
        List<MatchingRequest> result = query.select(matchingRequest)
                .from(matchingRequest)
                .join(matchingRequest.receiver, new QUser("receiver"))
                .fetchJoin()
                .join(matchingRequest.sender, new QUser("sender"))
                .fetchJoin()
                .where(matchingRequest.receiver.userToken.eq(current.getMemberCredentialToken())
                        .and(matchingRequest.status.in(Arrays.asList(ACTIVE, CONFIRMED))))
                .fetch();

        if (result.isEmpty()) {
            log.error(ErrorInfo.RECEIVED_REQUEST_NOT_FOUND.getMessage());
            throw new GlobalException(ErrorInfo.RECEIVED_REQUEST_NOT_FOUND);
        }
        MatchingRequest receivedRequest = result.get(0);
        return receivedRequest;
    }

    public MatchingRequestResultDto rejectRequest() {
        MatchingRequest receivedRequest = getReceivedRequest();

        User sender = receivedRequest.getSender();
        User receiver = receivedRequest.getReceiver();

        /**
         * 1. request 상태 변경
         */
        receivedRequest.reject();

        /**
         * 2. 유저들 상태 변경
         */
        sender.changeUserStatus(MemberStatus.ACTIVE);
        receiver.changeUserStatus(MemberStatus.ACTIVE);

        /**
         * 3. sender가 웹소켓 접속되어 있는 경우 alert
         */
        clientMessageService.sendMessageForUser(ClientMessageCode.REQUEST_REJECTED, sender);


        return MatchingRequestResultDto.builder()
                .memberStatus(sender.getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public Long findTodayRequestCount() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        LocalDateTime lastMidnight = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);
        LocalDateTime thisMidnight = LocalDateTime.of(tomorrow.getYear(), tomorrow.getMonth(), tomorrow.getDayOfMonth(), 0, 0, 0);

        return query.select(matchingRequest.count())
                .from(matchingRequest)
                .where(
                        matchingRequest.sender.userToken.eq(current.getMemberCredentialToken())
                                .and(matchingRequest.createdDate.between(lastMidnight, thisMidnight))
                )
                .fetchOne();
    }

    @Transactional(readOnly = true)
    public MatchingRequestRemainDto findMatchingRequestRemain() {
        return MatchingRequestRemainDto.builder()
                .searched(true)
                .currentCount(findTodayRequestCount())
                .maxCount(2L)
                .build();
    }

    public MatchingResponseDto confirmMatching() {
        Optional<User> optional = userRepository.findByUserToken(current.getMemberCredentialToken());
        if (optional.isEmpty()) {
            log.error(ErrorInfo.USER_NOT_FOUND.getMessage());
            throw new GlobalException(ErrorInfo.USER_NOT_FOUND);
        }
        User caller = optional.get();

        List<Matching> findMatching = query.select(matching)
                .from(matching)
                .where(
                        (caller.getGender().equals(M)
                                ? matching.man.eq(caller) : matching.woman.eq(caller))
                                .and(matching.status.eq(READY))
                )
                .fetch();

        if (findMatching.isEmpty()) {
            log.error(ErrorInfo.MATCHING_NOT_FOUND.getMessage());
            throw new GlobalException(ErrorInfo.MATCHING_NOT_FOUND);
        }
        caller.changeUserStatus(MemberStatus.MATCHING_CONFIRMED);

        return MatchingResponseDto.builder()
                .memberStatus(caller.getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public MatchingInfoDto getMatchingInfo() {
        Optional<User> optional = userRepository.findByUserToken(current.getMemberCredentialToken());
        if (optional.isEmpty()) {
            log.error(ErrorInfo.USER_NOT_FOUND.getMessage());
            throw new GlobalException(ErrorInfo.USER_NOT_FOUND);
        }
        User caller = optional.get();

        List<Matching> findMatching = query.select(matching)
                .from(matching)
                .where(
                        (caller.getGender().equals(M)
                                ? matching.man.eq(caller) : matching.woman.eq(caller))
                                .and(matching.status.eq(READY))
                )
                .fetch();

        if (findMatching.isEmpty()) {
            log.error(ErrorInfo.MATCHING_NOT_FOUND.getMessage());
            throw new GlobalException(ErrorInfo.MATCHING_NOT_FOUND);
        }
        Matching currentMatching = findMatching.get(0);

        User opponent = currentMatching.getMan() == caller
                ? currentMatching.getWoman()
                : currentMatching.getMan();

        MatchingInfoDto matchingInfoDto = MatchingInfoDto.builder()
                .opponentProfileInfo(opponent.getUserProfileInfo())
                .matchingStatus(currentMatching.getStatus())
                .id(currentMatching.getId())
                .build();

        matchingInfoDto.getOpponentProfileInfo()
                .setPhotoData(fileService.getFileData(opponent));

        return matchingInfoDto;
    }
}
