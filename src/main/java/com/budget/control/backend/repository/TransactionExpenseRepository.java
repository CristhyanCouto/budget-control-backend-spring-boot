package com.budget.control.backend.repository;

import com.budget.control.backend.model.TransactionExpenseModel;
import com.budget.control.backend.type.TransactionExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface TransactionExpenseRepository extends JpaRepository<TransactionExpenseModel, UUID>, JpaSpecificationExecutor<TransactionExpenseModel> {
    //Find transaction expense by name and description and amount and date and recurrent
    Optional<TransactionExpenseModel> findByNameAndDescriptionAndAmountAndDateAndRecurrent(TransactionExpenseType name, String description, BigDecimal amount, LocalDate date, Boolean recurrent);
}
