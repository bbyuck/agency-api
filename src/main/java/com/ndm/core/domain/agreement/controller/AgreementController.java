package com.ndm.core.domain.agreement.controller;

import com.ndm.core.domain.agreement.dto.AgreementDto;
import com.ndm.core.domain.agreement.dto.TempMemberDto;
import com.ndm.core.domain.agreement.service.AgreementService;
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

import java.util.List;

@V1
@Slf4j
@RestController
@RequiredArgsConstructor
public class AgreementController {

    private final AgreementService agreementService;

    @Trace
    @PutMapping("/agreement")
    @Operation(summary = "동의서에 동의", description = "약관 동의서에 동의")
    @ApiResponses({
            @ApiResponse(responseCode = "200"
                    , description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = TempMemberDto.class))),
            @ApiResponse(responseCode = "500", description = "약관 동의 실패"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<TempMemberDto> agree(@RequestBody AgreementDto agreementDto) {

        return Response.<TempMemberDto>builder()
                .data(agreementService.submitAgreement(agreementDto))
                .build();

    }
}
