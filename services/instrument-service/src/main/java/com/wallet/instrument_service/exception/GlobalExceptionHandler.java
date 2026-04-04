package com.wallet.instrument_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoStaticResourceFound(NoResourceFoundException e){
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = "The requested resource was not found. API documentation is available at /swagger-ui.html";
        ErrorResponse response = new ErrorResponse(status.value(), message, Instant.now());
        return ResponseEntity.status(status.value()).body(response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse response = new ErrorResponse(status.value(), message, Instant.now());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Throwable cause = e.getCause();
        String message = "Invalid JSON format";

        if (cause instanceof InvalidFormatException)
            message = "Invalid value provided. Please refer to the API documentation";

        ErrorResponse response = new ErrorResponse(status.value(), message, Instant.now());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Invalid value '" + e.getValue() + "' for parameter '" + e.getName() + "`";
        ErrorResponse response = new ErrorResponse(status.value(), message, Instant.now());
        return ResponseEntity.status(status).body(response);
    }
}
