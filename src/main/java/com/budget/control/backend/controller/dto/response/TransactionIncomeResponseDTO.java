package com.budget.control.backend.controller.dto.response;

import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.type.TransactionIncomeType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionIncomeResponseDTO(
        UUID id,
        TransactionIncomeType name,
        String description,
        BigDecimal amount,
        LocalDate date,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UUID userId
) {

}
