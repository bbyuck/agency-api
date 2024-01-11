package com.ndm.core.entity;

import com.ndm.core.status.MatchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "match")
public class Match {

    @Id @GeneratedValue
    @Column(name = "match_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "man_id")
    private User man;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "woman_id")
    private User woman;

    @Column(name = "status", length = 20)
    private MatchStatus status;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "requested_by")
    private Long requestedBy;

    @Column(name = "photo_exchange")
    private boolean photoExchange;

    @Column(name = "match_date")
    private LocalDateTime matchDate;

    private int step;
}
