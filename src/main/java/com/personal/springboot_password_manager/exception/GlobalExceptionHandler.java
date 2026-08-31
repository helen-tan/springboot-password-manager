package com.personal.springboot_password_manager.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.personal.springboot_password_manager.dto.response.GlobalResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<GlobalResponse<Void>> handleResponseStatusException(
            ResponseStatusException ex) {

        GlobalResponse<Void> response = new GlobalResponse<>(
                ex.getStatusCode().value(),
                ex.getReason(),
                null);

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(response);
    }
}
