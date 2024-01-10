package com.ndm.core.domain.kakao.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoLogoutDto {
    private String targetIdType = "user_id";
    private Long userId; // -> target_id
    private String accessToken;
}
