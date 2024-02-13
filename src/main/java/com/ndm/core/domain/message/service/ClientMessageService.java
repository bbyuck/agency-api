package com.ndm.core.domain.message.service;

import com.ndm.core.common.enums.ClientMessageCode;
import com.ndm.core.common.util.WebSocketHandler;
import com.ndm.core.entity.User;
import com.ndm.core.model.WebSocketMemberSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientMessageService {

    private final FCMService fcmService;
    private final WebSocketMemberSession webSocketMemberSession;


    public void sendMessageForUser(ClientMessageCode clientMessageCode, User target) {
        String receiverSessionId = webSocketMemberSession.getSessionId(target.getUserToken());
        if (receiverSessionId != null) {
            try {

                WebSocketSession webSocketSession = WebSocketHandler.CLIENTS.get(receiverSessionId);
                JSONObject response = new JSONObject();
                response.put("type", clientMessageCode.name());
                webSocketSession.sendMessage(new TextMessage(response.toJSONString()));
            } catch (IOException e) {
                log.error("WebSocket 메세지 전송 중 에러가 발생했습니다.");
                log.error(e.getMessage(), e);
            }
        } else {
            fcmService.sendNotificationForUser(clientMessageCode, target);
        }
    }
}
