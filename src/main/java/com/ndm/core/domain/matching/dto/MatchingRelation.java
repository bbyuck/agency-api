package com.ndm.core.domain.matching.dto;

import com.ndm.core.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingRelation {
    private User caller;
    private User opponent;
}
