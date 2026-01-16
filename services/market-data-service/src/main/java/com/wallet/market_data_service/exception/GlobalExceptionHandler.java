package com.wallet.market_data_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> defaultHandler(Exception e){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Something went wrong";
        ErrorResponse response = new ErrorResponse(status.value(), message, Instant.now());

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(value = TickerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTickerNotFound(TickerNotFoundException e){
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = "Ticker not found";
        ErrorResponse response = new ErrorResponse(status.value(), message, Instant.now());

        return ResponseEntity.status(status).body(response);
    }
}
