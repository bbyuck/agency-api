package com.ndm.core.domain.matching.dto;

import com.ndm.core.common.enums.MatchingRequestStatus;
import com.ndm.core.domain.user.dto.UserProfileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceivedRequestDto {

    private UserProfileDto senderProfileInfo;
    private MatchingRequestStatus matchingRequestStatus;
    private Long id;
}
