package com.ndm.core.entity;

import com.ndm.core.common.BaseEntity;
import com.ndm.core.status.FriendStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "friend")
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "friend_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_maker_id", nullable = false)
    private MatchMaker matchMaker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private FriendStatus status;
}
