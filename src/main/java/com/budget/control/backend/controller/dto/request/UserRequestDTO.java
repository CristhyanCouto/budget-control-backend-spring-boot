package com.budget.control.backend.controller.dto.request;

import com.budget.control.backend.type.UserRoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserRequestDTO(
        @NotNull(message = "Required field.")
        String firstName,
        @NotNull(message = "Required field.")
        String lastName,
        @NotNull(message = "Required field.")
        LocalDate birthDate,
        @NotNull(message = "Required field.")
        @CPF
        String cpf,
        @NotNull(message = "Required field.")
        @Email
        String email,
        String phone,
        @NotNull(message = "Required field.")
        String encryptedPassword,
        Boolean userAuthenticated,
        @NotNull(message = "Required field.")
        UserRoleType role,
        String confirmationToken,
        String recoveryToken,
        LocalDateTime recoveryTokenExpiration,
        UUID groupId
) {
}
