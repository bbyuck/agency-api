package com.ndm.core.common.exception;

import com.ndm.core.model.ErrorInfo;
import com.ndm.core.model.Response;
import com.ndm.core.model.exception.GlobalException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.ndm.core.model.ErrorInfo.INTERNAL_SERVER_ERROR;
import static com.ndm.core.common.util.DateUtil.now;
import static com.ndm.core.common.util.RequestUtil.getTraceData;
import static com.ndm.core.common.util.RequestUtil.getTraceId;

@Slf4j
public class FilterExceptionHandler extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }
        catch(GlobalException e) {
            setErrorResponse(request, response, e);
        } catch(Exception e) {
            setErrorResponse(request, response, e);
        }
    }

    private static void setErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception e) {
        log.error("[{}] {} ====== {}", getTraceId(), e.getClass().getSimpleName(), e.getMessage(), e);
        response.setStatus(INTERNAL_SERVER_ERROR.getHttpStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        try {
            response.getWriter()
                    .write(Response.builder()
                            .timestamp(now(String.class))
                            .status(INTERNAL_SERVER_ERROR.getHttpStatus())
                            .code(INTERNAL_SERVER_ERROR.getCode())
                            .message(INTERNAL_SERVER_ERROR.getMessage())
                            .path(request.getRequestURI())
                            .data(getTraceData())
                            .build().serialize());
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        }
    }
    private static void setErrorResponse(HttpServletRequest request, HttpServletResponse response, GlobalException e) {
        log.error("[{}] {} ====== {}", getTraceId(), e.getClass().getSimpleName(), e.getMessage(), e);
        ErrorInfo errorInfo = e.getErrorInfo();

        response.setStatus(errorInfo.getHttpStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        try {
            response.getWriter()
                    .write(Response.builder()
                            .timestamp(now(String.class))
                            .status(errorInfo.getHttpStatus())
                            .code(errorInfo.getCode())
                            .message(errorInfo.getMessage())
                            .path(request.getRequestURI())
                            .data(getTraceData())
                            .build().serialize());
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        }
    }
}
