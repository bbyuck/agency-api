package com.ndm.core.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import static com.ndm.core.common.util.DateUtil.now;
import static com.ndm.core.common.util.RequestUtil.getRequestUri;

@Data
@Builder
public class Response<T> {
    @Builder.Default
    private int status = HttpStatus.OK.value();

    @Builder.Default
    private String message = "정상 처리 되었습니다.";

    @Builder.Default
    private String code = "SUCCESS";

    @Builder.Default
    private String timestamp = now(String.class);

    @Builder.Default
    private String path = getRequestUri();

    private T data;

    public String serialize() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
