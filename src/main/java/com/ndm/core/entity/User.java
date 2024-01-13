package com.ndm.core.entity;

import com.ndm.core.common.BaseEntity;
import com.ndm.core.common.enums.Gender;
import com.ndm.core.common.enums.MBTI;
import com.ndm.core.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "kakao_id", unique = true, nullable = false)
    private Long kakaoId;

    @Column(name = "age")
    private Integer age;

    @Column(name = "address", length = 40)
    private String address;

    @Column(name = "height")
    private Integer height;

    @Column(name = "ideal_type", length = 200)
    private String idealType;

    @Column(name = "hobby", length = 100)
    private String hobby;

    @Column(name = "mbti", length = 4)
    @Enumerated(EnumType.STRING)
    private MBTI mbti;

    @Column(name = "self_description", length = 300)
    private String selfDescription;

    @Column(name = "allow_photo_exchange", columnDefinition = "TINYINT(1)")
    private boolean allowPhotoExchange;

    @Column(name = "user_status", length = 20)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "gender", length = 1)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // ======== 일대다 매핑 =========

    @OneToMany(mappedBy = "user")
    private List<Friend> friends = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<Photo> photos = new ArrayList<>();

}
