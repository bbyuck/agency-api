package com.ndm.core.common.util;

import com.ndm.core.model.TraceData;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

import static com.ndm.core.model.AttributeKey.TRACE_DATA;

@Slf4j
public class RequestUtil {

    public static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return request.getHeader("X-FORWARDED-FOR") == null
                ? request.getRemoteAddr()
                : request.getHeader("X-FORWARDED-FOR");
    }

    public static String getRequestUri() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return request.getRequestURI();
    }

    public static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    public static TraceData getTraceData() {
        HttpServletRequest request = getCurrentRequest();
        return (TraceData) request.getAttribute(TRACE_DATA.name());
    }

    public static String getTraceId() {
        TraceData traceData = getTraceData();
        if (traceData == null) {
            return null;
        }
        return traceData.getTraceId();
    }
}
