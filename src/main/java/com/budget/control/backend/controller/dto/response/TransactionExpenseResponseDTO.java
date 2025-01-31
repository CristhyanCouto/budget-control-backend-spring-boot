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
    // Mapping the data from DTO to the entity
    // This method will be used in the controller to return the transaction expense
    public TransactionExpenseModel mapToTransactionExpenseModel(){
        TransactionExpenseModel transactionExpenseModel = new TransactionExpenseModel();
        transactionExpenseModel.setId(this.id);
        transactionExpenseModel.setName(this.name);
        transactionExpenseModel.setDescription(this.description);
        transactionExpenseModel.setAmount(this.amount);
        transactionExpenseModel.setDate(this.date);
        transactionExpenseModel.setRecurrent(this.recurrent);
        transactionExpenseModel.setCreatedAt(this.createdAt);
        transactionExpenseModel.setUpdatedAt(this.updatedAt);
        transactionExpenseModel.setUserId(this.userId);
        return transactionExpenseModel;
    }
}
