package com.budget.control.backend.controller.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GroupRequestDTO(
        @NotNull(message = "Required field.")
        String name,
        String description,
        @NotNull(message = "Required field.")
        UUID userId,
        UUID referenceId
) {
}
