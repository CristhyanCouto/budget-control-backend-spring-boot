package com.budget.control.backend.model;

import com.budget.control.backend.type.UserRoleType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_authentication")
@Data
@ToString
@EntityListeners(AuditingEntityListener.class)
public class UserModel {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "cpf", nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "encrypted_password", nullable = false)
    private String encryptedPassword;

    @Column(name = "user_authenticated")
    private Boolean userAuthenticated;

    @Column(name = "role", nullable = false)
    private UserRoleType role;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "recovery_token")
    private String recoveryToken;

    @Column(name = "recovery_token_expiration")
    private LocalDateTime recoveryTokenExpiration;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @LastModifiedDate
    @Column(name = "invited_at")
    private LocalDateTime invitedAt;

    @LastModifiedDate
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @LastModifiedDate
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonBackReference
    private GroupModel groupId;
}
