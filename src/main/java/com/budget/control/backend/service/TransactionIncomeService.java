package com.budget.control.backend.service;

import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.repository.TransactionIncomeRepository;
import com.budget.control.backend.type.TransactionIncomeType;
import com.budget.control.backend.validator.UUIDValidator;
import com.budget.control.backend.validator.request.TransactionIncomeValidatorRequest;
import com.budget.control.backend.validator.response.TransactionIncomeValidatorResponse;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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

//    //Get income transaction by name or description or amount or date
//    public List<TransactionIncomeModel> getTransactionIncomeByNameOrDescriptionOrAmountOrDate(
//            TransactionIncomeType name, String description, BigDecimal amount, LocalDate date) {
//        return transactionIncomeRepository.findByNameOrDescriptionContainsOrAmountOrDateOrderByDateDesc(name, description, amount, date);
//    }

    //Dynamic query to get income transaction by filters
    public List<TransactionIncomeModel> getTransactionIncomeByNameOrDescriptionOrAmountOrDate(
            TransactionIncomeType name, String description, BigDecimal amount, LocalDate date){

        //Dynamic query specification
        Specification<TransactionIncomeModel> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            //Filter by name (enum type)
            if(name != null){
                predicates.add(criteriaBuilder.equal(root.get("name"), name));
            }
            //Filter by description (partial match)
            if(description != null && !description.isEmpty()){
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + description + "%"));
            }
            //Filter by amount
            if(amount != null){
                predicates.add(criteriaBuilder.equal(root.get("amount"), amount));
            }
            //Filter by date
            if(date != null) {
                predicates.add(criteriaBuilder.equal(root.get("date"), date));
            }
            //Combine predicates using And Logic
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        //Use the repository to execute the dynamic query
        return transactionIncomeRepository.findAll(specification);

    }


}
