package com.ndm.core.common.util;

import com.ndm.core.cache.Cache;
import com.ndm.core.domain.kakao.dto.JWKDto;
import com.ndm.core.domain.kakao.dto.JWKListDto;
import com.ndm.core.model.ErrorInfo;
import com.ndm.core.model.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class JWKManager {

    private final Cache cache;
    public static final String CACHE_KEY = "KAKAO_PUBLIC_KEYS";

    public Map<String, JWKDto> getKeys() {
        if (cache.get(JWKManager.CACHE_KEY) == null) {
            // cache not hit
            log.info("getKeys API Call ======");

            RestClient restClient = RestClient
                    .builder()
                    .messageConverters(converters -> converters.add(new FormHttpMessageConverter()))
                    .build();

            Map<String, JWKDto> jwkMap = new HashMap<>();
            List<JWKDto> keys = Objects.requireNonNull(restClient.get()
                    .uri("https://kauth.kakao.com/.well-known/jwks.json")
                    .retrieve()
                    .body(JWKListDto.class)).getKeys();

            for (JWKDto jwkDto : keys) {
                jwkMap.put(jwkDto.getKid(), jwkDto);
            }

            cache.add(CACHE_KEY, jwkMap);
        }
        return (Map<String, JWKDto>) cache.get(JWKManager.CACHE_KEY);
    }

    public Key getRSAPublicKey(String modulus, String exponent) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decodeN = Base64.getUrlDecoder().decode(modulus);
            byte[] decodeE = Base64.getUrlDecoder().decode(exponent);
            BigInteger n = new BigInteger(1, decodeN);
            BigInteger e = new BigInteger(1, decodeE);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(n, e);
            return keyFactory.generatePublic(keySpec);
        }
        catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error(e.getMessage(), e);
            throw new GlobalException(ErrorInfo.INTERNAL_SERVER_ERROR);
        }
    }
}
