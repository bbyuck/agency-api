package com.ndm.core.entity;

import com.ndm.core.common.BaseEntity;
import com.ndm.core.common.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@TableGenerator(
        name = "friendship_seq_generator",
        table = "ddu_seq",
        pkColumnName = "sequence_name",
        pkColumnValue = "friendship_seq",
        allocationSize = 100
)
@Table(name = "friendship")
public class Friendship extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "friendship_seq_generator")
    @Column(name = "friendship_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_maker_id", nullable = false)
    private MatchMaker matchMaker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "friendship_status", length = 20)
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    // === 편의 메서드 ===
    public void makeFriends(MatchMaker matchMaker, User user) {
        this.matchMaker = matchMaker;
        this.user = user;

        matchMaker.getFriendships().add(this);
        user.getFriendships().add(this);
    }

}
