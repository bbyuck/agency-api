package com.ndm.core.domain.kakao.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class JWKDto {
    private String kid;
    private String kty;
    private String alg;
    private String use;
    private String n;
    private String e;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JWKDto jwkDto = (JWKDto) o;
        return Objects.equals(getKid(), jwkDto.getKid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKid());
    }
}
