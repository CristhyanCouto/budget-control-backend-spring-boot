package com.budget.control.backend.service;

import com.budget.control.backend.model.TransactionExpenseModel;
import com.budget.control.backend.repository.TransactionExpenseRepository;
import com.budget.control.backend.type.TransactionExpenseType;
import com.budget.control.backend.validator.UUIDValidator;
import com.budget.control.backend.validator.request.TransactionExpenseValidatorRequest;
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
public class TransactionExpenseService {

    // Dependency Injection
    private final TransactionExpenseRepository transactionExpenseRepository;
    private final TransactionExpenseValidatorRequest transactionExpenseValidatorRequest;
    private final UUIDValidator uuidValidator;

    public TransactionExpenseService(
            TransactionExpenseRepository transactionExpenseRepository,
            TransactionExpenseValidatorRequest transactionExpenseValidatorRequest,
            UUIDValidator uuidValidator) {
        this.transactionExpenseRepository = transactionExpenseRepository;
        this.transactionExpenseValidatorRequest = transactionExpenseValidatorRequest;
        this.uuidValidator = uuidValidator;
    }

    // Save expense transaction
    public void saveTransactionExpense(TransactionExpenseModel transactionExpenseModel) {
        transactionExpenseValidatorRequest.validate(transactionExpenseModel);
        transactionExpenseRepository.save(transactionExpenseModel);
    }

    // Get expense transaction by id
    public Optional<TransactionExpenseModel> getTransactionExpenseById(UUID transactionExpenseId) {
        uuidValidator.validateUUID(transactionExpenseId.toString());
        return transactionExpenseRepository.findById(transactionExpenseId);
    }

    // Dynamic query to get expense transaction by filters
    public List<TransactionExpenseModel> getTransactionExpenseByNameOrDescriptionOrAmountOrDateOrRecurrent(
            TransactionExpenseType name, String description, BigDecimal amount, LocalDate date, LocalDate startDate, LocalDate endDate, Boolean recurrent
    ) {
        // Dynamic query specification
        Specification<TransactionExpenseModel> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by name (enum type)
            if (name != null) {
                predicates.add(criteriaBuilder.equal(root.get("name"), name));
            }
            // Filter by description (partial match)
            if (description != null && !description.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        "%" + description.toLowerCase() + "%"
                ));
            }
            // Filter by amount
            if (amount != null) {
                predicates.add(criteriaBuilder.equal(root.get("amount"), amount));
            }
            // Filter by exact date if no start or end date is provided
            if (date != null && startDate == null & endDate == null) {
                predicates.add(criteriaBuilder.equal(root.get("date"), date));
            }
            // Filter by date range if start or end date is provided
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate));
            }
            // Filter by recurrent
            if (recurrent != null) {
                predicates.add(criteriaBuilder.equal(root.get("recurrent"), recurrent));
            }
            // Combine predicates using And Logic
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // Use the repository to execute the dynamic query
        return transactionExpenseRepository.findAll(specification);
    }

    // Update transaction expense
    public void updateTransactionExpense (TransactionExpenseModel transactionExpenseModel) {
        if (transactionExpenseModel.getId() == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null.");
        }
        transactionExpenseValidatorRequest.validate(transactionExpenseModel);
        transactionExpenseRepository.save(transactionExpenseModel);
    }

    // Delete transaction expense by ID
    public void deleteTransactionExpenseById(TransactionExpenseModel transactionExpenseModel) {
        if (transactionExpenseModel.getId() != null) {
            uuidValidator.validateUUID(transactionExpenseModel.getId().toString());
        }
        transactionExpenseRepository.delete(transactionExpenseModel);
    }
}
