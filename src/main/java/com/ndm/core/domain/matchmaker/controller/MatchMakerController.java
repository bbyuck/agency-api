package com.ndm.core.domain.matchmaker.controller;


import com.ndm.core.domain.matchmaker.dto.MatchMakerDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@V1
@RestController
@RequiredArgsConstructor
public class MatchMakerController {


    @Trace
    @PostMapping("/signup")
    @Operation(summary = "회원 가입", description = "회원 정보를 저장한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = MatchMakerDto.class))),
            @ApiResponse(responseCode = "500", description = "회원 가입 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<MatchMakerDto> signup(@RequestBody MatchMakerDto matchMakerDto) {
        return Response
                .<MatchMakerDto>builder()
                .data(null)
                .build();
    }

}
