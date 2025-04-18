package com.budget.control.backend.controller.dto.request;

import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.type.TransactionIncomeType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionIncomeRequestDTO(
        @NotNull(message = "Required field.")
        TransactionIncomeType name,
        String description,
        @NotNull(message = "Required field.")
        BigDecimal amount,
        @NotNull(message = "Required field.")
        LocalDate date
) {

}
