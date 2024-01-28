package com.ndm.core.domain.agreement.dto;

import com.ndm.core.common.enums.AgreementCode;
import com.ndm.core.common.enums.OAuthCode;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AgreementDto {
    private OAuthCode oauthCode;
    private String oauthId;
    private List<AgreementInnerDto> agreements;

    @Data
    @Builder
    public static class AgreementInnerDto {
        private boolean agree;
        private AgreementCode agreementCode;
    }
}
