package com.ndm.core.entity;

import com.ndm.core.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "match_maker_name", unique = true, nullable = false, updatable = false, length = 40)
    private String name;

    @Column(name = "last_login_ip", length = 30)
    private String lastLoginIp;

    // ====== 일대다 연관관계 매핑 =====
    @OneToMany(mappedBy = "matchMaker")
    private List<Friend> friends = new ArrayList<>();

    // ====== 유저 편의 메서드 =======
//    public void updateJwtToken(TokenInfo tokenInfo) {
//        this.accessToken = tokenInfo.getAccessToken();
//        this.refreshToken = tokenInfo.getRefreshToken();
//    }
    public void updateLastLoginIp(String loginIp) {
        this.lastLoginIp = loginIp;
    }
}
