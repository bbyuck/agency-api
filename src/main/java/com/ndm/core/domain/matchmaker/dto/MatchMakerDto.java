package com.ndm.core.domain.matchmaker.dto;

import com.ndm.core.common.enums.MemberStatus;
import com.ndm.core.common.enums.MemberCode;
import com.ndm.core.common.enums.OAuthCode;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchMakerDto {

    private String credentialToken;

    private String accessToken;

    private String refreshToken;

    private final MemberCode memberCode = MemberCode.MATCH_MAKER;

    private MemberStatus memberStatus;



    private OAuthCode oauthCode;
    private String oauthId;

}
