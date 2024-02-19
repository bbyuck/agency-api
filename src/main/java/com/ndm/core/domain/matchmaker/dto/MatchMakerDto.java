package com.ndm.core.domain.matchmaker.dto;

import com.ndm.core.common.enums.MatchMakerStatus;
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

    private MatchMakerStatus matchMakerStatus;



    private OAuthCode oauthCode;
    private String oauthId;

}
