package com.budget.control.backend.controller.dto.response;

import java.util.UUID;

public record GroupResponseDTO(
        UUID id,
        String name,
        String description,
        UUID userId,
        UUID referenceId
) {
}
