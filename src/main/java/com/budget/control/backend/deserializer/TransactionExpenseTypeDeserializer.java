package com.budget.control.backend.deserializer;

import com.budget.control.backend.exception.InvalidFieldException;
import com.budget.control.backend.type.TransactionExpenseType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Arrays;

public class TransactionExpenseTypeDeserializer extends JsonDeserializer<TransactionExpenseType> {

    @Override
    public TransactionExpenseType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().trim().toUpperCase();

        return Arrays.stream(TransactionExpenseType.values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidFieldException("Invalid value provided for TransactionExpenseType: " + value));
    }
}
