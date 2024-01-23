package com.ndm.core.domain.kakao.dto;

import lombok.Data;

@Data
public class IdTokenHeader {

    private String alg;
    private String typ;
    private String kid;

}
