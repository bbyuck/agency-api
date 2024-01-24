package com.ndm.core.domain.user.dto;

import com.ndm.core.common.enums.MemberType;
import com.ndm.core.common.enums.OAuthCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    private String matchMakerCode;

    private String credentialToken;

    private String accessToken;

    private String refreshToken;

    private final MemberType memberType = MemberType.USER;

}
