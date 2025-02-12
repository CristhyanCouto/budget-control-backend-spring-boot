package com.budget.control.backend.validator.request;

import com.budget.control.backend.exception.DuplicatedRegisterException;
import com.budget.control.backend.exception.InvalidFieldException;
import com.budget.control.backend.exception.NullFieldException;
import com.budget.control.backend.model.TransactionBenefitModel;
import com.budget.control.backend.repository.TransactionBenefitRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Component
public class TransactionBenefitValidatorRequest {

    // Dependency Injection
    private final TransactionBenefitRepository transactionBenefitRepository;

    // Constructor
    public TransactionBenefitValidatorRequest(TransactionBenefitRepository transactionBenefitRepository){
        this.transactionBenefitRepository = transactionBenefitRepository;
    }

    // Validate Method
    public void validate(TransactionBenefitModel transactionBenefitModel) {
        if (isTransactionBenefitDuplicated(transactionBenefitModel)) {
            throw new DuplicatedRegisterException("Transaction benefit already exists");
        }
        isTransactionBenefitNull(transactionBenefitModel);
        isTransactionAmountOnBounds(transactionBenefitModel);
    }

    // Check if the transaction benefit is duplicated
    // If the transaction benefit is duplicated return true
    // If receives a TransactionBenefitModel object and checks if there is a transaction benefit with the same name, description, amount and date
    // If the transaction id is null, it knows the transaction income is new and checks if there is a transaction benefit with the same name, description, amount and date
    private boolean isTransactionBenefitDuplicated(TransactionBenefitModel transactionBenefitModel) {
        Optional<TransactionBenefitModel> transactionBenefitModelOptional = transactionBenefitRepository.findByNameAndDescriptionAndAmountAndDate(
                transactionBenefitModel.getName(),
                transactionBenefitModel.getDescription(),
                transactionBenefitModel.getAmount(),
                transactionBenefitModel.getDate()
        );
        if (transactionBenefitModel.getId() == null) {
            return transactionBenefitModelOptional.isPresent();
        }
        return transactionBenefitModelOptional.isPresent() && !transactionBenefitModel.getId().equals(transactionBenefitModelOptional.get().getId());
    }

    // Check if the transaction non-null fields is null
    private void isTransactionBenefitNull(TransactionBenefitModel transactionBenefitModel) {
        if (transactionBenefitModel.getName() == null) {
            throw new NullFieldException("Transaction benefit name cannot be null");
        }
        if (transactionBenefitModel.getAmount() == null) {
            throw new NullFieldException("Transaction benefit amount cannot be null");
        }
        if (transactionBenefitModel.getDate() == null) {
            throw new NullFieldException("Transaction benefit date cannot be null");
        }
    }

    // Checks if the amount BigDecimal has the right format
    private void isTransactionAmountOnBounds(TransactionBenefitModel transactionBenefitModel) {

        if (transactionBenefitModel.getAmount() != null) {
            BigDecimal amount = transactionBenefitModel.getAmount();

            // Ensure the amount is greater than zero
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidFieldException(
                        "Transaction amount must be greater than zero.");
            }

            // Ensure amount does not exceed (18,2)
            BigDecimal maxLimit = new BigDecimal("9999999999999999.99");

            // Normalize scale before comparison
            amount = amount.setScale(2, RoundingMode.HALF_UP);
            maxLimit = maxLimit.setScale(2, RoundingMode.HALF_UP);

            if (amount.compareTo(maxLimit) > 0) {
                throw new InvalidFieldException(
                        "Transaction amount exceed the allowed limit of 9999999999999999.99.");
            }

            // Ensure amount has at most two decimal places
            if (amount.scale() > 2) {
                throw new InvalidFieldException(
                        "Transaction amount must have at most two decimal places.");
            }
        }
    }
}
