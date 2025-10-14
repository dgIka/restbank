package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ResponseStatusException.class})
    public ResponseEntity<ErrorResponse> handleValidation(ResponseStatusException e, WebRequest request) {
        return buildResponse((HttpStatus) e.getStatusCode(), e.getReason(), e.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIAE(IllegalArgumentException ex, WebRequest req) {
        return buildResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), req);
    }




    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String code, String message, WebRequest request) {
        ErrorResponse body = new ErrorResponse(
                status.value(),
                code,
                message,
                request.getDescription(false),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(body);
    }
}
