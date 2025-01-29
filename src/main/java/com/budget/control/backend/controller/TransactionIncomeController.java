package com.budget.control.backend.controller;

import com.budget.control.backend.controller.dto.error.ErrorResponse;
import com.budget.control.backend.controller.dto.request.TransactionIncomeRequestDTO;
import com.budget.control.backend.controller.dto.response.TransactionIncomeResponseDTO;
import com.budget.control.backend.exception.*;
import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.service.TransactionIncomeService;
import com.budget.control.backend.type.TransactionIncomeType;
import com.budget.control.backend.validator.UUIDValidator;
import com.budget.control.backend.validator.request.TransactionIncomeValidatorRequest;
import com.budget.control.backend.validator.response.TransactionIncomeValidatorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
            // Map the DTO to the entity
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
            @RequestParam(value = "date", required = false) LocalDate date,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate
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

            // Validate date range
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new InvalidFieldException("Start date cannot be after end date.");
            }

            // Call the service to find the transactions with the provided params
            List<TransactionIncomeModel> result = transactionIncomeService
                    .getTransactionIncomeByNameOrDescriptionOrAmountOrDate(transactionIncomeType, description, amount, date, startDate, endDate);

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

    //Update transaction income by id
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateTransactionIncomeById(
            @PathVariable("id") String id,
            @RequestBody TransactionIncomeRequestDTO transactionIncomeRequestDTO){
        try{
            // State the id from string
            UUID transactionIncomeId = UUID.fromString(id);

            // Retrieve existing transaction with an Optional
            Optional<TransactionIncomeModel> transactionIncomeModelOptional =
                    transactionIncomeService.getTransactionIncomeById(transactionIncomeId);

            if (transactionIncomeModelOptional.isPresent()){

                // Creates a Model object with the found values
                TransactionIncomeModel existingTransaction = transactionIncomeModelOptional.get();

                //Update only fields provided in the request body
                if (transactionIncomeRequestDTO.name() != null) {

                        // Transaction Type to verify if the name is inside the ENUM
                        TransactionIncomeType transactionIncomeType = null;

                        //Passing the ENUM value to a String so it can be verified
                        String name = String.valueOf(transactionIncomeRequestDTO.name());

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

                        // UPDATE the name
                        existingTransaction.setName(transactionIncomeRequestDTO.name());

                }
                if (transactionIncomeRequestDTO.description() != null) {
                    existingTransaction.setDescription(transactionIncomeRequestDTO.description());
                }
                if (transactionIncomeRequestDTO.amount() != null) {
                    existingTransaction.setAmount(transactionIncomeRequestDTO.amount());
                }
                if (transactionIncomeRequestDTO.date() != null) {
                    try {
                        // Validate date format explicitly
                        LocalDate parsedDate = LocalDate.parse(transactionIncomeRequestDTO.date().toString());
                        existingTransaction.setDate(parsedDate);
                    } catch (DateTimeParseException e) {
                        return ResponseEntity.badRequest().body("Invalid date format. Expected format is YYYY-MM-DD.");
                    }
                }

                // Call service to update
                transactionIncomeService.updateTransactionIncome(existingTransaction);
                return ResponseEntity.noContent().build();
            }
            // Return 404 if the transaction is not found
            return ResponseEntity.notFound().build();

        } //Invalid name field catch Exception
        catch (InvalidFieldException e) {
            var errorDTO = ErrorResponse.invalidFieldResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid UUID: " + id);
        } catch (DuplicatedRegisterException e) {
            var errorDTO = ErrorResponse.conflictResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        } catch (Exception e) {
            var errorDTO = ErrorResponse.unexpectedErrorResponse("An unexpected error occurred.");
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    //Delete transaction income
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTransactionIncomeById(@PathVariable("id") String id){
        try{
            // States de ID from string to a var
            UUID transactionIncomeId = UUID.fromString(id);
            // Create an Optional Object from the id provided
            Optional<TransactionIncomeModel> transactionIncomeModelOptional = transactionIncomeService.getTransactionIncomeById(transactionIncomeId);

            // If the object does not return values, throw a not found
            if (transactionIncomeModelOptional.isEmpty()){
                return ResponseEntity.notFound().build();
            }

            // If the Optional has the values of the searched object, deletes it and return a ok no content
            transactionIncomeService.deleteTransactionIncomeById(transactionIncomeModelOptional.get());
            return ResponseEntity.noContent().build();
        }
        // If the UUID has no value format, throw invalid format error
        catch (IllegalArgumentException e) {
            var errorDTO = ErrorResponse.invalidUUIDResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        } catch (NonAuthorizedException e) {
            var errorDTO = ErrorResponse.standardResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

}
