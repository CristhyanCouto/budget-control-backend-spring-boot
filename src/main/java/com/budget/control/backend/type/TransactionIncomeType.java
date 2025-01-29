package com.budget.control.backend.type;

import com.budget.control.backend.deserializer.TransactionIncomeTypeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = TransactionIncomeTypeDeserializer.class)
public enum TransactionIncomeType {
    BONUS,
    FREELANCE,
    SALARY,
    SALE,
    REAL_STATE
}
