package com.ndm.core.domain.matchmaker.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchMakerDto {

    private Long kakaoId;

    private String accessToken;

    private String refreshToken;

    private String lastLoginIp;

}
