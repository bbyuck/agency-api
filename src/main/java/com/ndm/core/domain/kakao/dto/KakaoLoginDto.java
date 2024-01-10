package com.ndm.core.domain.kakao.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoLoginDto {
    private String code;

    private Long kakaoId;
    private String accessToken;
    private String refreshToken;
    private String lastLoginIp;

}
