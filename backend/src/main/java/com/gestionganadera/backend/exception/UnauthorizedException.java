package com.gestionganadera.backend.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.FORBIDDEN.value(), "UNAUTHORIZED");
    }

    public UnauthorizedException(String message, String errorCode) {
        super(message, HttpStatus.FORBIDDEN.value(), errorCode);
    }
}
