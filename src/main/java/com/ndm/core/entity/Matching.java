package com.ndm.core.entity;

import com.ndm.core.common.BaseEntity;
import com.ndm.core.common.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@TableGenerator(
        name = "matching_seq_generator",
        table = "ddu_seq",
        pkColumnName = "sequence_name",
        pkColumnValue = "matching_seq",
        allocationSize = 100
)
@Table(name = "matching")
public class Matching extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "matching_seq_generator")
    @Column(name = "matching_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "man_id")
    private User man;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "woman_id")
    private User woman;

    @Column(name = "matching_status", length = 20)
    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "requested_by")
    private Long requester_id;

    @Column(name = "photo_exchange", columnDefinition = "TINYINT(1)")
    private boolean photoExchange;

    @Column(name = "match_date")
    private LocalDateTime matchDate;

    @Column(name = "step")
    private Integer step;
}
