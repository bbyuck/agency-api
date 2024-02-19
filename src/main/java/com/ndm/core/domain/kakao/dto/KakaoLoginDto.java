package com.ndm.core.domain.kakao.dto;

import com.ndm.core.common.enums.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoLoginDto {
    private String code;

    private String credentialToken;
    private String accessToken;
    private String refreshToken;
    private UserStatus userStatus;
    private MatchMakerStatus matchMakerStatus;

    /**
     * 이용동의서용
     * 반드시 암호화해서 return
     */
    private OAuthCode oauthCode;
    private String oauthId;
}
