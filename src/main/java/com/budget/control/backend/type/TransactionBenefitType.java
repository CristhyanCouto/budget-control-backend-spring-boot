package com.budget.control.backend.type;

import com.budget.control.backend.deserializer.TransactionBenefitTypeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = TransactionBenefitTypeDeserializer.class)
public enum TransactionBenefitType {
    DENTAL_INSURANCE,
    GAS,
    GROCERY,
    GYM_MEMBERSHIP,
    HEALTH_INSURANCE,
    PET_INSURANCE
}
