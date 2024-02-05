package com.ndm.core.domain.ws;

import com.ndm.core.common.util.WebSocketHandler;
import com.ndm.core.domain.ws.dto.WebSocketSendDto;
import com.ndm.core.domain.ws.dto.WebSocketSessionRegisterDto;
import com.ndm.core.model.Response;
import com.ndm.core.model.Trace;
import com.ndm.core.model.WebSocketMemberSession;
import com.ndm.core.model.version.V1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static com.ndm.core.common.enums.WebSocketMessageType.SEND_REQUEST;

@V1
@Slf4j
@RestController
@RequiredArgsConstructor
public class WebSocketController {

    private final WebSocketMemberSession webSocketMemberSession;


    @Trace
    @PostMapping("/ws/session/register")
    public Response<String> registerWebSocketSession(@RequestBody WebSocketSessionRegisterDto requestDto) {
        webSocketMemberSession.put(requestDto.getSessionId(), requestDto.getCredentialToken());
        return Response.<String>builder()
                .build();
    }

    @Trace
    @PostMapping("/ws/send")
    public Response<String> send(@RequestBody WebSocketSendDto requestDto) {
        try {
            String receiverSessionId = webSocketMemberSession.getSessionId(requestDto.getReceiverCredentialToken());
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
        return Response.<String>builder().build();
    }


}
