package com.ndm.core.common.exception;

import com.ndm.core.model.Response;
import com.ndm.core.model.TraceData;
import com.ndm.core.model.exception.GlobalException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import static com.ndm.core.model.ErrorInfo.INTERNAL_SERVER_ERROR;
import static com.ndm.core.model.ErrorInfo.NOT_FOUND;
import static com.ndm.core.common.util.RequestUtil.getTraceData;

@Slf4j
@RestControllerAdvice
public class ServletExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Response<TraceData> handleException(Exception e, HttpServletResponse response) {
        response.setStatus(INTERNAL_SERVER_ERROR.getHttpStatus());
        return Response
                .<TraceData>builder()
                .status(INTERNAL_SERVER_ERROR.getHttpStatus())
                .message(INTERNAL_SERVER_ERROR.getMessage())
                .code(INTERNAL_SERVER_ERROR.getCode())
                .data(getTraceData())
                .build();
    }

    @ExceptionHandler(GlobalException.class)
    public Response<TraceData> handleGlobalException(GlobalException e, HttpServletResponse response) {
        response.setStatus(e.getErrorInfo().getHttpStatus());
        return Response
                .<TraceData>builder()
                .status(e.getErrorInfo().getHttpStatus())
                .message(e.getErrorInfo().getMessage())
                .code(e.getErrorInfo().getCode())
                .data(getTraceData())
                .build();
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Response<TraceData> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletResponse response) {
        response.setStatus(NOT_FOUND.getHttpStatus());
        return Response
                .<TraceData>builder()
                .status(NOT_FOUND.getHttpStatus())
                .message(NOT_FOUND.getMessage())
                .code(NOT_FOUND.getCode())
                .data(getTraceData())
                .build();
    }
}
