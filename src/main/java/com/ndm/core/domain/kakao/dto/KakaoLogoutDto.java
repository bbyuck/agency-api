package com.ndm.core.domain.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLogoutDto {
    @Builder.Default
    private String targetIdType = "user_id";
    private Long userId; // -> target_id
    private String accessToken;
}
