package com.budget.control.backend.repository;

import com.budget.control.backend.model.TransactionBenefitModel;
import com.budget.control.backend.type.TransactionBenefitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface TransactionBenefitRepository extends JpaRepository<TransactionBenefitModel, UUID>, JpaSpecificationExecutor<TransactionBenefitModel> {
    //Find transaction income by name and description and amount and date
    Optional<TransactionBenefitModel> findByNameAndDescriptionAndAmountAndDate(TransactionBenefitType name, String description, BigDecimal amount, LocalDate date);

}
