package com.budget.control.backend.repository;

import com.budget.control.backend.model.TransactionBenefitModel;
import com.budget.control.backend.type.TransactionBenefitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionBenefitRepository extends JpaRepository<TransactionBenefitModel, UUID>, JpaSpecificationExecutor<TransactionBenefitModel> {
    //Find transaction income by name and description and amount and date
    Optional<TransactionBenefitModel> findByNameAndDescriptionAndAmountAndDate(TransactionBenefitType name, String description, BigDecimal amount, LocalDate date);

    //Find transaction income by name or description or amount or date
    List<TransactionBenefitModel> findByNameOrDescriptionContainsOrAmountOrDateOrderByDateDesc(TransactionBenefitType name, String description, BigDecimal amount, LocalDate date);

}
