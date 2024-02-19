package com.ndm.core.domain.user.dto;

import com.ndm.core.domain.matchmaker.dto.MatchMakerFriendDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private UserDto userDto;
    private UserProfileDto userProfileDto;
    private MatchingRequestRemainDto matchingRequestRemainDto;
    private List<MatchMakerFriendDto> matchMakerFriends;
}
