package com.ndm.core.domain.kakao.dto;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class KakaoUserInfoResponseDto {
    private Long id;
    private boolean has_signed_up;
    private LocalDateTime connected_at;
    private LocalDateTime synched_at;
}
