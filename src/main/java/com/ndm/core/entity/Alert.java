package com.ndm.core.entity;

import com.ndm.core.common.BaseEntity;
import com.ndm.core.common.enums.TargetType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@TableGenerator(
        name = "alert_seq_generator",
        table = "ddu_seq",
        pkColumnName = "sequence_name",
        pkColumnValue = "alert_seq",
        allocationSize = 100
)
@Table(name = "alert")
public class Alert extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.TABLE, generator = "alert_seq_generator")
    @Column(name = "alert_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(name = "match_id")
    private Long match_id;

    @Column(name = "target_type", length = 20)
    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    @Column(name = "target_id")
    private Long targetId;
}
