package com.budget.control.backend.validator;

import com.budget.control.backend.exception.DuplicatedRegisterException;
import com.budget.control.backend.exception.NullFieldException;
import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.repository.TransactionIncomeRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TransactionIncomeValidator {

    private final TransactionIncomeRepository transactionIncomeRepository;

    public TransactionIncomeValidator(TransactionIncomeRepository transactionIncomeRepository) {
        this.transactionIncomeRepository = transactionIncomeRepository;
    }

    public void validate(TransactionIncomeModel transactionIncomeModel) {
        if (isTransactionIncomeDuplicated(transactionIncomeModel)) {
            throw new DuplicatedRegisterException("Transaction income already exists");
        }
        isTransactionIncomeNull(transactionIncomeModel);
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
    private boolean isTransactionIncomeNull(TransactionIncomeModel transactionIncomeModel) {
        if(transactionIncomeModel.getName() == null) {
            throw new NullFieldException("Transaction income name cannot be null");
        }
        if(transactionIncomeModel.getAmount() == null) {
            throw new NullFieldException("Transaction income amount cannot be null");
        }
        if(transactionIncomeModel.getDate() == null) {
            throw new NullFieldException("Transaction income date cannot be null");
        }
        return false;
    }
}
