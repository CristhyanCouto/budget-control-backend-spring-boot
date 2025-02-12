package com.budget.control.backend.controller.dto.request;

import com.budget.control.backend.model.TransactionBenefitModel;
import com.budget.control.backend.type.TransactionBenefitType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionBenefitRequestDTO(
        @NotNull(message = "Required field.")
        TransactionBenefitType name,
        String description,
        @NotNull(message = "Required field.")
        BigDecimal amount,
        @NotNull(message = "Required field.")
        LocalDate date
) {

}
