package com.budget.control.backend.service;

import com.budget.control.backend.model.TransactionBenefitModel;
import com.budget.control.backend.repository.TransactionBenefitRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionBenefitService {

    //Dependency Injection
    private final TransactionBenefitRepository transactionBenefitRepository;

    //Constructor Injection
    public TransactionBenefitService(TransactionBenefitRepository transactionBenefitRepository) {
        this.transactionBenefitRepository = transactionBenefitRepository;
    }

    //Save benefit transaction
    public void saveTransactionBenefit(TransactionBenefitModel transactionBenefitModel) {
        transactionBenefitRepository.save(transactionBenefitModel);
    }
}
