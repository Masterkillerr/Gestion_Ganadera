package com.gestionganadera.backend.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String resourceName, String id) {
        super(
            String.format("%s no encontrado: %s", resourceName, id),
            HttpStatus.NOT_FOUND.value(),
            "RESOURCE_NOT_FOUND"
        );
    }

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND.value(), "RESOURCE_NOT_FOUND");
    }
}
