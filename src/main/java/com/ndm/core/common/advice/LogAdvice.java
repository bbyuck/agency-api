package com.ndm.core.common.advice;


import com.ndm.core.model.ErrorInfo;
import com.ndm.core.model.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static com.ndm.core.common.util.RequestUtil.*;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAdvice {

    @Around("@annotation(com.ndm.core.model.Trace)")
    public Object loggingDomain(ProceedingJoinPoint joinPoint) throws Exception {
        try {
            log.info("[{}] {} ====== start ======", getTraceId(), joinPoint.getSignature().toString());
            Object result = joinPoint.proceed();
            log.info("[{}] {} ======  end  ======", getTraceId(), joinPoint.getSignature().toString());
            return result;
        }
        catch (GlobalException e) {
            log.error("[{}] {} ====== {}", getTraceId(), e.getClass().getSimpleName(), e.getMessage(), e);
            throw e;
        }
        catch (Throwable e) {
            log.error("[{}] {} ====== {}", getTraceId(), e.getClass().getSimpleName(), e.getMessage(), e);
            throw new Exception(ErrorInfo.INTERNAL_SERVER_ERROR.getMessage(), e);
        }
    }
}
