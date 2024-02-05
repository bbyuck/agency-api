package com.ndm.core.domain.user.controller;

import com.ndm.core.domain.user.dto.UserProfileDto;
import com.ndm.core.domain.user.dto.UserProfileSummaryDto;
import com.ndm.core.domain.user.service.ProfileService;
import com.ndm.core.model.Response;
import com.ndm.core.model.Trace;
import com.ndm.core.model.TraceData;
import com.ndm.core.model.version.V1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@V1
@Slf4j
@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @Trace
    @GetMapping("/profile")
    @Operation(summary = "요청자와 Friendship관계에 있는 주선자의 Pool에 존재하는 다른 유저들의 Profile 목록을 가져온다."
            , description = "요청자와 Friendship관계에 있는 주선자의 Pool에 존재하는 다른 유저들의 Profile 목록을 가져온다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = UserProfileSummaryDto.class))),
            @ApiResponse(responseCode = "500", description = "user 정보 저장 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<List<UserProfileSummaryDto>> findMatchingCandidate() {
        return Response
                .<List<UserProfileSummaryDto>>builder()
                .data(profileService.findMatchingCandidate())
                .build();
    }

    @Trace
    @GetMapping("/profile/detail/{id}")
    @Operation(summary = "요청자와 Friendship관계에 있는 주선자의 Pool에 존재하는 다른 유저들의 Profile 목록을 가져온다."
            , description = "요청자와 Friendship관계에 있는 주선자의 Pool에 존재하는 다른 유저들의 Profile 목록을 가져온다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = UserProfileSummaryDto.class))),
            @ApiResponse(responseCode = "500", description = "user 정보 저장 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<UserProfileDto> getProfileInfo(@PathVariable Long id) {
        return Response
                .<UserProfileDto>builder()
                .data(profileService.getProfileInfo(id))
                .build();
    }

}
