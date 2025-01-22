package com.budget.control.backend.controller.dto.error;

import org.springframework.http.HttpStatus;

import java.util.List;

public record ErrorResponse(int status, String message, List<ErrorField> errors) {

    public ErrorResponse(String errorMessage, int value) {
        this(value, errorMessage, List.of());
    }

    public static ErrorResponse standardResponse(String message){
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message, List.of());
    }

    public static ErrorResponse conflictResponse(String message){
        return new ErrorResponse(HttpStatus.CONFLICT.value(), message, List.of());
    }

    public static ErrorResponse nullFieldResponse(String message){
        return new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), message, List.of());
    }

    public static ErrorResponse invalidUUIDResponse(String message) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message, List.of());
    }

    public static ErrorResponse invalidFieldResponse(String message) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message, List.of());
    }
}
