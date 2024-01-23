package com.ndm.core.domain.kakao.dto;

import com.ndm.core.common.enums.MemberType;
import com.ndm.core.common.enums.OAuthCode;
import lombok.Builder;
import lombok.Data;

import static com.ndm.core.common.enums.OAuthCode.KAKAO;

@Data
@Builder
public class KakaoLoginDto {
    private String code;

    private String credentialToken;
    private String accessToken;
    private String refreshToken;
    private MemberType memberType;

}
