package com.ndm.core.entity;

import com.ndm.core.common.BaseEntity;
import com.ndm.core.common.enums.FriendStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@TableGenerator(
        name = "friend_seq_generator",
        table = "ddu_seq",
        pkColumnName = "sequence_name",
        pkColumnValue = "friend_seq",
        allocationSize = 100
)
@Table(name = "friend")
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "friend_seq_generator")
    @Column(name = "friend_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_maker_id", nullable = false)
    private MatchMaker matchMaker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "friend_status", length = 20)
    @Enumerated(EnumType.STRING)
    private FriendStatus status;
}
