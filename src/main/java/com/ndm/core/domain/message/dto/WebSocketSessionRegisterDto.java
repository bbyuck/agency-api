package com.ndm.core.domain.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class WebSocketSessionRegisterDto {

    private String credentialToken;
    private String sessionId;
}
