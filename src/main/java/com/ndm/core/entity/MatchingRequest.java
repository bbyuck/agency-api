package com.ndm.core.entity;

import com.ndm.core.common.BaseEntity;
import com.ndm.core.common.enums.MatchingRequestStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@TableGenerator(
        name = "matching_request_seq_generator",
        table = "ddu_seq",
        pkColumnName = "sequence_name",
        pkColumnValue = "matching_request_seq",
        allocationSize = 100
)
@Table(name = "matching_request")
public class MatchingRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "matching_request_seq_generator")
    @Column(name = "matching_request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(name = "matching_request_status", length = 20)
    @Enumerated(EnumType.STRING)
    private MatchingRequestStatus status;


    // ========= 유저 편의 메소드 ============
    public void reject() {
        this.status = MatchingRequestStatus.REJECTED;
    }
}
