package com.budget.control.backend.validator;

import com.budget.control.backend.exception.InvalidUUIDException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDValidator {
    public void validateUUID(String id) {
        try {
            UUID.fromString(id); //Validates the UUID format
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDException("Invalid UUID format: " + id);
        }
    }
}
