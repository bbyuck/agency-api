package com.ndm.core.domain.matching.service;

import com.ndm.core.common.enums.MemberStatus;
import com.ndm.core.common.util.WebSocketHandler;
import com.ndm.core.domain.matching.dto.MatchingRequestDto;
import com.ndm.core.domain.matching.dto.MatchingRequestResultDto;
import com.ndm.core.domain.matching.repository.MatchingRequestRepository;
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
import com.ndm.core.model.WebSocketMemberSession;
import com.ndm.core.model.exception.GlobalException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.ndm.core.common.enums.Gender.M;
import static com.ndm.core.common.enums.MatchingRequestStatus.ACTIVE;
import static com.ndm.core.common.enums.MatchingRequestStatus.CONFIRMED;
import static com.ndm.core.common.enums.WebSocketMessageType.REJECT_REQUEST;
import static com.ndm.core.common.enums.WebSocketMessageType.SEND_REQUEST;
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

    private final Current current;

    private final WebSocketMemberSession webSocketMemberSession;

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
         * 5. websocket 세션 확인
         * 상대방이 있으면 메세지 send
         */
        try {
            String receiverSessionId = webSocketMemberSession.getSessionId(receiver.getUserToken());
            if (receiverSessionId != null) {
                WebSocketSession webSocketSession = WebSocketHandler.CLIENTS.get(receiverSessionId);
                JSONObject response = new JSONObject();
                response.put("type", SEND_REQUEST.name());
                webSocketSession.sendMessage(new TextMessage(response.toJSONString()));
            }
        }
        catch(IOException e) {
            log.error("WebSocket 메세지 전송 중 에러가 발생했습니다.");
            log.error(e.getMessage(), e);
        }

        return MatchingRequestResultDto.builder()
                .memberStatus(sender.getStatus())
                .build();
    }

    public void confirmReceivedRequest() {
        MatchingRequest receivedRequest = getReceivedRequest();
        receivedRequest.confirm();
    }



    @Transactional(readOnly = true)
    public UserProfileDto getReceivedRequestSender() {
        MatchingRequest receivedRequest = getReceivedRequest();
        User sender = receivedRequest.getSender();

        UserProfileDto senderProfileInfo = sender.getUserProfileInfo();
        senderProfileInfo.setMatchingRequestStatus(receivedRequest.getStatus());

        return senderProfileInfo;
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
        try {
            String senderSessionId = webSocketMemberSession.getSessionId(sender.getUserToken());
            if (senderSessionId != null) {
                WebSocketSession webSocketSession = WebSocketHandler.CLIENTS.get(senderSessionId);
                JSONObject response = new JSONObject();
                response.put("type", REJECT_REQUEST.name());
                webSocketSession.sendMessage(new TextMessage(response.toJSONString()));
            }
        }
        catch(IOException e) {
            log.error("WebSocket 메세지 전송 중 에러가 발생했습니다.");
            log.error(e.getMessage(), e);
        }

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

}
