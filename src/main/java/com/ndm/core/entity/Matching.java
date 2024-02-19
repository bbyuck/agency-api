package com.ndm.core.entity;

import com.ndm.core.common.BaseEntity;
import com.ndm.core.common.enums.MatchingStatus;
import com.ndm.core.domain.matching.dto.MatchingRelation;
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

    @ManyToOne
    @JoinColumn(name = "man_id")
    private User man;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "man_match_maker_id")
    private MatchMaker manMatchMaker;

    @ManyToOne
    @JoinColumn(name = "woman_id")
    private User woman;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "woman_match_maker_id")
    private MatchMaker womanMatchMaker;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matching_request_id")
    private MatchingRequest matchingRequest;

    @Column(name = "matching_status", length = 20)
    @Enumerated(EnumType.STRING)
    private MatchingStatus status;

    @Column(name = "requester_id")
    private Long requesterId;

    @Column(name = "matching_date")
    private LocalDateTime matchingDate;


    // ============== 유저 편의 메서드 =================
    public void success() {
        this.status = MatchingStatus.SUCCESS;
    }

    public void cancel() {
        this.status = MatchingStatus.CANCEL;
    }

    public MatchingRelation getRelation(String callersToken) {
        User caller = getMan().getCredentialToken().equals(callersToken) ? getMan() : getWoman();
        return MatchingRelation.builder()
                .caller(caller)
                .opponent(getMan() == caller ? getWoman() : getMan())
                .build();
    }
}
