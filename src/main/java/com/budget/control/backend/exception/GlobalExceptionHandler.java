package com.budget.control.backend.exception;

import com.budget.control.backend.controller.dto.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.DateTimeException;
import java.time.LocalDate;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Date Format Exception Handler Name Handler Exception
    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<Object> handleHttpMessageConversionException(HttpMessageConversionException e) {
        Throwable cause = e.getCause();
        while (cause != null) {
            if (cause instanceof DateTimeException) {
                return ResponseEntity.badRequest().body("Invalid date format. Expected format is YYYY-MM-DD.");
            }
            cause = cause.getCause();
        }
        String errorMessage = "Invalid transaction name.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(InvalidFieldException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFieldException(InvalidFieldException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // Generic Method Argument Type Mismatch Handler
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() != null && ex.getRequiredType().equals(LocalDate.class)) {
            return ResponseEntity.badRequest().body("Invalid date format. Expected format is YYYY-MM-DD.");
        }
        return ResponseEntity.badRequest().body("Invalid parameter: " + ex.getMessage());
    }
}
