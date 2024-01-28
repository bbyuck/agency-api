package com.ndm.core.filter;

import com.ndm.core.model.HeaderKey;
import com.ndm.core.model.TraceData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static com.ndm.core.model.AttributeKey.*;

@Slf4j
public class AttributeFilter extends OncePerRequestFilter {
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // TraceData attribute
        request.setAttribute(TRACE_DATA.name(), TraceData.builder().traceId(UUID.randomUUID().toString()).build());

        System.out.println("request = " + request.getHeader(HeaderKey.X_Credential_Token.name()));
        chain.doFilter(request, response);
    }
}
