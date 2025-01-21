package com.budget.control.backend.service;

import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.repository.TransactionIncomeRepository;
import com.budget.control.backend.validator.TransactionIncomeValidator;
import org.springframework.stereotype.Service;

@Service
public class TransactionIncomeService {

    //Dependency Injection
    private final TransactionIncomeRepository transactionIncomeRepository;
    private final TransactionIncomeValidator transactionIncomeValidator;

    //Constructor Injection
    public TransactionIncomeService(TransactionIncomeRepository transactionIncomeRepository, TransactionIncomeValidator transactionIncomeValidator) {
        this.transactionIncomeRepository = transactionIncomeRepository;
        this.transactionIncomeValidator = transactionIncomeValidator;
    }

    //Save income transaction
    public TransactionIncomeModel saveTransactionIncome(TransactionIncomeModel transactionIncomeModel) {
        transactionIncomeValidator.validate(transactionIncomeModel);
        return transactionIncomeRepository.save(transactionIncomeModel);
    }
}
