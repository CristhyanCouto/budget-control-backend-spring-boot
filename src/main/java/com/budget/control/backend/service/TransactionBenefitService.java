package com.budget.control.backend.service;

import com.budget.control.backend.model.TransactionBenefitModel;
import com.budget.control.backend.repository.TransactionBenefitRepository;
import com.budget.control.backend.type.TransactionBenefitType;
import com.budget.control.backend.validator.UUIDValidator;
import com.budget.control.backend.validator.request.TransactionBenefitValidatorRequest;
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
public class TransactionBenefitService {

    //Dependency Injection
    private final TransactionBenefitRepository transactionBenefitRepository;
    private final TransactionBenefitValidatorRequest transactionBenefitValidatorRequest;
    private final UUIDValidator uuidValidator;

    //Constructor Injection
    public TransactionBenefitService(
            TransactionBenefitRepository transactionBenefitRepository,
            TransactionBenefitValidatorRequest transactionBenefitValidatorRequest,
            UUIDValidator uuidValidator
    ) {
        this.transactionBenefitRepository = transactionBenefitRepository;
        this.transactionBenefitValidatorRequest =  transactionBenefitValidatorRequest;
        this.uuidValidator = uuidValidator;
    }

    // Save benefit transaction
    public void saveTransactionBenefit(TransactionBenefitModel transactionBenefitModel) {
        transactionBenefitValidatorRequest.validate(transactionBenefitModel);
        transactionBenefitRepository.save(transactionBenefitModel);
    }

    // Get benefit transaction by id
    public Optional<TransactionBenefitModel> getTransactionBenefitById(UUID transactionBenefitID) {
        uuidValidator.validateUUID(transactionBenefitID.toString());
        return transactionBenefitRepository.findById(transactionBenefitID);
    }

    // Dynamic query to get benefit transaction by filters
    public List<TransactionBenefitModel> getTransactionBenefitByNameOrDescriptionOrAmountOrDate (
            TransactionBenefitType name, String description, BigDecimal amount, LocalDate date, LocalDate startDate, LocalDate endDate
    ) {
        // Dynamic query specification
        Specification<TransactionBenefitModel> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by name (enum type)
            if(name != null){
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

        // Use the repository to execute the dynamic query
        return transactionBenefitRepository.findAll(specification);
    }

    // Update transaction benefit
    public void updateTransactionBenefit(TransactionBenefitModel transactionBenefitModel) {
        if (transactionBenefitModel.getId() == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }
        transactionBenefitValidatorRequest.validate(transactionBenefitModel);
        transactionBenefitRepository.save(transactionBenefitModel);
    }

    // Delete transaction benefit by ID
    public void deleteTransactionBenefitById(TransactionBenefitModel transactionBenefitModel) {
        if (transactionBenefitModel.getId() != null ) {
            uuidValidator.validateUUID(transactionBenefitModel.getId().toString());
        }
        transactionBenefitRepository.delete(transactionBenefitModel);
    }
}
