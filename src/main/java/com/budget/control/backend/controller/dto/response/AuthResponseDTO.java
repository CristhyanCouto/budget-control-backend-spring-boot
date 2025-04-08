package com.budget.control.backend.controller.dto.response;

import jakarta.validation.constraints.NotNull;

public record AuthResponseDTO(
        @NotNull(message = "Required field.")
        String token,
        @NotNull(message = "Required field.")
        String firstName,
        @NotNull(message = "Required field.")
        String lastName,
        @NotNull(message = "Required field.")
        String role
) {
}
