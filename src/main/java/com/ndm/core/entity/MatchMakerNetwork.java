package com.ndm.core.entity;

import com.ndm.core.common.BaseEntity;
import com.ndm.core.status.MatchMakerNetworkStatus;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "match_maker_network")
public class MatchMakerNetwork extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "match_maker_network_id", unique = true, updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_maker_id_1")
    private MatchMaker matchMaker1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_maker_id_2")
    private MatchMaker matchMaker2;

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private MatchMakerNetworkStatus status;
}
