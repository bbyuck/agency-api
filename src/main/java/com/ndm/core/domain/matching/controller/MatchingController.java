package com.ndm.core.domain.matching.controller;

import com.ndm.core.domain.matching.dto.MatchingRequestDto;
import com.ndm.core.domain.matching.dto.MatchingRequestResultDto;
import com.ndm.core.domain.matching.service.MatchingService;
import com.ndm.core.domain.user.dto.MatchingRequestRemainDto;
import com.ndm.core.domain.user.dto.UserDto;
import com.ndm.core.domain.user.dto.UserInfoDto;
import com.ndm.core.domain.user.dto.UserProfileDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@V1
@Slf4j
@RestController
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @Trace
    @PostMapping("/matching/request")
    @Operation(summary = "상대방에게 매칭 요청보내기", description = "상대방에게 매칭 요청보내기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = MatchingRequestResultDto.class))),
            @ApiResponse(responseCode = "500", description = "요청 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<MatchingRequestResultDto> sendRequest(@RequestBody MatchingRequestDto requestDto) {
        return Response
                .<MatchingRequestResultDto>builder()
                .data(matchingService.sendRequest(requestDto))
                .build();
    }

    @Trace
    @PostMapping("/matching/request/reject")
    @Operation(summary = "매칭 요청 거절하기", description = "매칭 요청 거절하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = MatchingRequestResultDto.class))),
            @ApiResponse(responseCode = "500", description = "매칭 요청 거절 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<MatchingRequestResultDto> rejectRequest() {
        return Response
                .<MatchingRequestResultDto>builder()
                .data(matchingService.rejectRequest())
                .build();
    }

    @Trace
    @GetMapping("/matching/request/received")
    @Operation(summary = "받은 요청 조회", description = "받은 요청 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = UserInfoDto.class))),
            @ApiResponse(responseCode = "500", description = "요청 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<UserProfileDto> findReceivedRequest() {
        return Response.<UserProfileDto>builder()
                .data(matchingService.getReceivedRequestSender())
                .build();
    }

    @Trace
    @PostMapping("/matching/request/confirm")
    @Operation(summary = "받은 요청 확인", description = "받은 요청 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(responseCode = "500", description = "요청 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<UserDto> confirmReceivedRequest() {
        return Response.<UserDto>builder()
                .data(matchingService.confirmReceivedRequest())
                .build();
    }


    @Trace
    @GetMapping("/matching/request/today/remain")
    @Operation(summary = "오늘 남은 매칭 요청 횟수 조회", description = "오늘 남은 매칭 요청 횟수 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = MatchingRequestRemainDto.class))),
            @ApiResponse(responseCode = "500", description = "요청 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<MatchingRequestRemainDto> findTodayMatchingRequestRemain() {
        return Response.<MatchingRequestRemainDto>builder()
                .data(matchingService.findMatchingRequestRemain())
                .build();
    }

}
