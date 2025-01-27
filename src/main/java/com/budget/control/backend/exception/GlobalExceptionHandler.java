package com.budget.control.backend.exception;

import com.budget.control.backend.controller.dto.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageConversionException(HttpMessageConversionException e) {
        String errorMessage = "Invalid value provided for TransactionIncomeType. Please provide a valid enum value.";
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
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

    //Date Format Exception Handler
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() != null && ex.getRequiredType().equals(LocalDate.class)) {
            return ResponseEntity.badRequest().body("Invalid date format. Expected format is YYYY-MM-DD.");
        }
        return ResponseEntity.badRequest().body("Invalid parameter: " + ex.getMessage());
    }
}
