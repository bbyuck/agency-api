package com.ndm.core.model.exception;

import com.ndm.core.model.ErrorInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class GlobalException extends RuntimeException {
    protected final ErrorInfo errorInfo;

    public GlobalException() {
        super(ErrorInfo.INTERNAL_SERVER_ERROR.getMessage());
        errorInfo = ErrorInfo.INTERNAL_SERVER_ERROR;
    }

    public GlobalException(Throwable e) {
        super(ErrorInfo.INTERNAL_SERVER_ERROR.getMessage(), e);
        errorInfo = ErrorInfo.INTERNAL_SERVER_ERROR;
    }

    public GlobalException(ErrorInfo errorInfo) {
        super(errorInfo.getMessage());
        this.errorInfo = errorInfo;
    }

    public GlobalException(ErrorInfo errorInfo, Throwable e) {
        super(errorInfo.getMessage(), e);
        this.errorInfo = errorInfo;
    }
}
