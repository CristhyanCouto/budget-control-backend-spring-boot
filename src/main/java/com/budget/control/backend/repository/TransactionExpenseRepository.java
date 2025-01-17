package com.budget.control.backend.repository;

import com.budget.control.backend.model.TransactionExpenseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionExpenseRepository extends JpaRepository<TransactionExpenseModel, UUID> {
}
