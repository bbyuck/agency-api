package com.ndm.core.domain.agreement.repository;

import com.ndm.core.common.enums.AgreementCode;
import com.ndm.core.common.enums.OAuthCode;
import com.ndm.core.entity.Agreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgreementRepository extends JpaRepository<Agreement, Long> {

    Optional<Agreement> findByOauthCodeAndOauthId(OAuthCode oauthCode, String oauthId);

    Optional<Agreement> findByCodeAndCredentialToken(AgreementCode agreementCode, String credentialToken);

    Optional<Agreement> findByOauthCodeAndOauthIdAndCode(OAuthCode oauthCode, String oauthId, AgreementCode agreementCode);
}
