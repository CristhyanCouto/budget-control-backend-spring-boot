package com.budget.control.backend.controller;

import com.budget.control.backend.controller.dto.error.ErrorResponse;
import com.budget.control.backend.controller.dto.request.TransactionIncomeRequestDTO;
import com.budget.control.backend.controller.dto.response.TransactionIncomeResponseDTO;
import com.budget.control.backend.exception.*;
import com.budget.control.backend.mappers.TransactionIncomeMapper;
import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.service.TransactionIncomeService;
import com.budget.control.backend.type.TransactionIncomeType;
import com.budget.control.backend.validator.UUIDValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TransactionIncomeController {

    //Dependency Injection
    private final TransactionIncomeService transactionIncomeService;
    private final UUIDValidator uuidValidator;
    private final TransactionIncomeMapper transactionIncomeMapper;

    //Saving an income transaction in the database
    @PostMapping
    public ResponseEntity<Object> saveIncomeTransaction(@RequestBody @Valid TransactionIncomeRequestDTO transactionIncomeRequestDTO) {
        try {
            // Map the DTO to the entity
            TransactionIncomeModel transactionIncomeModel = transactionIncomeMapper.toRequestEntity(transactionIncomeRequestDTO);

            //Save the income transaction
            transactionIncomeService.saveTransactionIncome(transactionIncomeModel);

            //Return a response with the status code 201 and the URL location of the new resource in the header
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(transactionIncomeModel.getId())
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
    public ResponseEntity<?> getIncomeTransactionById(@PathVariable("id") String id) {
        try {
            //Validate the UUID format
            uuidValidator.validateUUID(id);

            //Get the income transaction by id from string to UUID
            UUID transactionIncomeID = UUID.fromString(id);

            //Receives an Optional of TransactionIncomeModel case the ID does not exist
            Optional<TransactionIncomeModel> transactionIncomeModelOptional = transactionIncomeService.getTransactionIncomeById(transactionIncomeID);

            return transactionIncomeService
                    .getTransactionIncomeById(transactionIncomeID)
                    .map(transaction -> {
                        TransactionIncomeResponseDTO transactionIncomeResponseDTO = transactionIncomeMapper.toResponseDTO(transaction);
                        return ResponseEntity.ok(transactionIncomeResponseDTO);
                    }).orElseGet(() -> ResponseEntity.notFound().build());
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
                    transactionIncomeType = TransactionIncomeType.valueOf(name.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new InvalidFieldException("Invalid value provided for TransactionIncomeType: " + name);
                }
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
                    .map(transactionIncomeMapper::toResponseDTO)
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

            // If the Optional has the values of the searched object, deletes it and return an ok no content
            transactionIncomeService.deleteTransactionIncomeById(transactionIncomeModelOptional.get());
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
