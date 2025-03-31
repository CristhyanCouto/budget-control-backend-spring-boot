package com.budget.control.backend.controller;

import com.budget.control.backend.controller.dto.error.ErrorResponse;
import com.budget.control.backend.controller.dto.request.TransactionBenefitRequestDTO;
import com.budget.control.backend.controller.dto.request.UserRequestDTO;
import com.budget.control.backend.controller.dto.response.TransactionBenefitResponseDTO;
import com.budget.control.backend.controller.dto.response.UserResponseDTO;
import com.budget.control.backend.exception.*;
import com.budget.control.backend.mappers.UserMapper;
import com.budget.control.backend.model.TransactionBenefitModel;
import com.budget.control.backend.model.UserModel;
import com.budget.control.backend.service.UserService;
import com.budget.control.backend.type.TransactionBenefitType;
import com.budget.control.backend.type.UserRoleType;
import com.budget.control.backend.validator.UUIDValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    // Dependency Injection
    private final UserService userService;
    private final UUIDValidator uuidValidator;
    private final UserMapper userMapper;

    // Saving user
    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        try {
            // Map DTO to entity
            UserModel userModel = userMapper.toRequestEntity(userRequestDTO);

            // Save user
            userService.saveUser(userModel);

            // Return a response with the status code 201 and the URL location of the new resource in the header
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(userModel.getId())
                    .toUri();
            return ResponseEntity.created(location).build();
        } // Handle duplication error
        catch (DuplicatedRegisterException e) {
            var errorDTO = ErrorResponse.conflictResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }// Handle null field exception
        catch (NullFieldException e) {
            var errorDTO = ErrorResponse.nullFieldResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    // Get user by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") String id) {
        try {
            uuidValidator.validateUUID(id);

            UUID userID = UUID.fromString(id);

            Optional<UserModel> userModelOptional = userService.getUserById(userID);

            return userService
                    .getUserById(userID)
                    .map(user -> {
                        UserResponseDTO userResponseDTO = userMapper.toResponseDTO(user);
                        return ResponseEntity.ok(userResponseDTO);
                    }).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (InvalidUUIDException e) {
            var errorDTO = ErrorResponse.invalidFieldResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    // Get user with params
    @GetMapping
    public ResponseEntity<Object> getUserByParams(
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "cpf", required = false) String cpf,
            @RequestParam(value = "dateOfBirth", required = false) LocalDate dateOfBirth,
            @RequestParam(value = "role", required = false) String role
    ) {
        try {
            UserRoleType userRoleType = null;

            // Verify if the param 'name' has been provided and if is valid
            if (role != null && !role.isEmpty()) {
                try {
                    // Try to convert the value 'name' to enum, if not possible, throw an error.
                    userRoleType = UserRoleType.valueOf(role.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new InvalidFieldException("Invalid value provided for TransactionBenefitType: " + role);
                }
            }

            // Call the service to find the transactions with the provided params
            List<UserModel> result = userService
                    .getUserByFirstNameOrLastNameOrEmailOrCpfOrDateOfBirthOrRole(
                            firstName,
                            lastName,
                            email,
                            cpf,
                            dateOfBirth,
                            userRoleType
                    );

            // Map the found fields to a List of DTO to return a response of fields
            List<UserResponseDTO> response = result.stream()
                    .map(userMapper::toResponseDTO)
                    .toList();

            // If no fields were found, throw a Response Entity of Not Found 404
            if (response.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // If fields were found, throw a Response Entity OK 200
            return ResponseEntity.ok(response);
        }// Invalid name field catch Exception
        catch (InvalidFieldException e) {
            var errorDTO = ErrorResponse.invalidFieldResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }// Invalid date catch Exception
        catch (MethodArgumentTypeMismatchException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Expected format is YYYY-MM-DD.");
        }
    }

    // Update user id
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateTransactionBenefitById(
            @PathVariable("id") String id,
            @RequestBody UserRequestDTO userRequestDTO
    ) {
        try {
            // State the id from string
            UUID userID = UUID.fromString(id);

            // Retrieve existing transaction with an Optional
            Optional<UserModel> userModelOptional =
                    userService.getUserById(userID);

            if (userModelOptional.isPresent()) {
                // Creates a Model object with the found values
                UserModel existingTransaction = userModelOptional.get();

                // Update only fields provided in the request body
                if (userRequestDTO.firstName() != null) {
                    existingTransaction.setFirstName(userRequestDTO.firstName());
                }
                if (userRequestDTO.lastName() != null) {
                    existingTransaction.setLastName(userRequestDTO.lastName());
                }


                // Call service to update
                userService.updateUserById(existingTransaction);
                return ResponseEntity.noContent().build();
            }
            // Return 404 if the transaction is not found
            return ResponseEntity.notFound().build();
        }// Invalid name field catch Exception
        catch (InvalidFieldException e) {
            var errorDTO = ErrorResponse.invalidFieldResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid UUID: " + id);
        } catch (DuplicatedRegisterException e) {
            var errorDTO = ErrorResponse.conflictResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        } catch (Exception e) {
            var errorDTO = ErrorResponse.unexpectedErrorResponse("An unexpected error occurred.");
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }


    //Delete transaction benefit
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable("id") String id){
        try{
            // States de ID from string
            UUID userID = UUID.fromString(id);
            // Create an Optional Object from the id provided
            Optional<UserModel> userModelOptional = userService.getUserById(userID);

            // If the object does not return values, throw a not found
            if (userModelOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // If the Optional has the values of the searched object, deletes it and return an ok no content
            userService.deleteUserById(userModelOptional.get());
            return ResponseEntity.noContent().build();
        }
        // If the UUID has no value format, throw an invalid format error
        catch (IllegalArgumentException e) {
            var errorDTO = ErrorResponse.invalidUUIDResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        } catch (NonAuthorizedException e) {
            var errorDTO = ErrorResponse.standardResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }
}
