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
    //Mapping the data from DTO to the entity
    //This method will be used in the controller to return the transaction income
    public TransactionIncomeModel mapToTransactionIncomeModel() {
        TransactionIncomeModel transactionIncomeModel = new TransactionIncomeModel();
        transactionIncomeModel.setId(this.id);
        transactionIncomeModel.setName(this.name);
        transactionIncomeModel.setDescription(this.description);
        transactionIncomeModel.setAmount(this.amount);
        transactionIncomeModel.setDate(this.date);
        transactionIncomeModel.setCreatedAt(this.createdAt);
        transactionIncomeModel.setUpdatedAt(this.updatedAt);
        transactionIncomeModel.setUserId(this.userId);
        return transactionIncomeModel;
    }
}
