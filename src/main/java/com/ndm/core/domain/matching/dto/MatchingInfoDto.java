package com.ndm.core.domain.matching.dto;

import com.ndm.core.common.enums.MatchingStatus;
import com.ndm.core.domain.user.dto.UserProfileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingInfoDto {
    private UserProfileDto opponentProfileInfo;
    private MatchingStatus matchingStatus;
    private Long id;

}
