package com.ndm.core.domain.agreement.service;

import com.ndm.core.common.enums.MemberCode;
import com.ndm.core.common.enums.MemberStatus;
import com.ndm.core.common.enums.OAuthCode;
import com.ndm.core.common.util.RSACrypto;
import com.ndm.core.domain.agreement.dto.AgreementDto;
import com.ndm.core.domain.agreement.dto.TempMemberDto;
import com.ndm.core.domain.agreement.repository.AgreementRepository;
import com.ndm.core.entity.Agreement;
import com.ndm.core.model.ErrorInfo;
import com.ndm.core.model.Trace;
import com.ndm.core.model.exception.GlobalException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.ndm.core.entity.QAgreement.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AgreementService {

    private final JPAQueryFactory query;

    private final AgreementRepository agreementRepository;
    private final RSACrypto rsaCrypto;

    public TempMemberDto submitAgreement(AgreementDto agreementDto) {
        checkUserAuth(agreementDto);
        String oauthId = null;

        try {
            oauthId = rsaCrypto.decrypt(agreementDto.getOauthId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GlobalException(ErrorInfo.INVALID_CREDENTIAL_TOKEN);
        }

        for (AgreementDto.AgreementInnerDto agreementInnerDto : agreementDto.getAgreements()) {
            if (agreementInnerDto.getAgreementCode() == null) {
                throw new GlobalException(ErrorInfo.AGREEMENT_CODE_DOES_NOT_SELECTED);
            }

            if (!agreementInnerDto.isAgree()) {
                throw new GlobalException(ErrorInfo.DO_NOT_AGREE);
            }

            Optional<Agreement> optional = agreementRepository.findByOauthCodeAndOauthIdAndCode(agreementDto.getOauthCode(), oauthId, agreementInnerDto.getAgreementCode());
            if (optional.isEmpty()) {
                throw new GlobalException(ErrorInfo.AGREEMENT_NOT_FOUND);
            }

            Agreement agreement = optional.get();
            agreement.writeAgreement(agreementInnerDto.getAgreementCode(), agreementInnerDto.isAgree());
        }

        try {
            return TempMemberDto.builder()
                    .oauthCode(agreementDto.getOauthCode())
                    .oauthId(agreementDto.getOauthId())
                    .memberCode(MemberCode.TEMP)
                    .memberStatus(MemberStatus.NEW)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GlobalException(ErrorInfo.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 필수로 받아야 하는 동의서 코드 목록에 있는 동의서 데이터를 모두 생성한다.
     *
     * @param agreementDto
     */
    public void createAgreement(AgreementDto agreementDto) {
        checkUserAuth(agreementDto);

        Agreement.ESSENTIAL_LIST.forEach((ESSENTIAL_AGREEMENT) -> {
            Optional<Agreement> optional = agreementRepository.findByOauthCodeAndOauthIdAndCode(agreementDto.getOauthCode(), agreementDto.getOauthId(), ESSENTIAL_AGREEMENT);
            if (optional.isPresent()) {
                return;
            }

            Agreement agreement = Agreement.builder()
                    .oauthCode(agreementDto.getOauthCode())
                    .oauthId(agreementDto.getOauthId())
                    .code(ESSENTIAL_AGREEMENT)
                    .build();
            agreementRepository.save(agreement);
        });
    }

    @Transactional(readOnly = true)
    public boolean agreeWithAllEssential(OAuthCode oauthCode, String decryptedOauthId) {
        List<Agreement> result =
                query.selectFrom(agreement).distinct()
                        .where(agreement.code.in(Agreement.ESSENTIAL_LIST)
                                .and(agreement.agree.eq(true))
                                .and(agreement.oauthId.eq(decryptedOauthId))
                                .and(agreement.oauthCode.eq(oauthCode))).fetch();
        return result.size() == Agreement.ESSENTIAL_LIST.size();
    }

    @Transactional(readOnly = true)
    public boolean agreeWithAllEssential(AgreementDto agreementDto) {
        OAuthCode oauthCode = agreementDto.getOauthCode();
        String decryptedOauthId;

        try {
            decryptedOauthId = rsaCrypto.decrypt(agreementDto.getOauthId());
        }
        catch(Exception e) {
            log.error("oauth id 복호화에 실패했습니다.");
            log.error(e.getMessage(), e);
            throw new GlobalException(ErrorInfo.INTERNAL_SERVER_ERROR);
        }

        List<Agreement> result =
                query.selectFrom(agreement).distinct()
                        .where(agreement.code.in(Agreement.ESSENTIAL_LIST)
                                .and(agreement.agree.eq(true))
                                .and(agreement.oauthId.eq(decryptedOauthId))
                                .and(agreement.oauthCode.eq(oauthCode))).fetch();
        return result.size() == Agreement.ESSENTIAL_LIST.size();
    }

    private static void checkUserAuth(AgreementDto agreementDto) {
        if (agreementDto.getOauthCode() == null
                || !StringUtils.hasText(agreementDto.getOauthId())) {
            throw new GlobalException(ErrorInfo.INVALID_OAUTH_ID);
        }
    }

//    @Transactional(readOnly = true)
//    public AgreementDto findCallersAgreement(AgreementDto agreementDto) {
//        checkUserAuth(agreementDto);
//        Optional<Agreement> optional;
//        try {
//            optional = agreementRepository.findByOauthCodeAndOauthId(agreementDto.getOauthCode(), rsaCrypto.decrypt(agreementDto.getOauthId()));
//        }
//        catch (Exception e) {
//            log.error(ErrorInfo.INTERNAL_SERVER_ERROR.getMessage());
//            throw new GlobalException(ErrorInfo.INTERNAL_SERVER_ERROR);
//        }
//        if (optional.isEmpty()) {
//            log.error("생성된 동의서가 없습니다.");
//            throw new GlobalException(ErrorInfo.INTERNAL_SERVER_ERROR);
//        }
//
//        Agreement callersAgreement = optional.get();
//    }
}
