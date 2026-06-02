package com.gestionganadera.backend.exception;

import com.gestionganadera.backend.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(
            AppException ex,
            HttpServletRequest request) {
        log.warn("AppException: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            ex.getHttpStatus(),
            ex.getErrorCode(),
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.valueOf(ex.getHttpStatus()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        StringBuilder message = new StringBuilder("Validación fallida: ");
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                message.append(fieldError.getField()).append(" - ");
                message.append(fieldError.getDefaultMessage()).append("; ");
            }
        });
        
        log.warn("Validation error: {}", message);
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR",
            message.toString(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_ARGUMENT",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Unexpected exception", ex);
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_ERROR",
            "Se produjo un error interno. Por favor, contacte al administrador.",
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
