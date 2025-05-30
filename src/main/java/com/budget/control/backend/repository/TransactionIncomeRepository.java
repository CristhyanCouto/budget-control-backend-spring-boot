package com.budget.control.backend.repository;

import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.type.TransactionIncomeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface TransactionIncomeRepository extends JpaRepository<TransactionIncomeModel, UUID>, JpaSpecificationExecutor<TransactionIncomeModel> {
    //Find transaction income by name and description and amount and date
    Optional<TransactionIncomeModel> findByNameAndDescriptionAndAmountAndDate(TransactionIncomeType name, String description, BigDecimal amount, LocalDate date);

}
