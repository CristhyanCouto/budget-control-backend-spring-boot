package com.budget.control.backend.controller.dto;

import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.type.TransactionIncomeType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionIncomeDTO(
        TransactionIncomeType name,
        String description,
        BigDecimal amount,
        LocalDate date
) {
    //Map the data from the DTO to the model
    //This method will be used in the controller to save the transaction income
    public TransactionIncomeModel mapToTransactionIncomeModel() {
        TransactionIncomeModel transactionIncomeModel = new TransactionIncomeModel();
        transactionIncomeModel.setName(this.name);
        transactionIncomeModel.setDescription(this.description);
        transactionIncomeModel.setAmount(this.amount);
        transactionIncomeModel.setDate(this.date);
        return transactionIncomeModel;
    }
}
