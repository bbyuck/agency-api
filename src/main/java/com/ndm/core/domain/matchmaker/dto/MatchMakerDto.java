package com.ndm.core.domain.matchmaker.dto;

import com.ndm.core.model.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchMakerDto {
    private Long id;

    private String loginId;

    private String password;

    private String passwordConfirm;

    private String accessToken;

    private String refreshToken;

    private Role role;
}
