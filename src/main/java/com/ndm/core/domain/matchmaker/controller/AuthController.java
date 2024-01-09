package com.ndm.core.domain.matchmaker.controller;

import com.ndm.core.domain.matchmaker.dto.LoginDto;
import com.ndm.core.domain.matchmaker.dto.TokenInfo;
import com.ndm.core.domain.matchmaker.service.AuthService;
import com.ndm.core.model.Response;
import com.ndm.core.model.Trace;
import com.ndm.core.model.TraceData;
import com.ndm.core.model.version.V1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@V1
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Trace
    @PostMapping("/token/issue")
    @Operation(summary = "JWT 인증 토큰 발행", description = "JWT 인증 토큰을 발행한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"
                    , description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = TokenInfo.class))),
            @ApiResponse(responseCode = "500", description = "JWT 인증 토큰 발행 실패"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<TokenInfo> issueToken(@RequestBody LoginDto loginDto) {
        return Response
                .<TokenInfo>builder()
                .data(authService.issueJwtToken(loginDto))
                .build();
    }

    @Trace
    @PostMapping("/token/reissue")
    @Operation(summary = "JWT 인증 토큰 재발행", description = "JWT 인증 토큰을 재발행한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"
                    , description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = TokenInfo.class))),
            @ApiResponse(responseCode = "400", description = "마지막 로그인 ip와 refresh token을 이용한 token reissue api 요청 ip가 달라 토큰 재발급 요청 실패"
                    , content = @Content(schema = @Schema(implementation = TraceData.class))),
            @ApiResponse(responseCode = "500", description = "JWT 인증 토큰 재발행 실패"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<TokenInfo> reissueToken(HttpServletRequest request) {
        return Response
                .<TokenInfo>builder()
                .data(authService.reissueJwtToken(TokenInfo.parseClientTokenInfo(request)))
                .build();
    }


}
