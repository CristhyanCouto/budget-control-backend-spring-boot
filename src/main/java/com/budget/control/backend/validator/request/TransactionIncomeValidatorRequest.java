package com.budget.control.backend.validator.request;

import com.budget.control.backend.controller.dto.error.ErrorResponse;
import com.budget.control.backend.exception.DuplicatedRegisterException;
import com.budget.control.backend.exception.InvalidFieldException;
import com.budget.control.backend.exception.NullFieldException;
import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.repository.TransactionIncomeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Component
public class TransactionIncomeValidatorRequest {

    // Dependency Injection
    private final TransactionIncomeRepository transactionIncomeRepository;

    // Constructor
    public TransactionIncomeValidatorRequest(TransactionIncomeRepository transactionIncomeRepository) {
        this.transactionIncomeRepository = transactionIncomeRepository;
    }

    // Validate Method
    public void validate(TransactionIncomeModel transactionIncomeModel) {
        if (isTransactionIncomeDuplicated(transactionIncomeModel)) {
            throw new DuplicatedRegisterException("Transaction income already exists");
        }
        isTransactionIncomeNull(transactionIncomeModel);
        isTransactionAmountOnBounds(transactionIncomeModel);
    }

    //Check if the transaction income is duplicated
    //If the transaction income is duplicated, return true
    //It receives a TransactionIncomeModel object and checks if there is a transaction income with the same name, description, amount and date
    //If the transaction id is null, it knows the transaction income is new and checks if there is a transaction income with the same name, description, amount and date
    private boolean isTransactionIncomeDuplicated(TransactionIncomeModel transactionIncomeModel) {
        Optional<TransactionIncomeModel> transactionIncomeModelOptional = transactionIncomeRepository.findByNameAndDescriptionAndAmountAndDate(
                transactionIncomeModel.getName(),
                transactionIncomeModel.getDescription(),
                transactionIncomeModel.getAmount(),
                transactionIncomeModel.getDate());
        if (transactionIncomeModel.getId() == null) {
            return transactionIncomeModelOptional.isPresent();
        }
        return transactionIncomeModelOptional.isPresent() && !transactionIncomeModel.getId().equals(transactionIncomeModelOptional.get().getId());
    }

    //Check if the transaction non-null fields is null
    private void isTransactionIncomeNull(TransactionIncomeModel transactionIncomeModel) {
        if(transactionIncomeModel.getName() == null) {
            throw new NullFieldException("Transaction income name cannot be null");
        }
        if(transactionIncomeModel.getAmount() == null) {
            throw new NullFieldException("Transaction income amount cannot be null");
        }
        if(transactionIncomeModel.getDate() == null) {
            throw new NullFieldException("Transaction income date cannot be null");
        }
    }

    // Checks if the amount BigDecimal has the right format
    private void isTransactionAmountOnBounds(TransactionIncomeModel transactionIncomeModel){

            if (transactionIncomeModel.getAmount() != null) {
                BigDecimal amount = transactionIncomeModel.getAmount();

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
