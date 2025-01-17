package com.budget.control.backend.service;

import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.repository.TransactionIncomeRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionIncomeService {

    //Dependency Injection
    private final TransactionIncomeRepository transactionIncomeRepository;

    //Constructor Injection
    public TransactionIncomeService(TransactionIncomeRepository transactionIncomeRepository) {
        this.transactionIncomeRepository = transactionIncomeRepository;
    }

    //Save income transaction
    public TransactionIncomeModel saveTransactionIncome(TransactionIncomeModel transactionIncomeModel) {
        return transactionIncomeRepository.save(transactionIncomeModel);
    }
}
