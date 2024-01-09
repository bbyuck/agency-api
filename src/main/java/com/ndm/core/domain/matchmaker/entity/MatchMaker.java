package com.ndm.core.domain.matchmaker.entity;

import com.ndm.core.common.BaseEntity;
import com.ndm.core.domain.matchmaker.dto.TokenInfo;
import com.ndm.core.model.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "match_maker")
public class MatchMaker extends BaseEntity {

    @Id
    @Column(name = "match_maker_id", unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", unique = true, nullable = false, updatable = false)
    private String loginId;

    @Column(name = "password")
    private String password;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    // ====== 유저 편의 메서드 =======
    public void updateJwtToken(TokenInfo tokenInfo) {
        this.accessToken = tokenInfo.getAccessToken();
        this.refreshToken = tokenInfo.getRefreshToken();
    }

    public void updateLastLoginIp(String loginIp) {
        this.lastLoginIp = loginIp;
    }
}
