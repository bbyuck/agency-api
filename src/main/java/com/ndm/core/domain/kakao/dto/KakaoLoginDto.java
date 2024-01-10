package com.ndm.core.domain.kakao.dto;

import lombok.Data;

@Data
public class KakaoLoginDto {
    private String code;

    private String accessToken;
    private String refreshToken;
}
