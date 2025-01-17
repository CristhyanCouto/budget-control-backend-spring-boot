package com.budget.control.backend.repository;

import com.budget.control.backend.model.TransactionBenefitModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionBenefitRepository extends JpaRepository<TransactionBenefitModel, UUID> {
}
