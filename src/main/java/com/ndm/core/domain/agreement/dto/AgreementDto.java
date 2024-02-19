package com.ndm.core.domain.agreement.dto;

import com.ndm.core.common.enums.AgreementCode;
import com.ndm.core.common.enums.OAuthCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgreementDto {
    private OAuthCode oauthCode;
    private String oauthId;
    private String credentialToken;
    private List<AgreementInnerDto> agreements;

    @Data
    @Builder
    public static class AgreementInnerDto {
        private boolean agree;
        private AgreementCode agreementCode;
    }
}
