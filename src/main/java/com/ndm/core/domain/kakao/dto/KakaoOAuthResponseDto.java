package com.ndm.core.domain.kakao.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class KakaoOAuthResponseDto {
    private String token_type; // 토큰 타입, bearer로 고정
    private String access_token; // 사용자 액세스 토큰 값
    /**
     * ID 토큰 값
     * OpenID Connect 확장 기능을 통해 발급되는 ID 토큰, Base64 인코딩 된 사용자 인증 정보 포함
     *
     * 제공 조건: OpenID Connect가 활성화 된 앱의 토큰 발급 요청인 경우
     * 또는 scope에 openid를 포함한 추가 항목 동의 받기 요청을 거친 토큰 발급 요청인 경우
     */
    private String id_token; // ID 토큰 값
    private int expires_in; // 액세스 토큰 및 ID 토큰 만료 시간
    private String refresh_token; // 사용자 리프레시 토큰 값
    private int refresh_token_expires_in; // 리프레시 토큰 만료 시간(초)
    private String scope;   // scope
    private String app_id;  // client id
    private Long id;        // 유저 ID


    /**
     * error
     */
    private String error;
    private String error_description;
    private String error_code;

}
