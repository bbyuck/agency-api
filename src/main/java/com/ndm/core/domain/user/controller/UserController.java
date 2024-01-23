package com.ndm.core.domain.user.controller;

import com.ndm.core.domain.user.dto.UserDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@V1
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Trace
    @PostMapping("/user/join")
    @Operation(summary = "user 회원 가입", description = "유저 회원 정보를 저장한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "500", description = "회원 가입 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<UserDto> join(@RequestBody UserDto userDto) {
        return Response
                .<UserDto>builder()
                .data(userService.join(userDto))
                .build();
    }



}
