package com.ndm.core.domain.matching.controller;

import com.ndm.core.domain.matching.dto.*;
import com.ndm.core.domain.matching.service.MatchingService;
import com.ndm.core.domain.user.dto.MatchingRequestRemainDto;
import com.ndm.core.domain.user.dto.UserDto;
import com.ndm.core.domain.user.dto.UserInfoDto;
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
    @Operation(summary = "상대에게 매칭 요청보내기", description = "상대에게 매칭 요청보내기")
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
    public Response<ReceivedRequestDto> findReceivedRequest() {
        return Response.<ReceivedRequestDto>builder()
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

    @Trace
    @PostMapping("/matching/request/accept")
    @Operation(summary = "매칭 요청 수락", description = "매칭 요청을 수락한다.")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "SUCCESS"
                            , content = @Content(schema = @Schema(implementation = MatchingResponseDto.class))),
                    @ApiResponse(responseCode = "500", description = "요청 실패 - 사유 코드 참조"
                            , content = @Content(schema = @Schema(implementation = TraceData.class)))
            }
    )
    public Response<MatchingResponseDto> acceptMatchingRequest(@RequestBody MatchingRequestDto requestDto) {
        return Response.
                <MatchingResponseDto>builder()
                .data(matchingService.acceptMatchingRequest(requestDto))
                .build();
    }

    @Trace
    @PostMapping("/matching/confirm")
    @Operation(summary = "매칭 확인", description = "매칭 확인")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "SUCCESS"
                            , content = @Content(schema = @Schema(implementation = MatchingResponseDto.class))),
                    @ApiResponse(responseCode = "500", description = "요청 실패 - 사유 코드 참조"
                            , content = @Content(schema = @Schema(implementation = TraceData.class)))
            }
    )
    public Response<MatchingResponseDto> confirmMatching() {
        return Response.<MatchingResponseDto>builder()
                .data(matchingService.confirmMatching())
                .build();
    }

    @Trace
    @GetMapping("/matching")
    @Operation(summary = "현재 매칭 정보", description = "현재 매칭 정보")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "SUCCESS"
                            , content = @Content(schema = @Schema(implementation = MatchingResponseDto.class))),
                    @ApiResponse(responseCode = "500", description = "요청 실패 - 사유 코드 참조"
                            , content = @Content(schema = @Schema(implementation = TraceData.class)))
            }
    )
    public Response<MatchingInfoDto> getMatchingInfo() {
        return Response.<MatchingInfoDto>builder()
                .data(matchingService.getMatchingInfo())
                .build();
    }


    @Trace
    @PostMapping("/matching/complete")
    @Operation(summary = "매칭 성사 요청", description = "매칭에 동의하고 소개 받기를 주선자에게 요청, 둘 모두가 동의된 시점에 양측 주선자에게 알림 발송")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "SUCCESS"
                            , content = @Content(schema = @Schema(implementation = MatchingResponseDto.class))),
                    @ApiResponse(responseCode = "500", description = "요청 실패 - 사유 코드 참조"
                            , content = @Content(schema = @Schema(implementation = TraceData.class)))
            }
    )
    public Response<MatchingResponseDto> matchingComplete(@RequestBody MatchingDto matchingDto) {
        return Response.<MatchingResponseDto>builder()
                .data(matchingService.matchingComplete(matchingDto))
                .build();
    }

    @Trace
    @PostMapping("/matching/cancel")
    @Operation(summary = "매칭 취소 요청", description = "현재 걸려있는 매칭을 취소하고 상대에게 알림 발송")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "SUCCESS"
                            , content = @Content(schema = @Schema(implementation = MatchingResponseDto.class))),
                    @ApiResponse(responseCode = "500", description = "요청 실패 - 사유 코드 참조"
                            , content = @Content(schema = @Schema(implementation = TraceData.class)))
            }
    )
    public Response<MatchingResponseDto> matchingCancel(@RequestBody MatchingDto matchingDto) {
        return Response.<MatchingResponseDto>builder()
                .data(matchingService.matchingCancel(matchingDto))
                .build();
    }


}
