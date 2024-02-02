package com.ndm.core.domain.matchmaker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchMakerInfoDto {

    private MatchMakerDto matchMakerDto;
}
