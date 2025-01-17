package com.budget.control.backend.service;

import com.budget.control.backend.model.TransactionExpenseModel;
import com.budget.control.backend.repository.TransactionExpenseRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionExpenseService {

    //Dependency Injection
    private final TransactionExpenseRepository transactionExpenseRepository;

    //Constructor Injection
    public TransactionExpenseService(TransactionExpenseRepository transactionExpenseRepository) {
        this.transactionExpenseRepository = transactionExpenseRepository;
    }

    //Save transaction expense
    public TransactionExpenseModel saveTransactionExpense(TransactionExpenseModel transactionExpenseModel) {
        return transactionExpenseRepository.save(transactionExpenseModel);
    }
}
