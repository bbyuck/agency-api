package com.ndm.core.model;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Current {
    private final HttpServletRequest request;

    public String getUserId() {
        return request.getHeader(HeaderKey.X_LOGIN_ID.key());
    }

    public String getMemberCredentialToken() {
        return request.getHeader(HeaderKey.X_Credential_Token.key());
    }

    public String getClientIp() {
        return request.getHeader("X-FORWARDED-FOR") == null
                ? request.getRemoteAddr()
                : request.getHeader("X-FORWARDED-FOR");
    }
}
