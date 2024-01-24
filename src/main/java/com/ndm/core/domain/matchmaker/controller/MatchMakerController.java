package com.ndm.core.domain.matchmaker.controller;


import com.ndm.core.domain.matchmaker.dto.MatchMakerDto;
import com.ndm.core.domain.matchmaker.service.MatchMakerService;
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
    public Response<String> getCode(MatchMakerDto matchMakerDto) {
        return Response.<String>builder()
                .data(matchMakerService.getUriWithCode(matchMakerDto))
                .build();
    }
}
