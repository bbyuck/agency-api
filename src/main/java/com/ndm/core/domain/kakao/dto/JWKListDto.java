package com.ndm.core.domain.kakao.dto;

import lombok.Data;

import java.util.List;

@Data
public class JWKListDto {
    private List<JWKDto> keys;
}
