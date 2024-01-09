package com.ndm.core.domain.matchmaker.dto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;

import static com.ndm.core.model.HeaderKey.X_ACCESS_TOKEN;
import static com.ndm.core.model.HeaderKey.X_REFRESH_TOKEN;

@Data
@Builder
public class TokenInfo {
    @Builder.Default
    private String grantType = "Bearer";
    private String accessToken;
    private String refreshToken;

    public static TokenInfo parseClientTokenInfo(HttpServletRequest request) {
        return TokenInfo
                .builder()
                .accessToken(request.getHeader(X_ACCESS_TOKEN.key()))
                .refreshToken(request.getHeader(X_REFRESH_TOKEN.key()))
                .build();
    }
}
