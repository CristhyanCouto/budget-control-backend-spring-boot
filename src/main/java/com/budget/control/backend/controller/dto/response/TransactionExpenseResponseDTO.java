package com.budget.control.backend.controller.dto.response;

import com.budget.control.backend.model.TransactionExpenseModel;
import com.budget.control.backend.type.TransactionExpenseType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionExpenseResponseDTO (
        UUID id,
        TransactionExpenseType name,
        String description,
        BigDecimal amount,
        LocalDate date,
        Boolean recurrent,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UUID userId
) {

}
