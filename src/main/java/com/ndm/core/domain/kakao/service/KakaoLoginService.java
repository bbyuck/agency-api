package com.ndm.core.domain.kakao.service;

import com.ndm.core.domain.kakao.dto.KakaoLoginDto;
import com.ndm.core.domain.kakao.dto.KakaoOAuthResponseDto;
import com.ndm.core.domain.kakao.dto.KakaoUserInfoResponseDto;
import com.ndm.core.model.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import static com.ndm.core.model.ErrorInfo.INVALID_TOKEN;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Slf4j
@Service
public class KakaoLoginService {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public void kakaoLogin(KakaoLoginDto loginDto) {
        if (loginDto.getAccessToken() == null || loginDto.getAccessToken().isEmpty()) {
            KakaoOAuthResponseDto kakaoOAuthResponseDto = requestOAuthToken(loginDto.getCode());
            loginDto.setAccessToken(kakaoOAuthResponseDto.getAccess_token());
            loginDto.setRefreshToken(kakaoOAuthResponseDto.getRefresh_token());
        }

        requestUserInfo(loginDto.getAccessToken());
    }

    private KakaoOAuthResponseDto requestOAuthToken(String authorizationCode) {
        log.info("requestOAuthToken(authorizationCode) ====== {}", authorizationCode);
        if (authorizationCode == null || authorizationCode.isEmpty()) {
            // 카카오 authorization code가 없음
            throw new GlobalException(INVALID_TOKEN);
        }
        RestClient restClient = RestClient
                .builder()
                .messageConverters(converters -> converters.add(new FormHttpMessageConverter()))
                .build();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", kakaoApiKey);
        requestBody.add("redirect_uri", "http://localhost:3000/auth");
        requestBody.add("code", authorizationCode);

        KakaoOAuthResponseDto kakaoOAuthResponseDto = restClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(APPLICATION_FORM_URLENCODED)
                .body(requestBody)
                .retrieve()
                .body(KakaoOAuthResponseDto.class);

        log.debug("Kakao OAuth response ====== {}", kakaoOAuthResponseDto);

        return kakaoOAuthResponseDto;
    }

    public void requestUserInfo(String accessToken) {
        log.info("requestUserInfo(accessToken) ====== {}", accessToken);

        RestClient restClient = RestClient
                .builder()
                .messageConverters(converters -> converters.add(new FormHttpMessageConverter()))
                .build();

        KakaoUserInfoResponseDto userInfo = restClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer" + " " + accessToken)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .body(KakaoUserInfoResponseDto.class);

        System.out.println("userInfo = " + userInfo);
    }
}
