package com.ndm.core.domain.agreement.dto;

import com.ndm.core.common.enums.MemberCode;
import com.ndm.core.common.enums.MemberStatus;
import com.ndm.core.common.enums.OAuthCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TempMemberDto {
    private String oauthId;
    private OAuthCode oauthCode;
    private MemberCode memberCode;
    private MemberStatus memberStatus;
}
