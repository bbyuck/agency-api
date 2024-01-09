package com.ndm.core.filter;

import com.ndm.core.domain.matchmaker.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.ndm.core.model.HeaderKey.X_LOGIN_ID;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Request Header에서 JWT 토큰 / username 추출
        String accessToken = resolveToken(request);
        String username = request.getHeader(X_LOGIN_ID.key());

        // 인증
        authService.authentication(accessToken, username);

        chain.doFilter(request, response);
    }

    // Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
