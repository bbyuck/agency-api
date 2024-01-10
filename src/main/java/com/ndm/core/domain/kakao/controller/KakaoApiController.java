package com.ndm.core.domain.kakao.controller;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@V1
@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoApiController {

    private final KakaoLoginService kakaoLoginService;

    @Value("${kakao.key.rest}")
    private String kakaoRestKey;

    @Trace
    @GetMapping("/kakao/key/rest")
    @Operation(summary = "Kakao REST API Key", description = "Kakao REST API Key를 리턴한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"
                    , description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Kakao REST API Key 리턴 실패"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<String> getKakaoRestKey() {
        log.info("kakaoRestKey request ====== {}");
        return Response
                .<String>builder()
                .data(kakaoRestKey)
                .build();
    }


}
