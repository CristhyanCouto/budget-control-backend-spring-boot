package com.budget.control.backend.service;

import com.budget.control.backend.model.UserModel;
import com.budget.control.backend.repository.UserRepository;
import com.budget.control.backend.type.UserRoleType;
import com.budget.control.backend.validator.UUIDValidator;
import com.budget.control.backend.validator.request.UserValidatorRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    //Dependency Injection
    private final UserRepository userRepository;
    private final UserValidatorRequest userValidatorRequest;
    private final UUIDValidator uuidValidator;
    private final PasswordEncoder passwordEncoder;

    //Constructor Injection
    public UserService(
            UserRepository userRepository,
            UserValidatorRequest userValidatorRequest,
            UUIDValidator uuidValidator,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userValidatorRequest = userValidatorRequest;
        this.uuidValidator = uuidValidator;
        this.passwordEncoder = passwordEncoder;
    }

    // Save user
    public void saveUser(UserModel userModel) {
        userValidatorRequest.validate(userModel);

        // Encrypt password
        String hashedPassword = passwordEncoder.encode(userModel.getEncryptedPassword());
        userModel.setEncryptedPassword(hashedPassword);
        userRepository.save(userModel);
    }

    // Get user by id
    public Optional<UserModel> getUserById(UUID userID){
        uuidValidator.validateUUID(userID.toString());
        return  userRepository.findById(userID);
    }

    // Dynamic query to get user by filters
    public List<UserModel> getUserByFirstNameOrLastNameOrEmailOrCpfOrDateOfBirthOrRole(
            String firstName, String lastName, String email, String cpf, LocalDate dateOfBirth, UserRoleType role
    ){
        // Dynamic query specification
        Specification<UserModel> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by first name
            if(firstName != null){
                predicates.add(criteriaBuilder.equal(root.get("firstName"), firstName));
            }
            // Filter by last name
            if(lastName != null){
                predicates.add(criteriaBuilder.equal(root.get("lastName"), lastName));
            }
            // Filter by email
            if(email != null){
                predicates.add(criteriaBuilder.equal(root.get("email"), email));
            }
            // Filter by CPF
            if(cpf != null){
                predicates.add(criteriaBuilder.equal(root.get("cpf"), cpf));
            }
            // Filter by date of birth
            if(dateOfBirth != null){
                predicates.add(criteriaBuilder.equal(root.get("birthDate"), dateOfBirth));
            }
            // Filter by role
            if(role != null){
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return userRepository.findAll(specification);
    }

    public void updateUserById(UserModel userModel){
        if (userModel.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        userValidatorRequest.validate(userModel);
        userRepository.save(userModel);
    }

    // Delete user
    public void deleteUserById(UserModel userModel){
        if (userModel.getId() != null) {
            uuidValidator.validateUUID(userModel.getId().toString());
        }
        userRepository.delete(userModel);
    }

    // Validate password
    public boolean validatePassword(String rawPassword, String encryptedPassword) {
        return passwordEncoder.matches(rawPassword, encryptedPassword);
    }

    public Optional<UserModel> authenticateUser(String email, String rawPassword) {
        Optional<UserModel> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            UserModel user = userOptional.get();

            if (validatePassword(rawPassword, user.getEncryptedPassword())) {
                return Optional.of(user); // Wrong password
            }
        }
        return Optional.empty(); // Wrong password or user not found
    }


}
