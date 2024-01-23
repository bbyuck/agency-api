package com.ndm.core.domain.kakao.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IdToken {
    private IdTokenHeader header;
    private IdTokenPayload payload;
    private String signature;
}
