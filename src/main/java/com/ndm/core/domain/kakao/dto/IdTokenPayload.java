package com.ndm.core.domain.kakao.dto;

import lombok.Data;

@Data
public class IdTokenPayload {
    private String iss;
    private String aud;
    private String sub;
    private Long iat;
    private Long auth_time;
    private Long exp;
    private String nonce;

    /*
    private String nickname;
    private String picture;
    private String email;
    */
}
