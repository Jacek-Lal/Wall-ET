package com.wallet.portfolio_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundHandler(ResourceNotFoundException e){
        log.warn(e.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = "Resource not found";
        ErrorResponse response = new ErrorResponse(status.value(), message, Instant.now());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(value = ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> serviceUnavailableHandler(ServiceUnavailableException e){
        log.warn(e.getMessage());
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        String message = "Service currenntly unavailable. Try again later";
        ErrorResponse response = new ErrorResponse(status.value(), message, Instant.now());
        return ResponseEntity.status(status).body(response);
    }
}
