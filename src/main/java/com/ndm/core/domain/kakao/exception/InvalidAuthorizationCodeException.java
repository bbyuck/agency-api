package com.ndm.core.domain.kakao.exception;

public class InvalidAuthorizationCodeException extends RuntimeException {
    public InvalidAuthorizationCodeException() {
        super();
    }

    public InvalidAuthorizationCodeException(String message) {
        super(message);
    }

    public InvalidAuthorizationCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAuthorizationCodeException(Throwable cause) {
        super(cause);
    }

    protected InvalidAuthorizationCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
