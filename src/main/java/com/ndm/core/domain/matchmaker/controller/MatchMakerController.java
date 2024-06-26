package com.ndm.core.domain.matchmaker.controller;


import com.ndm.core.domain.message.dto.FCMTokenDto;
import com.ndm.core.domain.matchmaker.dto.MatchMakerDto;
import com.ndm.core.domain.matchmaker.dto.MatchMakerInfoDto;
import com.ndm.core.domain.matchmaker.service.MatchMakerService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@V1
@RestController
@RequiredArgsConstructor
public class MatchMakerController {

    private final MatchMakerService matchMakerService;

    @Trace
    @PostMapping("/matchmaker/join")
    @Operation(summary = "match maker 회원 가입", description = "회원 정보를 저장한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = MatchMakerDto.class))),
            @ApiResponse(responseCode = "500", description = "회원 가입 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<MatchMakerDto> join(@RequestBody MatchMakerDto matchMakerDto) {
        return Response
                .<MatchMakerDto>builder()
                .data(matchMakerService.join(matchMakerDto))
                .build();
    }

    @Trace
    @GetMapping("/matchmaker/link")
    @Operation(summary = "유저 회원가입에 필요로하는 주선자 코드를 담은 uri 가져오기", description = "주선자 코드를 담은 uri을 리턴한다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "SUCCESS"
            , content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "회원 가입 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<String> getCode() {
        return Response.<String>builder()
                .data(matchMakerService.getUriWithCode())
                .build();
    }

    @Trace
    @GetMapping("/matchmaker/info/my")
    @Operation(summary = "요청자의 멤버 정보 조회", description = "요청자의 멤버 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = MatchMakerInfoDto.class))),
            @ApiResponse(responseCode = "500", description = "유저 정보 조회 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<MatchMakerInfoDto> getMyInfo() {
        return Response.<MatchMakerInfoDto>builder()
                .data(MatchMakerInfoDto.builder()
                        .matchMakerDto(matchMakerService.findCaller())
                        .build())
                .build();
    }

    @Trace
    @PostMapping("/matchmaker/fcm/token")
    @Operation(summary = "주선자의 fcm token register", description = "주선자의 fcm token register")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "유저 정보 조회 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<FCMTokenDto> registerMatchMakerFCMToken(@RequestBody FCMTokenDto token) {
        return Response.<FCMTokenDto>builder()
                .data(matchMakerService.registerMatchMakerFCMToken(token))
                .build();
    }


    @Trace
    @PostMapping("/matchmaker")
    @Operation(summary = "주선자 등록", description = "주선자 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "유저 정보 조회 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<MatchMakerDto> registerMatchMaker() {
        return Response.<MatchMakerDto>builder()
                .data(matchMakerService.registerMatchMaker())
                .build();
    }

    @Trace
    @GetMapping("/matchmaker/user")
    @Operation(summary = "요청을 보낸 주선자와 Friendship관계가 있는 유저 목록 조회", description = "요청을 보낸 주선자와 Friendship관계가 있는 유저 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "유저 정보 조회 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<List<UserProfileDto>> getAllUserProfilesFromMyPool() {
        return Response.<List<UserProfileDto>>builder()
                .data(matchMakerService.findAllUserProfilesFromCallersPool())
                .build();
    }
}
