package com.budget.control.backend.controller.dto;

import com.budget.control.backend.model.TransactionBenefitModel;
import com.budget.control.backend.type.TransactionBenefitType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionBenefitDTO(
        TransactionBenefitType name,
        String description,
        BigDecimal amount,
        LocalDate date
) {

    //Map the data from the DTO to the model
    //This method will be used in the controller to save the transaction benefit
    public TransactionBenefitModel mapToTransactionBenefitModel() {
        TransactionBenefitModel transactionBenefitModel = new TransactionBenefitModel();
        transactionBenefitModel.setName(this.name);
        transactionBenefitModel.setDescription(this.description);
        transactionBenefitModel.setAmount(this.amount);
        transactionBenefitModel.setDate(this.date);
        return transactionBenefitModel;
    }
}
