package com.ndm.core.common.util;

import com.ndm.core.domain.kakao.dto.JWKDto;
import com.ndm.core.model.exception.GlobalException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import static com.ndm.core.model.ErrorInfo.*;

@Slf4j
@RequiredArgsConstructor
public class OIDCHelper {

    private final JWKManager jwkManager;

    private final String KAKAO_TOKEN_ISSUER = "https://kauth.kakao.com";

    @Value("${kakao.key.rest}")
    private String kakaoRestKey;

    private String getUnsignedToken(String token) {
        String[] splitToken = token.split("\\.");
        if (splitToken.length != 3) {
            throw new GlobalException(INVALID_TOKEN);
        }
        return splitToken[0] + "." + splitToken[1] + ".";
    }

    private String getPublicKeyId(String token) {
        Jwt<Header, Claims> unsignedTokenClaims = getUnsignedTokenClaims(token, KAKAO_TOKEN_ISSUER, kakaoRestKey);
        return (String) unsignedTokenClaims.getHeader().get("kid");
    }
    private Jwt<Header, Claims> getUnsignedTokenClaims(String token, String iss, String aud) {
        try {
            return Jwts.parserBuilder()
                    .requireAudience(aud) //aud(두둥 카카오톡 어플리케이션 아이디) 가 같은지 확인
                    .requireIssuer(iss)
                    .build()
                    .parseClaimsJwt(getUnsignedToken(token));
        } catch (ExpiredJwtException e) { //파싱하면서 만료된 토큰인지 확인.
            log.error(e.getMessage(), e);
            throw new GlobalException(ID_TOKEN_EXPIRED);
        } catch (Exception e) {
            log.error(e.toString());
            throw new GlobalException(INVALID_TOKEN);
        }
    }

    public Jws<Claims> getSignedOIDCTokenJws(String token) {
        String kid = getPublicKeyId(token);
        JWKDto jwkDto = jwkManager.getKeys().get(kid);
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwkManager.getRSAPublicKey(jwkDto.getN(), jwkDto.getE()))
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new GlobalException(ACCESS_TOKEN_EXPIRED);
        } catch (Exception e) {
            log.error(e.toString());
            throw new GlobalException(INVALID_TOKEN);
        }
    }
}
