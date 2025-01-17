package com.budget.control.backend.repository;

import com.budget.control.backend.model.TransactionIncomeModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionIncomeRepository extends JpaRepository<TransactionIncomeModel, UUID> {
}
