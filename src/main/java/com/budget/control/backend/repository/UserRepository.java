package com.budget.control.backend.repository;

import com.budget.control.backend.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID>, JpaSpecificationExecutor<UserModel> {

    Optional<UserModel> findByFirstNameAndLastNameAndCpfAndEmail(String firstName, String lastName, String cpf, String email);
}
