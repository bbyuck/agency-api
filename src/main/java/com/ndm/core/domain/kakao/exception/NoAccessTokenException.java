package com.ndm.core.domain.kakao.exception;

public class NoAccessTokenException extends RuntimeException {
    public NoAccessTokenException() {
        super();
    }

    public NoAccessTokenException(String message) {
        super(message);
    }

    public NoAccessTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAccessTokenException(Throwable cause) {
        super(cause);
    }

    protected NoAccessTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
