package com.budget.control.backend.controller.dto;

import com.budget.control.backend.model.TransactionExpenseModel;
import com.budget.control.backend.type.TransactionExpenseType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionExpenseDTO(
        TransactionExpenseType name,
        String description,
        BigDecimal amount,
        LocalDate date,
        Boolean recurrent
) {

    //Map the data from the DTO to the model
    //This method will be used in the controller to save the transaction expense
    public TransactionExpenseModel mapToTransactionExpenseModel() {
        TransactionExpenseModel transactionExpenseModel = new TransactionExpenseModel();
        transactionExpenseModel.setName(this.name);
        transactionExpenseModel.setDescription(this.description);
        transactionExpenseModel.setAmount(this.amount);
        transactionExpenseModel.setDate(this.date);
        transactionExpenseModel.setRecurrent(this.recurrent);
        return transactionExpenseModel;
    }
}
