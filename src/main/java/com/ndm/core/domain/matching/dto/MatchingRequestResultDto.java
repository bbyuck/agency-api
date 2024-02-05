package com.ndm.core.domain.matching.dto;

import com.ndm.core.common.enums.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingRequestResultDto {

    private MemberStatus memberStatus;
}
