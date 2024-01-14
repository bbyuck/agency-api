package com.ndm.core.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    private Long kakaoId;

    private String accessToken;

    private String refreshToken;

    private String lastLoginIp;

    private String matchMakerName;
}
