package com.ndm.core.common.util;

import com.ndm.core.domain.message.dto.WebSocketMessageDto;
import com.ndm.core.model.WebSocketMemberSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ndm.core.common.enums.WebSocketMessageType.*;

@Slf4j
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    public static final Map<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<>();

    private final WebSocketMemberSession webSocketMemberSession;

    private final JSONParser jsonParser;



    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        CLIENTS.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        CLIENTS.remove(session.getId());
        webSocketMemberSession.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage messageString) throws Exception {
        JSONObject message = (JSONObject) jsonParser.parse(messageString.getPayload());
        WebSocketMessageDto webSocketMessageDto = WebSocketMessageDto.builder()
                .credentialToken(message.get("credentialToken").toString())
                .type(valueOf(message.get("type").toString()))
                .build();


        if (webSocketMessageDto.getType() == CONNECT) {
            JSONObject response = new JSONObject();
            response.put("type", CONNECT.name());
            response.put("sessionId", session.getId());
            session.sendMessage(new TextMessage(response.toJSONString()));
        }


        //        CLIENTS.entrySet().forEach( arg->{
//            if(!arg.getKey().equals(sessionId)) {  //같은 아이디가 아니면 메시지를 전달합니다.
//                try {
//                    arg.getValue().sendMessage(messageString);
//                } catch (IOException e) {
//                    log.error(e.getMessage());
//                }
//            }
//        });
    }

}
