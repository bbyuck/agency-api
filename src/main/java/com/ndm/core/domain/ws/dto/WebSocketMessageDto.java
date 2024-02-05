package com.ndm.core.domain.ws.dto;

import com.ndm.core.common.enums.WebSocketMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessageDto {
    private String credentialToken;
    private WebSocketMessageType type;
}
