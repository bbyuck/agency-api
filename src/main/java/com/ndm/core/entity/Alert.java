package com.ndm.core.entity;

import com.ndm.core.status.TargetType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "alert")
public class Alert {

    @Id @GeneratedValue
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
