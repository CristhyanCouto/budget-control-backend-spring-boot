package com.budget.control.backend.validator.request;

import com.budget.control.backend.exception.DuplicatedRegisterException;
import com.budget.control.backend.exception.NullFieldException;
import com.budget.control.backend.model.UserModel;
import com.budget.control.backend.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserValidatorRequest {

    //Dependency Injection
    private final UserRepository userRepository;

    // Constructor
    public UserValidatorRequest(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    // Validate Method
    public void validate(UserModel userModel) {
        if (isUserDuplicated(userModel)) {
            throw new DuplicatedRegisterException("User already exists");
        }
        isUserNull(userModel);
    }

    // Check if the user is duplicated
    private boolean isUserDuplicated(UserModel userModel){
        Optional<UserModel> userModelOptional = userRepository.findByFirstNameAndLastNameAndCpfAndEmail(
                userModel.getFirstName(),
                userModel.getLastName(),
                userModel.getCpf(),
                userModel.getEmail()
        );
        if (userModel.getId() == null) {
            return userModelOptional.isPresent();
        }
        return userModelOptional.isPresent() && !userModel.getId().equals(userModelOptional.get().getId());
    }

    private void isUserNull(UserModel userModel) {
        if (userModel.getFirstName() == null) {
            throw new NullFieldException("User first name cannot be null");
        }
        if (userModel.getLastName() == null) {
            throw new NullFieldException("User last name cannot be null");
        }
        if (userModel.getCpf() == null) {
            throw new NullFieldException("User CPF cannot be null");
        }
        if (userModel.getEmail() == null) {
            throw new NullFieldException("User email cannot be null");
        }
    }
}
