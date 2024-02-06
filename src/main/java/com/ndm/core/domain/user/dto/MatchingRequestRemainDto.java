package com.ndm.core.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingRequestRemainDto {
    private Long maxCount;
    private Long currentCount;
    private boolean searched;
}
