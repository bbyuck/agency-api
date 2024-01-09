package com.ndm.core.config;

import com.ndm.core.domain.matchmaker.service.AuthService;
import com.ndm.core.common.exception.FilterExceptionHandler;
import com.ndm.core.filter.JwtAuthenticationFilter;
import com.ndm.core.security.JwtAccessDeniedHandler;
import com.ndm.core.security.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private final AuthService authService;

    @Value("${jwt.white-list}")
    private List<String> whiteList;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                whiteList
                                        .stream()
                                        .map(AntPathRequestMatcher::antMatcher)
                                        .toArray(AntPathRequestMatcher[]::new))
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(authService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new FilterExceptionHandler(), JwtAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
