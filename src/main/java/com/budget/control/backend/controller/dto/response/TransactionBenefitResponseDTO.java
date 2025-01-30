package com.budget.control.backend.controller.dto.response;

import com.budget.control.backend.model.TransactionBenefitModel;
import com.budget.control.backend.type.TransactionBenefitType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionBenefitResponseDTO(
        UUID id,
        TransactionBenefitType name,
        String description,
        BigDecimal amount,
        LocalDate date,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UUID userId
) {
    // Mapping the data from DTO to the entity
    // This method will be used in the controller to return the transaction benefit
    public TransactionBenefitModel mapToTransactionBenifitModel() {
        TransactionBenefitModel transactionBenefitModel = new TransactionBenefitModel();
        transactionBenefitModel.setId(this.id);
        transactionBenefitModel.setName(this.name);
        transactionBenefitModel.setDescription(this.description);
        transactionBenefitModel.setAmount(this.amount);
        transactionBenefitModel.setDate(this.date);
        transactionBenefitModel.setCreatedAt(this.createdAt);
        transactionBenefitModel.setUpdatedAt(this.updatedAt);
        transactionBenefitModel.setUserId(this.userId);
        return transactionBenefitModel;
    }
}
