package com.ndm.core.domain.user.dto;

import com.ndm.core.common.enums.MemberCode;
import com.ndm.core.common.enums.OAuthCode;
import com.ndm.core.common.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    private String matchMakerCode;

    private String credentialToken;

    private String accessToken;

    private String refreshToken;

    private UserStatus userStatus;


    private final MemberCode memberCode = MemberCode.USER;

    private OAuthCode oauthCode;

    private String oauthId;

}
