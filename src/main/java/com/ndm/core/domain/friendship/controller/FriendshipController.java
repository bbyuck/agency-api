package com.ndm.core.domain.friendship.controller;

import com.ndm.core.domain.friendship.dto.FriendshipRequestDto;
import com.ndm.core.domain.friendship.dto.FriendshipResponseDto;
import com.ndm.core.domain.friendship.service.FriendshipService;
import com.ndm.core.model.Response;
import com.ndm.core.model.Trace;
import com.ndm.core.model.TraceData;
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

@Slf4j
@RestController
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;


    @Trace
    @PostMapping("/friendship")
    @Operation(summary = "주선자와 friend 관계 생성", description = "주선자와 friend 관계 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS"
                    , content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "유저 정보 조회 실패 - 사유 코드 참조"
                    , content = @Content(schema = @Schema(implementation = TraceData.class)))
    })
    public Response<FriendshipResponseDto> createFriendship(@RequestBody FriendshipRequestDto requestDto) {
        return Response.<FriendshipResponseDto>builder()
                .data(friendshipService.createFriendship(requestDto))
                .build();
    }
}
