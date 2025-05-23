package com.budget.control.backend.deserializer;

import com.budget.control.backend.exception.InvalidFieldException;
import com.budget.control.backend.type.TransactionBenefitType;
import com.budget.control.backend.type.UserRoleType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Arrays;

public class UserRoleTypeDeserializer extends JsonDeserializer<UserRoleType> {

    @Override
    public UserRoleType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().trim().toUpperCase();

        return Arrays.stream(UserRoleType.values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->  new InvalidFieldException("Invalid value provided for UserRoleType: " + value));
    }
}
