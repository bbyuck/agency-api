package com.ndm.core.domain.agreement.service;

import com.ndm.core.common.enums.MatchMakerStatus;
import com.ndm.core.common.enums.UserStatus;
import com.ndm.core.domain.agreement.dto.AgreementDto;
import com.ndm.core.domain.agreement.dto.TempMemberDto;
import com.ndm.core.domain.agreement.repository.AgreementRepository;
import com.ndm.core.domain.matchmaker.repository.MatchMakerRepository;
import com.ndm.core.domain.user.repository.UserRepository;
import com.ndm.core.entity.Agreement;
import com.ndm.core.entity.MatchMaker;
import com.ndm.core.entity.User;
import com.ndm.core.model.Current;
import com.ndm.core.model.ErrorInfo;
import com.ndm.core.model.exception.GlobalException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.ndm.core.entity.QAgreement.agreement;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AgreementService {

    private final JPAQueryFactory query;

    private final AgreementRepository agreementRepository;
    private final UserRepository userRepository;
    private final MatchMakerRepository matchMakerRepository;
    private final Current current;


    /**
     * 동의서 제출
     *
     * @param agreementDto
     * @return
     */
    public TempMemberDto submitAgreement(AgreementDto agreementDto) {
        for (AgreementDto.AgreementInnerDto agreementInnerDto : agreementDto.getAgreements()) {
            if (agreementInnerDto.getAgreementCode() == null) {
                throw new GlobalException(ErrorInfo.AGREEMENT_CODE_DOES_NOT_SELECTED);
            }

            if (!agreementInnerDto.isAgree()) {
                throw new GlobalException(ErrorInfo.DO_NOT_AGREE);
            }

            agreementRepository
                    .findByCodeAndCredentialToken(agreementInnerDto.getAgreementCode(), current.getMemberCredentialToken())
                    .orElseThrow(() -> new GlobalException(ErrorInfo.AGREEMENT_NOT_FOUND))
                    .writeAgreement(agreementInnerDto.getAgreementCode(), agreementInnerDto.isAgree());
        }

        User callerUser = userRepository.findByCredentialToken(current.getMemberCredentialToken())
                .orElseThrow(() -> {
                    log.error(ErrorInfo.USER_NOT_FOUND.getMessage());
                    return new GlobalException(ErrorInfo.USER_NOT_FOUND);
                });

        callerUser.changeUserStatus(UserStatus.NEW);

        MatchMaker callerMatchMaker = matchMakerRepository.findByCredentialToken(current.getMemberCredentialToken())
                .orElseThrow(() -> {
                    log.error(ErrorInfo.MATCHMAKER_NOT_FOUND.getMessage());
                    return new GlobalException(ErrorInfo.MATCHMAKER_NOT_FOUND);
                });
        callerMatchMaker.changeMatchMakerStatus(MatchMakerStatus.NEW);

        return TempMemberDto.builder()
                .oauthCode(agreementDto.getOauthCode())
                .oauthId(agreementDto.getOauthId())
                .userStatus(callerUser.getStatus())
                .matchMakerStatus(callerMatchMaker.getStatus())
                .build();
    }

    /**
     * 필수로 받아야 하는 동의서 코드 목록에 있는 동의서 데이터를 모두 생성한다.
     * 만약 이미 생성되어 있는 동의서가 있을 경우 생성하지 않는다.
     *
     * @param agreementDto
     */
    public void createAgreement(AgreementDto agreementDto) {
        Agreement.ESSENTIAL_LIST.forEach((ESSENTIAL_AGREEMENT) -> {
            agreementRepository
                    .findByOauthCodeAndOauthIdAndCode(agreementDto.getOauthCode(), agreementDto.getOauthId(), ESSENTIAL_AGREEMENT)
                    .ifPresentOrElse(null, () -> {
                        Agreement agreement = Agreement.builder()
                                .oauthCode(agreementDto.getOauthCode())
                                .oauthId(agreementDto.getOauthId())
                                .credentialToken(agreementDto.getCredentialToken())
                                .code(ESSENTIAL_AGREEMENT)
                                .build();
                        agreementRepository.save(agreement);
                    });
        });
    }

    @Transactional(readOnly = true)
    public boolean agreementCreated(AgreementDto agreementDto) {
        Long resultSize = query.select(agreement.count()).from(agreement).distinct()
                .where(agreement.code.in(Agreement.ESSENTIAL_LIST)
                        .and(agreement.oauthId.eq(agreementDto.getOauthId()))
                        .and(agreement.oauthCode.eq(agreementDto.getOauthCode()))).fetchFirst();
        return Math.toIntExact(resultSize) == Agreement.ESSENTIAL_LIST.size();
    }

    @Transactional(readOnly = true)
    public boolean agreeWithAllEssential() {
        List<Agreement> result =
                query.selectFrom(agreement).distinct()
                        .where(agreement.code.in(Agreement.ESSENTIAL_LIST)
                                .and(agreement.agree.eq(true))
                                .and(agreement.credentialToken.eq(current.getMemberCredentialToken()))).fetch();
        return result.size() == Agreement.ESSENTIAL_LIST.size();
    }
}
