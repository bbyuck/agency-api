package com.ndm.core.domain.matchmaker.dto;

import com.ndm.core.common.enums.MemberType;
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

    private final MemberType memberType = MemberType.MATCH_MAKER;


}
