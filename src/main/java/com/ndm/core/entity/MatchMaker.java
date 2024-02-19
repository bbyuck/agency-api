package com.ndm.core.entity;

import com.ndm.core.common.BaseEntity;
import com.ndm.core.common.enums.MatchMakerStatus;
import com.ndm.core.common.enums.OAuthCode;
import com.ndm.core.domain.message.dto.FCMTokenDto;
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
@TableGenerator(
        name = "match_maker_seq_generator",
        table = "ddu_seq",
        pkColumnName = "sequence_name",
        pkColumnValue = "match_maker_seq",
        allocationSize = 100
)
public class MatchMaker extends BaseEntity {

    @Id
    @Column(name = "match_maker_id", unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "match_maker_seq_generator")
    private Long id;

    @Column(name = "oauth_id", length = 40)
    private String oauthId;

    @Column(name = "oauth_code", length = 20)
    @Enumerated(EnumType.STRING)
    private OAuthCode oauthCode;

    @Column(name = "credential_token", length = 36, unique = true, nullable = false)
    private String credentialToken;

    @Column(name = "member_status", length = 20)
    @Enumerated(EnumType.STRING)
    private MatchMakerStatus status;

    @Column(name = "last_login_ip", length = 30)
    private String lastLoginIp;

    @Column(name = "fcm_token", length = 200)
    private String fcmToken;

    @Column(name = "access_token", length = 100)
    private String accessToken;

    @Column(name = "refresh_token", length = 100)
    private String refreshToken;

    // ====== 일대다 연관관계 매핑 =====
    @OneToMany(mappedBy = "matchMaker")
    private List<Friendship> friendships = new ArrayList<>();

    // ====== 유저 편의 메서드 =======
//    public void updateJwtToken(TokenInfo tokenInfo) {
//        this.accessToken = tokenInfo.getAccessToken();
//        this.refreshToken = tokenInfo.getRefreshToken();
//    }
    public void updateLoginInfo(String accessToken, String refreshToken, String lastLoginIp) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.lastLoginIp = lastLoginIp;
    }

    public void changeMatchMakerStatus(MatchMakerStatus matchMakerStatus) {
        this.status = matchMakerStatus;
    }

    public void registerFCMToken(FCMTokenDto token) {
        this.fcmToken = token.getValue();
    }
}
