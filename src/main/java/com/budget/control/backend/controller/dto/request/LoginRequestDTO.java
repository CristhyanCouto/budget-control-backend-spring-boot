package com.budget.control.backend.controller.dto.request;

import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO(
        @NotNull(message = "Required field.")
        String email,
        @NotNull(message = "Required field.")
        String password
) {
}
