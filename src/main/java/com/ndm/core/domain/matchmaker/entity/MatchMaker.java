package com.ndm.core.domain.matchmaker.entity;

import com.ndm.core.common.BaseEntity;
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

    @Column(name = "kakao_id", unique = true, nullable = false)
    private Long kakaoId;

    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    // ====== 유저 편의 메서드 =======
//    public void updateJwtToken(TokenInfo tokenInfo) {
//        this.accessToken = tokenInfo.getAccessToken();
//        this.refreshToken = tokenInfo.getRefreshToken();
//    }
    public void updateLastLoginIp(String loginIp) {
        this.lastLoginIp = loginIp;
    }
}
