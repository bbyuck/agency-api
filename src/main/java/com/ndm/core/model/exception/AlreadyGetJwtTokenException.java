package com.ndm.core.model.exception;

import com.ndm.core.domain.matchmaker.dto.TokenInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AlreadyGetJwtTokenException extends RuntimeException {

    private final TokenInfo tokenInfo;

}
