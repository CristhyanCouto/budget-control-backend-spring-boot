package com.budget.control.backend.controller.dto.request;

import com.budget.control.backend.model.TransactionExpenseModel;
import com.budget.control.backend.type.TransactionExpenseType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionExpenseRequestDTO(
        @NotNull(message = "Required field.")
        TransactionExpenseType name,
        String description,
        @NotNull(message = "Required field.")
        BigDecimal amount,
        @NotNull(message = "Required field.")
        LocalDate date,
        @NotNull(message = "Required field.")
        Boolean recurrent
) {

}
