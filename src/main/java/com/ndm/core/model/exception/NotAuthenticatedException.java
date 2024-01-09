package com.ndm.core.model.exception;

import com.ndm.core.model.ErrorInfo;

public class NotAuthenticatedException extends GlobalException {

    public NotAuthenticatedException(ErrorInfo errorInfo) {
        super(errorInfo);
    }

    public NotAuthenticatedException(ErrorInfo errorInfo, Throwable cause) {
        super(errorInfo, cause);
    }
}
