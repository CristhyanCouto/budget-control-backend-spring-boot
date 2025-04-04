package com.budget.control.backend.controller;

import com.budget.control.backend.controller.dto.error.ErrorResponse;
import com.budget.control.backend.controller.dto.request.LoginRequestDTO;
import com.budget.control.backend.controller.dto.request.UserRequestDTO;
import com.budget.control.backend.controller.dto.response.UserResponseDTO;
import com.budget.control.backend.exception.*;
import com.budget.control.backend.mappers.UserMapper;
import com.budget.control.backend.model.UserModel;
import com.budget.control.backend.service.UserService;
import com.budget.control.backend.type.UserRoleType;
import com.budget.control.backend.validator.UUIDValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UUIDValidator uuidValidator;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        try {
            UserModel userModel = userMapper.toRequestEntity(userRequestDTO);
            userService.saveUser(userModel);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(userModel.getId())
                    .toUri();
            return ResponseEntity.created(location).build();
        } catch (DuplicatedRegisterException e) {
            var errorDTO = ErrorResponse.conflictResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        } catch (NullFieldException e) {
            var errorDTO = ErrorResponse.nullFieldResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") String id) {
        try {
            uuidValidator.validateUUID(id);
            UUID userID = UUID.fromString(id);
            return userService.getUserById(userID)
                    .map(user -> {
                        UserResponseDTO userResponseDTO = userMapper.toResponseDTO(user);
                        return ResponseEntity.ok(userResponseDTO);
                    }).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (InvalidUUIDException e) {
            var errorDTO = ErrorResponse.invalidFieldResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

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
            if (role != null && !role.isEmpty()) {
                try {
                    userRoleType = UserRoleType.valueOf(role.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new InvalidFieldException("Invalid value provided for UserRoleType: " + role);
                }
            }
            List<UserModel> result = userService
                    .getUserByFirstNameOrLastNameOrEmailOrCpfOrDateOfBirthOrRole(
                            firstName, lastName, email, cpf, dateOfBirth, userRoleType);
            List<UserResponseDTO> response = result.stream()
                    .map(userMapper::toResponseDTO)
                    .toList();
            if (response.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(response);
        } catch (InvalidFieldException e) {
            var errorDTO = ErrorResponse.invalidFieldResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        } catch (MethodArgumentTypeMismatchException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Expected format is YYYY-MM-DD.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUserById(
            @PathVariable("id") String id,
            @RequestBody UserRequestDTO userRequestDTO
    ) {
        try {
            UUID userID = UUID.fromString(id);
            Optional<UserModel> userModelOptional = userService.getUserById(userID);
            if (userModelOptional.isPresent()) {
                UserModel existingUser = userModelOptional.get();
                if (userRequestDTO.firstName() != null) {
                    existingUser.setFirstName(userRequestDTO.firstName());
                }
                if (userRequestDTO.lastName() != null) {
                    existingUser.setLastName(userRequestDTO.lastName());
                }
                userService.updateUserById(existingUser);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (InvalidFieldException e) {
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable("id") String id) {
        try {
            UUID userID = UUID.fromString(id);
            Optional<UserModel> userModelOptional = userService.getUserById(userID);
            if (userModelOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            userService.deleteUserById(userModelOptional.get());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            var errorDTO = ErrorResponse.invalidUUIDResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        } catch (NonAuthorizedException e) {
            var errorDTO = ErrorResponse.standardResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    @PostMapping("/auth")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Optional<UserModel> user = userService.authenticateUser(loginRequestDTO.email(), loginRequestDTO.password());

        if (user.isPresent()) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username or password is incorrect");
        }
    }


}