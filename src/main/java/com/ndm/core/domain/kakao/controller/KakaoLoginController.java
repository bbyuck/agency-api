package com.ndm.core.domain.kakao.controller;

import com.ndm.core.domain.kakao.dto.KakaoLoginDto;
import com.ndm.core.domain.kakao.dto.KakaoOAuthRequestDto;
import com.ndm.core.domain.kakao.service.KakaoLoginService;
import com.ndm.core.domain.matchmaker.dto.TokenInfo;
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
    @PostMapping("/kakao/oauth/token")
    @Operation(summary = "Kakao oauth token 요청", description = "Kakao oauth token을 발급을 요청하고 로그인한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"
                    , description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = TokenInfo.class))),
            @ApiResponse(responseCode = "500", description = "Kakao Login 실패"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<String> kakaoLogin(@RequestBody KakaoLoginDto requestDto) {

        kakaoLoginService.kakaoLogin(requestDto);

        return Response
                .<String>builder()
                .data("628d3a3c134c38043b0bd8d5814dc86d")
                .build();
    }
}
