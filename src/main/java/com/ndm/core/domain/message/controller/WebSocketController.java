package com.ndm.core.domain.message.controller;

import com.ndm.core.common.util.WebSocketHandler;
import com.ndm.core.domain.message.dto.WebSocketSendDto;
import com.ndm.core.domain.message.dto.WebSocketSessionRegisterDto;
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

}
