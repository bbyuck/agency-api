package com.ndm.core.domain.member.dto;

import com.ndm.core.common.enums.MatchMakerStatus;
import com.ndm.core.common.enums.UserStatus;
import com.ndm.core.domain.matchmaker.dto.MatchMakerInfoDto;
import com.ndm.core.domain.user.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoDto {

    private UserStatus userStatus;
    private MatchMakerStatus matchMakerStatus;
}
