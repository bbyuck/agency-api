package com.ndm.core.entity;

import com.ndm.core.common.BaseEntity;
import com.ndm.core.common.enums.AgreementCode;
import com.ndm.core.common.enums.MemberCode;
import com.ndm.core.common.enums.OAuthCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.List;

import static com.ndm.core.common.enums.AgreementCode.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@TableGenerator(
        name = "agreement_seq_generator",
        table = "ddu_seq",
        pkColumnName = "sequence_name",
        pkColumnValue = "agreement_seq",
        allocationSize = 100
)
@Table(name = "agreement")
public class Agreement extends BaseEntity {
    @Transient
    public static final List<AgreementCode> ESSENTIAL_LIST = Arrays.asList(PERSONAL_INFORMATION_USE, DISCLAIMER);

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "agreement_seq_generator")
    @Column(name = "agreement_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "member_code", length = 20)
    @Enumerated(EnumType.STRING)
    private MemberCode memberCode;

    @Column(name = "oauth_id", length = 40)
    private String oauthId;

    @Column(name = "oauth_code", length = 20)
    @Enumerated(EnumType.STRING)
    private OAuthCode oauthCode;

    @Column(name = "agree", columnDefinition="TINYINT(1)")
    private boolean agree;

    @Column(name = "agreement_code", length = 20)
    @Enumerated(EnumType.STRING)
    private AgreementCode code;


    public void writeAgreement(AgreementCode code, boolean agree) {
        this.code = code;
        this.agree = agree;
    }

    public void mappingTo(MemberCode memberCode, Long memberId) {
        this.memberCode = memberCode;
        this.memberId = memberId;
    }
}
