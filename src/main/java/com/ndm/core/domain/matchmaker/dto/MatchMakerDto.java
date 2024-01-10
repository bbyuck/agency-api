package com.ndm.core.domain.matchmaker.dto;

import com.ndm.core.model.Role;
import lombok.*;

import java.time.LocalDateTime;

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
