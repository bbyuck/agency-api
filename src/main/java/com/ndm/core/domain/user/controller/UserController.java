package com.ndm.core.domain.user.controller;

import com.ndm.core.domain.matching.service.MatchingService;
import com.ndm.core.domain.user.dto.UserDto;
import com.ndm.core.domain.user.dto.UserInfoDto;
import com.ndm.core.domain.user.dto.UserProfileDto;
import com.ndm.core.domain.user.service.UserService;
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
import org.springframework.web.bind.annotation.*;

@V1
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final MatchingService matchingService;

    @Trace
    @PostMapping("/user/join")
    @Operation(summary = "user 회원 가입", description = "유저 회원 정보를 저장한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "500", description = "user 정보 저장 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<UserDto> join(@RequestBody UserDto userDto) {
        return Response
                .<UserDto>builder()
                .data(userService.join(userDto))
                .build();
    }

    @Trace
    @GetMapping("/user/profile/my")
    @Operation(summary = "요청자의 프로필 정보 조회", description = "요청자의 프로필 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = UserProfileDto.class))),
            @ApiResponse(responseCode = "500", description = "프로필 정보 조회 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<UserProfileDto> findMyProfile() {
        return Response.<UserProfileDto>builder()
                .data(userService.findCallersProfile())
                .build();
    }

    @Trace
    @PostMapping("/user/profile/my")
    @Operation(summary = "요청자의 프로필 정보 저장", description = "요청자의 프로필 정보 저장")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = UserProfileDto.class))),
            @ApiResponse(responseCode = "500", description = "프로필 정보 저장 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<UserProfileDto> saveMyProfile(@RequestBody UserProfileDto userProfileDto) {
        return Response.<UserProfileDto>builder()
                .data(userService.saveCallersProfile(userProfileDto))
                .build();
    }

    @Trace
    @PostMapping("/user/profile/new")
    @Operation(summary = "요청자의 프로필 정보 등록", description = "요청자의 프로필 정보 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = UserProfileDto.class))),
            @ApiResponse(responseCode = "500", description = "프로필 정보 저장 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<UserDto> registerProfile(@RequestBody UserProfileDto userProfileDto) {
        return Response.<UserDto>builder()
                .data(userService.registerProfile(userProfileDto))
                .build();
    }

    @Trace
    @GetMapping("/user/info/my")
    @Operation(summary = "요청자의 유저 정보 조회", description = "요청자의 유저 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = UserInfoDto.class))),
            @ApiResponse(responseCode = "500", description = "유저 정보 조회 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<UserInfoDto> getMyInfo() {
        return Response.<UserInfoDto>builder()
                .data(UserInfoDto.builder()
                        .userDto(userService.findCaller())
                        .userProfileDto(userService.findCallersProfile())
                        .matchingRequestRemainDto(matchingService.findMatchingRequestRemain())
                        .build())
                .build();
    }

    @Trace
    @PostMapping("/user/fcm/token")
    @Operation(summary = "요청자의 fcm token register", description = "요청자의 fcm token register")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "유저 정보 조회 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<String> registerUserFCMToken(@RequestBody String token) {
        return Response.<String>builder()
                .data(userService.registerUserFCMToken(token))
                .build();
    }
}
