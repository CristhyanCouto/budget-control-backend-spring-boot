package com.budget.control.backend.validator.response;

import com.budget.control.backend.exception.InvalidFieldException;
import com.budget.control.backend.type.TransactionIncomeType;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class TransactionIncomeValidatorResponse {

    public void validate(TransactionIncomeType name) {

        if (!EnumSet.allOf(TransactionIncomeType.class).contains(name)) {
            throw new InvalidFieldException("Transaction income name does not exist in TransactionIncomeType.");
        }
    }



}
