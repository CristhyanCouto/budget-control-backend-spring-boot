package com.budget.control.backend.controller;

import com.budget.control.backend.controller.dto.error.ErrorResponse;
import com.budget.control.backend.controller.dto.request.TransactionExpenseRequestDTO;
import com.budget.control.backend.controller.dto.response.TransactionExpenseResponseDTO;
import com.budget.control.backend.exception.*;
import com.budget.control.backend.model.TransactionExpenseModel;
import com.budget.control.backend.service.TransactionExpenseService;
import com.budget.control.backend.type.TransactionExpenseType;
import com.budget.control.backend.validator.UUIDValidator;
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
@RequestMapping("/transaction-expense")
public class TransactionExpenseController {

    // Dependency Injection
    private final TransactionExpenseService transactionExpenseService;
    private final UUIDValidator uuidValidator;

    public TransactionExpenseController(TransactionExpenseService transactionExpenseService,
                                        UUIDValidator uuidValidator) {
        this.transactionExpenseService = transactionExpenseService;
        this.uuidValidator = uuidValidator;
    }

    // Saving an expense transaction in the database
    @PostMapping
    public ResponseEntity<Object> saveExpenseTransaction(@RequestBody TransactionExpenseRequestDTO transactionExpenseRequestDTO) {
        try {
            // Map the DTO to the entity
            TransactionExpenseModel transactionExpenseEntity = transactionExpenseRequestDTO.mapToTransactionExpenseModel();

            // Save the expense transaction
            transactionExpenseService.saveTransactionExpense(transactionExpenseEntity);

            // Return a response with the status code 201 and the URL location of the new resource in the header
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(transactionExpenseEntity.getId())
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

    // Getting an expense transaction by id
    @GetMapping("/{id}")
    public ResponseEntity<Object> getExpenseTransactionById(@PathVariable("id") String id) {
        try {
            // Validate the UUID format
            uuidValidator.validateUUID(id);

            // Get the expense transaction by id from string to UUID
            UUID transactionExpenseId = UUID.fromString(id);

            // Receives an Optional of TransactionExpenseModel case the ID does not exist
            Optional<TransactionExpenseModel> transactionExpenseModelOptional = transactionExpenseService.getTransactionExpenseById(transactionExpenseId);

            // If the ID exists, map the entity to the DTO and return it
            if (transactionExpenseModelOptional.isPresent()) {
                TransactionExpenseModel transactionExpenseEntity = transactionExpenseModelOptional.get();
                TransactionExpenseResponseDTO transactionExpenseResponseDTO = new TransactionExpenseResponseDTO(
                        transactionExpenseEntity.getId(),
                        transactionExpenseEntity.getName(),
                        transactionExpenseEntity.getDescription(),
                        transactionExpenseEntity.getAmount(),
                        transactionExpenseEntity.getDate(),
                        transactionExpenseEntity.getRecurrent(),
                        transactionExpenseEntity.getCreatedAt(),
                        transactionExpenseEntity.getUpdatedAt(),
                        transactionExpenseEntity.getUserId()
                );
                return ResponseEntity.ok(transactionExpenseResponseDTO);
            }
            // If the ID does not exist, return 404 status code
            return ResponseEntity.notFound().build();
        }catch (InvalidUUIDException e) {
            //If the UUID is invalid, return a 400 status code with an error message invalid format
            var errorDTO = ErrorResponse.invalidUUIDResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    // Getting expense with params - name or description or amount or date ou recurrent
    @GetMapping
    public ResponseEntity<Object> getExpenseTransactionByParams(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "amount", required = false) BigDecimal amount,
            @RequestParam(value = "date", required = false) LocalDate date,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "recurrent", required = false) Boolean recurrent
    ) {
        try {
            TransactionExpenseType transactionExpenseType = null;

            // Verify if the param 'name' has been provided and if is valid
            if (name != null && !name.isEmpty()) {
                try {
                    // Try to convert the value 'name' to enum, if not possible, throw an error.
                    transactionExpenseType = TransactionExpenseType.valueOf(name.trim().toUpperCase());
                }catch (IllegalArgumentException e) {
                    throw new InvalidFieldException("Invalid value provided for TransactionExpenseType: " + name);
                }
            }

            // Validate date range
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new InvalidFieldException("Start date cannot be after end date.");
            }

            // Call the service to find the transactions with the provided params
            List<TransactionExpenseModel> result = transactionExpenseService
                    .getTransactionExpenseByNameOrDescriptionOrAmountOrDateOrRecurrent(transactionExpenseType, description, amount, date, startDate, endDate, recurrent);

            // Map the found fields to a List of DTO to return a response of fields
            List<TransactionExpenseResponseDTO> response = result.stream()
                    .map(transactionExpense -> new TransactionExpenseResponseDTO(
                            transactionExpense.getId(),
                            transactionExpense.getName(),
                            transactionExpense.getDescription(),
                            transactionExpense.getAmount(),
                            transactionExpense.getDate(),
                            transactionExpense.getRecurrent(),
                            transactionExpense.getCreatedAt(),
                            transactionExpense.getUpdatedAt(),
                            transactionExpense.getUserId()
                    ))
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
    }

    //Update transaction income by id
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateTransactionExpenseById(
            @PathVariable("id") String id,
            @RequestBody TransactionExpenseRequestDTO transactionExpenseRequestDTO
    ) {
        try {
            // State the id from string
            UUID transactionExpenseId = UUID.fromString(id);

            // Retrieve existing transaction with an Optional
            Optional<TransactionExpenseModel> transactionExpenseModelOptional =
                    transactionExpenseService.getTransactionExpenseById(transactionExpenseId);

            if (transactionExpenseModelOptional.isPresent()) {

                // Creates a Model object with the found values
                TransactionExpenseModel existingTransaction = transactionExpenseModelOptional.get();

                // Update only fields provided in the request body
                if (transactionExpenseRequestDTO.name() != null) {
                    existingTransaction.setName(transactionExpenseRequestDTO.name());
                }
                if (transactionExpenseRequestDTO.description() != null) {
                    existingTransaction.setDescription(transactionExpenseRequestDTO.description());
                }
                if (transactionExpenseRequestDTO.amount() != null) {
                    existingTransaction.setAmount(transactionExpenseRequestDTO.amount());
                }
                if (transactionExpenseRequestDTO.date() != null) {
                    try {
                        // Validate date format explicitly
                        LocalDate parseDate = LocalDate.parse(transactionExpenseRequestDTO.date().toString());
                        existingTransaction.setDate(parseDate);
                    } catch (DateTimeParseException e) {
                        return ResponseEntity.badRequest().body("Invalid date format. Expected format is YYYY-MM-DD.");
                    }
                }
                if (transactionExpenseRequestDTO.recurrent() != null) {
                    existingTransaction.setRecurrent(transactionExpenseRequestDTO.recurrent());
                }

                // Call the service to update
                transactionExpenseService.updateTransactionExpense(existingTransaction);
                return ResponseEntity.noContent().build();
            }
            // Return 404 if the transaction is not found
            return ResponseEntity.notFound().build();
        }//Invalid name field catch Exception
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

    // Delete transaction expense
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTransactionExpenseById(@PathVariable("id") String id) {
        try {
            // States the ID from string to a var
            UUID transactionExpenseId = UUID.fromString(id);
            //Create an Optional Object from the provided id
            Optional<TransactionExpenseModel> transactionExpenseModelOptional = transactionExpenseService.getTransactionExpenseById(transactionExpenseId);

            // If the object does not return values, throw a not found
            if (transactionExpenseModelOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // If the Optional has the values of the searched object, deletes it and return an ok no content
            transactionExpenseService.deleteTransactionExpenseById(transactionExpenseModelOptional.get());
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
