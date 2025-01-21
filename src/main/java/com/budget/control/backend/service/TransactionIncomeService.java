package com.budget.control.backend.service;

import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.repository.TransactionIncomeRepository;
import com.budget.control.backend.validator.UUIDValidator;
import com.budget.control.backend.validator.request.TransactionIncomeValidatorRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionIncomeService {

    //Dependency Injection
    private final TransactionIncomeRepository transactionIncomeRepository;
    private final TransactionIncomeValidatorRequest transactionIncomeValidatorRequest;
    private final UUIDValidator uuidValidator;

    //Constructor Injection
    public TransactionIncomeService(
            TransactionIncomeRepository transactionIncomeRepository,
            TransactionIncomeValidatorRequest transactionIncomeValidatorRequest,
            UUIDValidator uuidValidator) {
        this.transactionIncomeRepository = transactionIncomeRepository;
        this.transactionIncomeValidatorRequest = transactionIncomeValidatorRequest;
        this.uuidValidator = uuidValidator;
    }

    //Save income transaction
    public TransactionIncomeModel saveTransactionIncome(TransactionIncomeModel transactionIncomeModel) {
        transactionIncomeValidatorRequest.validate(transactionIncomeModel);
        return transactionIncomeRepository.save(transactionIncomeModel);
    }

    //Get income transaction by id
    public Optional<TransactionIncomeModel> getTransactionIncomeById(UUID transactionIncomeID) {
        uuidValidator.validateUUID(transactionIncomeID.toString());
        return transactionIncomeRepository.findById(transactionIncomeID);
    }
}
