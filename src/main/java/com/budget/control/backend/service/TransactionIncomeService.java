package com.budget.control.backend.service;

import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.repository.TransactionIncomeRepository;
import com.budget.control.backend.type.TransactionIncomeType;
import com.budget.control.backend.validator.UUIDValidator;
import com.budget.control.backend.validator.request.TransactionIncomeValidatorRequest;
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
    public void saveTransactionIncome(TransactionIncomeModel transactionIncomeModel) {
        transactionIncomeValidatorRequest.validate(transactionIncomeModel);
        transactionIncomeRepository.save(transactionIncomeModel);
    }

    //Get income transaction by id
    public Optional<TransactionIncomeModel> getTransactionIncomeById(UUID transactionIncomeID) {
        uuidValidator.validateUUID(transactionIncomeID.toString());
        return transactionIncomeRepository.findById(transactionIncomeID);
    }

    //Dynamic query to get income transaction by filters
    public List<TransactionIncomeModel> getTransactionIncomeByNameOrDescriptionOrAmountOrDate(
            TransactionIncomeType name, String description, BigDecimal amount, LocalDate date, LocalDate startDate, LocalDate endDate){

        //Dynamic query specification
        Specification<TransactionIncomeModel> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            //Filter by name (enum type)
            if(name != null){
                predicates.add(criteriaBuilder.equal(root.get("name"), name));
            }
            //Filter by description (partial match)
            if (description != null && !description.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        "%" + description.toLowerCase() + "%"
                ));
            }
            //Filter by amount
            if(amount != null){
                predicates.add(criteriaBuilder.equal(root.get("amount"), amount));
            }
            // Filter by exact date if no start or end date is provided
            if (date != null && startDate == null && endDate == null) {
                predicates.add(criteriaBuilder.equal(root.get("date"), date));
            }
            // Filter by date range if start or end date is provided
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate));
            }
            //Combine predicates using And Logic
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        //Use the repository to execute the dynamic query
        return transactionIncomeRepository.findAll(specification);

    }

    // Update transaction income
    public void updateTransactionIncome(TransactionIncomeModel transactionIncomeModel){
        if (transactionIncomeModel.getId() == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }
        transactionIncomeValidatorRequest.validate(transactionIncomeModel);
        transactionIncomeRepository.save(transactionIncomeModel);
    }


    // Delete transaction income by ID
    public void deleteTransactionIncomeById(TransactionIncomeModel transactionIncomeModel) {
        if (transactionIncomeModel.getId() != null){
            uuidValidator.validateUUID(transactionIncomeModel.getId().toString());
        }
        transactionIncomeRepository.delete(transactionIncomeModel);
    }
}
