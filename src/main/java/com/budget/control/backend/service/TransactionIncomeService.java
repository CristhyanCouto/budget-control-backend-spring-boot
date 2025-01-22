package com.budget.control.backend.service;

import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.repository.TransactionIncomeRepository;
import com.budget.control.backend.type.TransactionIncomeType;
import com.budget.control.backend.validator.UUIDValidator;
import com.budget.control.backend.validator.request.TransactionIncomeValidatorRequest;
import com.budget.control.backend.validator.response.TransactionIncomeValidatorResponse;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionIncomeService {

    //Dependency Injection
    private final TransactionIncomeRepository transactionIncomeRepository;
    private final TransactionIncomeValidatorRequest transactionIncomeValidatorRequest;
    private final TransactionIncomeValidatorResponse transactionIncomeValidatorResponse;
    private final UUIDValidator uuidValidator;

    //Constructor Injection
    public TransactionIncomeService(
            TransactionIncomeRepository transactionIncomeRepository,
            TransactionIncomeValidatorRequest transactionIncomeValidatorRequest,
            TransactionIncomeValidatorResponse transactionIncomeValidatorResponse,
            UUIDValidator uuidValidator) {
        this.transactionIncomeRepository = transactionIncomeRepository;
        this.transactionIncomeValidatorRequest = transactionIncomeValidatorRequest;
        this.transactionIncomeValidatorResponse = transactionIncomeValidatorResponse;
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

    //Get income transaction by name or description or amount or date
    public List<TransactionIncomeModel> getTransactionIncomeByNameOrDescriptionOrAmountOrDate(
            TransactionIncomeType name, String description, BigDecimal amount, LocalDate date) {
        return transactionIncomeRepository.findByNameOrDescriptionContainsOrAmountOrDateOrderByDateDesc(name, description, amount, date);
    }

}
