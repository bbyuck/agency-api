package com.ndm.core.entity;

import com.ndm.core.common.BaseEntity;
import com.ndm.core.common.enums.MatchMakerNetworkStatus;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@TableGenerator(
        name = "match_maker_network_seq_generator",
        table = "ddu_seq",
        pkColumnName = "sequence_name",
        pkColumnValue = "match_maker_network_seq",
        allocationSize = 100
)
@Table(name = "match_maker_network")
public class MatchMakerNetwork extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "match_maker_network_seq_generator")
    @Column(name = "match_maker_network_id", unique = true, updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_maker_id_1")
    private MatchMaker matchMaker1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_maker_id_2")
    private MatchMaker matchMaker2;

    @Column(name = "match_maker_network_status", length = 20)
    @Enumerated(EnumType.STRING)
    private MatchMakerNetworkStatus status;
}
