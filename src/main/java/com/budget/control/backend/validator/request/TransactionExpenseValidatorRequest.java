package com.budget.control.backend.validator.request;

import com.budget.control.backend.exception.DuplicatedRegisterException;
import com.budget.control.backend.exception.InvalidFieldException;
import com.budget.control.backend.exception.NullFieldException;
import com.budget.control.backend.model.TransactionExpenseModel;
import com.budget.control.backend.repository.TransactionExpenseRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Component
public class TransactionExpenseValidatorRequest {

    // Dependency Injection
    private final TransactionExpenseRepository transactionExpenseRepository;

    // Constructor
    public TransactionExpenseValidatorRequest(TransactionExpenseRepository transactionExpenseRepository) {
        this.transactionExpenseRepository = transactionExpenseRepository;
    }

    // Validate Method
    public void validate(TransactionExpenseModel transactionExpenseModel) {
        if (isTransactionExpenseDuplicated(transactionExpenseModel)) {
            throw new DuplicatedRegisterException("Transaction expense already exists.");
        }
        isTransactionExpenseNull(transactionExpenseModel);
        isTransactionAmountOnBounds(transactionExpenseModel);
    }

    //Check if the transaction expense is duplicated
    //If the transaction expense is duplicated, return true
    //It receives a TransactionExpenseModel object and checks if there is a transaction expense with the same name, description, amount, date and recurrent
    //If the transaction id is null, it knows the transaction expense is new and checks if there is a transaction expense with the same name, description, amount, date and recurrent
    private boolean isTransactionExpenseDuplicated(TransactionExpenseModel transactionExpenseModel) {
        Optional<TransactionExpenseModel> transactionExpenseModelOptional = transactionExpenseRepository.findByNameAndDescriptionAndAmountAndDateAndRecurrent(
                transactionExpenseModel.getName(),
                transactionExpenseModel.getDescription(),
                transactionExpenseModel.getAmount(),
                transactionExpenseModel.getDate(),
                transactionExpenseModel.getRecurrent()
        );
        if (transactionExpenseModel.getId() == null) {
            return transactionExpenseModelOptional.isPresent();
        }
        return  transactionExpenseModelOptional.isPresent() && !transactionExpenseModel.getId().equals(transactionExpenseModelOptional.get().getId());
    }

    // Check if the transaction non-null fields is null
    private void isTransactionExpenseNull(TransactionExpenseModel transactionExpenseModel) {
        if (transactionExpenseModel.getName() == null) {
            throw new NullFieldException("Transaction expense name cannot be null");
        }
        if (transactionExpenseModel.getAmount() == null) {
            throw new NullFieldException("Transaction amount cannot be null");
        }
        if (transactionExpenseModel.getDate() == null) {
            throw new NullFieldException("Transaction date cannot be null");
        }
        if (transactionExpenseModel.getRecurrent() == null) {
            throw new NullFieldException("Transaction recurrent cannot be null");
        }
    }

    // Checks if the amount BigDecimal has the right format
    private void isTransactionAmountOnBounds(TransactionExpenseModel transactionExpenseModel){

        if (transactionExpenseModel.getAmount() != null) {
            BigDecimal amount = transactionExpenseModel.getAmount();

            // Ensure amount is greater than zero
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidFieldException("Transaction amount must be greater than zero.");
            }

            // Ensure amount does not exceed (18,2)
            BigDecimal maxLimit = new BigDecimal("9999999999999999.99");

            // Normalize scale before comparison
            amount = amount.setScale(2, RoundingMode.HALF_UP);
            maxLimit = maxLimit.setScale(2, RoundingMode.HALF_UP);

            if (amount.compareTo(maxLimit) > 0) {
                throw new InvalidFieldException("Transaction amount exceed the allowed limit of 9999999999999999.99.");
            }

            // Ensure amount has at most two decimal places
            if (amount.scale() > 2) {
                throw new InvalidFieldException("Transaction amount must have at most two decimal places.");
            }
        }
    }
}
