package com.budget.control.backend.controller.dto.response;

import com.budget.control.backend.model.UserModel;
import com.budget.control.backend.type.UserRoleType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String cpf,
        String email,
        String phone,
        String encryptedPassword,
        String userAuthenticated,
        UserRoleType role,
        String confirmationToken,
        String recoveryToken,
        LocalDateTime recoveryTokenExpiration,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime invitedAt,
        LocalDateTime confirmedAt,
        LocalDateTime lastLoginAt,
        LocalDateTime deletedAt
) {

}
