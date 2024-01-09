package com.ndm.core.domain.matchmaker.dto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.*;

import static com.ndm.core.model.HeaderKey.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    private String loginId;
    private String password;


    public static LoginDto parseLoginInfo(HttpServletRequest request) {
        return LoginDto
                .builder()
                .loginId(request.getHeader(X_LOGIN_ID.key()))
                .password(request.getHeader(X_PASSWORD.key()))
                .build();
    }
}
