package com.budget.control.backend.controller;

import com.budget.control.backend.controller.dto.error.ErrorResponse;
import com.budget.control.backend.controller.dto.request.TransactionIncomeRequestDTO;
import com.budget.control.backend.controller.dto.response.TransactionIncomeResponseDTO;
import com.budget.control.backend.exception.DuplicatedRegisterException;
import com.budget.control.backend.exception.InvalidFieldException;
import com.budget.control.backend.exception.InvalidUUIDException;
import com.budget.control.backend.exception.NullFieldException;
import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.service.TransactionIncomeService;
import com.budget.control.backend.type.TransactionIncomeType;
import com.budget.control.backend.validator.UUIDValidator;
import com.budget.control.backend.validator.response.TransactionIncomeValidatorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/transaction-income")
public class TransactionIncomeController {

    //Dependency Injection
    private final TransactionIncomeService transactionIncomeService;
    private final TransactionIncomeValidatorResponse transactionIncomeValidatorResponse = new TransactionIncomeValidatorResponse();
    private final UUIDValidator uuidValidator;

    //Constructor Injection
    public TransactionIncomeController(TransactionIncomeService transactionIncomeService,
                                       UUIDValidator uuidValidator) {
        this.transactionIncomeService = transactionIncomeService;
        this.uuidValidator = uuidValidator;
    }

    //Saving an income transaction in the database
    @PostMapping
    public ResponseEntity<Object> saveIncomeTransaction(@RequestBody TransactionIncomeRequestDTO transactionIncomeRequestDTO) {
        try {
            //Map the DTO to the entity
            TransactionIncomeModel transactionIncomeEntity = transactionIncomeRequestDTO.mapToTransactionIncomeModel();
            //Save the income transaction
            transactionIncomeService.saveTransactionIncome(transactionIncomeEntity);

            //Return a response with the status code 201 and the URL location of the new resource in the header
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(transactionIncomeEntity.getId())
                    .toUri();
            return ResponseEntity.created(location).build();

        }catch (DuplicatedRegisterException e) {
            var errorDTO = ErrorResponse.conflictResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }catch (NullFieldException e) {
            var errorDTO = ErrorResponse.nullFieldResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    //Getting an income transaction by id
    @GetMapping("/{id}")
    public ResponseEntity<Object> getIncomeTransactionById(@PathVariable("id") String id) {
        try {
            //Validate the UUID format
            uuidValidator.validateUUID(id);

            //Get the income transaction by id from string to UUID
            var transactionIncomeID = UUID.fromString(id);
            //Receives an Optional of TransactionIncomeModel case the ID does not exist
            Optional<TransactionIncomeModel> transactionIncomeModelOptional = transactionIncomeService.getTransactionIncomeById(transactionIncomeID);
            //If the ID exists, map the entity to the DTO and return it
            if (transactionIncomeModelOptional.isPresent()) {
                TransactionIncomeModel transactionIncomeEntity = transactionIncomeModelOptional.get();
                TransactionIncomeResponseDTO transactionIncomeResponseDTO = new TransactionIncomeResponseDTO(
                        transactionIncomeEntity.getId(),
                        transactionIncomeEntity.getName(),
                        transactionIncomeEntity.getDescription(),
                        transactionIncomeEntity.getAmount(),
                        transactionIncomeEntity.getDate(),
                        transactionIncomeEntity.getCreatedAt(),
                        transactionIncomeEntity.getUpdatedAt(),
                        transactionIncomeEntity.getUserId()
                );
                return ResponseEntity.ok(transactionIncomeResponseDTO);
            }
            //If the ID does not exist, return a 404 status code
            return ResponseEntity.notFound().build();
        }catch (InvalidUUIDException e) {
            //If the UUID is invalid, return a 400 status code with an error message invalid format
            var errorDTO = ErrorResponse.invalidUUIDResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    //Getting income with params - name or description or amount or date
    @GetMapping
    public ResponseEntity<Object> getIncomeTransactionByParams(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "amount", required = false) BigDecimal amount,
            @RequestParam(value = "date", required = false) LocalDate date
    ) {
        try {
            TransactionIncomeType transactionIncomeType = null;

            // Verify if the param 'name' has been provided and if is valid
            if (name != null && !name.isEmpty()) {
                try {
                    //Try to convert the value 'name' to enum, if not possible, throw an error.
                    transactionIncomeType = TransactionIncomeType.valueOf(name.trim());
                } catch (IllegalArgumentException e) {
                    throw new InvalidFieldException("Invalid value provided for TransactionIncomeType: " + name);
                }
            }

            // If 'transactionIncomeType' has been defined, validates it
            if (transactionIncomeType != null) {
                transactionIncomeValidatorResponse.validate(transactionIncomeType); // Validates ENUM if it's provided
            }

            // Call the service to find the transactions with the provided params
            List<TransactionIncomeModel> result = transactionIncomeService
                    .getTransactionIncomeByNameOrDescriptionOrAmountOrDate(transactionIncomeType, description, amount, date);

            // Map the found fields to a List of DTO to return a response of fields
            List<TransactionIncomeResponseDTO> response = result.stream()
                    .map(transactionIncome -> new TransactionIncomeResponseDTO(
                            transactionIncome.getId(),
                            transactionIncome.getName(),
                            transactionIncome.getDescription(),
                            transactionIncome.getAmount(),
                            transactionIncome.getDate(),
                            transactionIncome.getCreatedAt(),
                            transactionIncome.getUpdatedAt(),
                            transactionIncome.getUserId()))
                    .toList();

            //If no fields were found. throw a Response Entity of Not Found 404
            if (response.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            //If fields were found, throw a Response Entity OK 200
            return ResponseEntity.ok(response);

        }
        //Invalid name field catch Exception
        catch (InvalidFieldException e) {
            var errorDTO = ErrorResponse.invalidFieldResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
        //Invalid date catch Exception
        catch (MethodArgumentTypeMismatchException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Expected format is YYYY-MM-DD.");
        }
        // Generic catch Exception
        catch (Exception e) {
            e.printStackTrace();  // Shows the error in the console
            var errorDTO = ErrorResponse.invalidFieldResponse("An unexpected error occurred.");
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

}
