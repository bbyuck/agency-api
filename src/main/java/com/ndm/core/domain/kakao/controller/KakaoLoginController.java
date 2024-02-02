package com.ndm.core.domain.kakao.controller;

import com.ndm.core.domain.kakao.dto.KakaoLoginDto;
import com.ndm.core.domain.kakao.dto.KakaoLogoutDto;
import com.ndm.core.domain.kakao.service.KakaoLoginService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@V1
@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    @Trace
    @PostMapping("/kakao/authentication")
    @Operation(summary = "Kakao oauth token 요청", description = "Kakao oauth token 발급을 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200"
                    , description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = KakaoLoginDto.class))),
            @ApiResponse(responseCode = "500", description = "Kakao authentication 실패"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<KakaoLoginDto> kakaoAuthentication(@RequestBody KakaoLoginDto requestDto) {
        return Response
                .<KakaoLoginDto>builder()
                .data(kakaoLoginService.authentication(requestDto))
                .build();
    }

    @Trace
    @PostMapping("/kakao/login")
    @Operation(summary = "Kakao oauth token 요청 및 로그인", description = "Kakao oauth token을 발급을 요청하고 로그인한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"
                    , description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = KakaoLoginDto.class))),
            @ApiResponse(responseCode = "500", description = "Kakao Login 실패"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<KakaoLoginDto> kakaoLogin(@RequestBody KakaoLoginDto requestDto) {
        return Response
                .<KakaoLoginDto>builder()
                .data(kakaoLoginService.kakaoLogin(requestDto))
                .build();
    }

    @Trace
    @PostMapping("/kakao/logout")
    @Operation(summary = "Kakao oauth token 요청", description = "Kakao oauth token을 발급을 요청하고 로그인한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"
                    , description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Kakao Login 실패"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<String> kakaoLogout(@RequestBody KakaoLogoutDto requestDto) {

        kakaoLoginService.kakaoLogout(requestDto);

        return Response
                .<String>builder()
                .message("성공적으로 로그아웃 되었습니다.")
                .build();
    }
}
