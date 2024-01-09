package com.ndm.core.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HeaderKey {
    X_LOGIN_ID("로그인 User ID"),
    X_PASSWORD("로그인 User password"),
    X_ACCESS_TOKEN("클라이언트에 저장된 Access Token"),
    X_REFRESH_TOKEN("클라이언트에 저장된 Refresh Token"),
    Authorization("Spring security jwt Authorization header");

    private final String description;

    public String key() {
        return this.name().replace("_", "-");
    }

    public static String customHeaders() {
        StringBuilder headerBuilder = new StringBuilder();
        for (HeaderKey header : HeaderKey.values()) {
            headerBuilder.append(", ").append(header.key());
        }

        return headerBuilder.toString();
    }

}
