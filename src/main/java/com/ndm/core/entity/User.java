package com.ndm.core.entity;

import com.ndm.core.common.BaseEntity;
import com.ndm.core.common.enums.Gender;
import com.ndm.core.common.enums.MBTI;
import com.ndm.core.common.enums.OAuthCode;
import com.ndm.core.common.enums.MemberStatus;
import com.ndm.core.domain.user.dto.UserProfileDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static com.ndm.core.common.enums.MemberStatus.NEW;
import static com.ndm.core.common.enums.MemberStatus.WAIT;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@TableGenerator(
        name = "users_seq_generator",
        table = "ddu_seq",
        pkColumnName = "sequence_name",
        pkColumnValue = "users_seq",
        allocationSize = 100
)
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "users_seq_generator")
    @Column(name = "user_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(name = "oauth_id", unique = true, length = 40)
    private String oauthId;

    @Column(name = "oauth_code", length = 10)
    @Enumerated(EnumType.STRING)
    private OAuthCode oauthCode;

    @Column(name = "user_token", length = 36, unique = true, nullable = false)
    private String userToken;

    @Column(name = "age", length = 10)
    private String age;

    @Column(name = "address", length = 40)
    private String address;

    @Column(name = "height")
    private Integer height;

    @Column(name = "job", length = 40)
    private String job;

    @Column(name = "ideal_type", length = 400)
    private String idealType;

    @Column(name = "hobby", length = 100)
    private String hobby;

    @Column(name = "mbti", length = 4)
    @Enumerated(EnumType.STRING)
    private MBTI mbti;

    @Column(name = "smoking", columnDefinition = "TINYINT(1)")
    private boolean smoking;

    @Column(name = "self_description", length = 400)
    private String selfDescription;

    @Column(name = "member_status", length = 20)
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Column(name = "gender", length = 1)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "last_login_ip", length = 30)
    private String lastLoginIp;

    @Column(name = "access_token", length = 100)
    private String accessToken;

    @Column(name = "refresh_token", length = 100)
    private String refreshToken;

    // ======== 일대다 매핑 =========

    @OneToMany(mappedBy = "user")
    private List<Friendship> friendships = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<Photo> photos = new ArrayList<>();


    public void updateLoginInfo(String clientIp, String accessToken, String refreshToken) {
        this.lastLoginIp = clientIp;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public void changeUserStatus(MemberStatus memberStatus) {
        this.status = memberStatus;
    }

    public void updateProfileInfo(UserProfileDto userProfileDto) {
        this.gender = userProfileDto.getGender();
        this.age = userProfileDto.getAge();
        this.job = userProfileDto.getJob();
        this.address = userProfileDto.getAddress();
        this.height = userProfileDto.getHeight();
        this.idealType = userProfileDto.getIdealType();
        this.hobby = userProfileDto.getHobby();
        this.mbti = userProfileDto.getMbti();
        this.smoking = userProfileDto.isSmoking();
        this.selfDescription = userProfileDto.getSelfDescription();
    }

    public void registerProfile(UserProfileDto userProfileDto) {
        updateProfileInfo(userProfileDto);
        this.status = WAIT;
    }
}
