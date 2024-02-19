package com.ndm.core.domain.agreement.dto;

import com.ndm.core.common.enums.MatchMakerStatus;
import com.ndm.core.common.enums.OAuthCode;
import com.ndm.core.common.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TempMemberDto {
    private String oauthId;
    private OAuthCode oauthCode;
    private UserStatus userStatus;
    private MatchMakerStatus matchMakerStatus;
}
