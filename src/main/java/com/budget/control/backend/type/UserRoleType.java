package com.budget.control.backend.type;

import com.budget.control.backend.deserializer.UserRoleTypeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = UserRoleTypeDeserializer.class)
public enum UserRoleType {
    ADMIN,
    USER,
    GUEST,
    MODERATOR,
    MEMBER,
    SUBSCRIBER,
}
