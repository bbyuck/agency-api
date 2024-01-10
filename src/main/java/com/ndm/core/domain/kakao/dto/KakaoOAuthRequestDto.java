package com.ndm.core.domain.kakao.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoOAuthRequestDto {
    private String grant_type;
    private String client_id;
    private String redirect_uri;
    private String code;
    private String client_secret;
}
