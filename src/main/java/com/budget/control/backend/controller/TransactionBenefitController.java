package com.budget.control.backend.controller;

import com.budget.control.backend.controller.dto.error.ErrorResponse;
import com.budget.control.backend.controller.dto.request.TransactionBenefitRequestDTO;
import com.budget.control.backend.controller.dto.response.TransactionBenefitResponseDTO;
import com.budget.control.backend.exception.*;
import com.budget.control.backend.mappers.TransactionBenefitMapper;
import com.budget.control.backend.model.TransactionBenefitModel;
import com.budget.control.backend.service.TransactionBenefitService;
import com.budget.control.backend.type.TransactionBenefitType;
import com.budget.control.backend.validator.UUIDValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/transaction-benefit")
@RequiredArgsConstructor
public class TransactionBenefitController {

    //Dependency Injection
    private final TransactionBenefitService transactionBenefitService;
    private final UUIDValidator uuidValidator;
    private final TransactionBenefitMapper transactionBenefitMapper;

    //Saving a benefit transaction in the database
    @PostMapping
    public ResponseEntity<Object> saveBenefitTransaction(@RequestBody @Valid TransactionBenefitRequestDTO transactionBenefitRequestDTO) {
        try{
            // Map the DTO to the entity
            TransactionBenefitModel transactionBenefitModel = transactionBenefitMapper.toRequestEntity(transactionBenefitRequestDTO);

            // Save the benefit transaction
            transactionBenefitService.saveTransactionBenefit(transactionBenefitModel);

            // Return a response with the status code 201 and the URL location of the new resource in the header
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(transactionBenefitModel.getId())
                    .toUri();
            return ResponseEntity.created(location).build();

        }// Handle duplication error
        catch (DuplicatedRegisterException e) {
            var errorDTO = ErrorResponse.conflictResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }// Handle null field exception
        catch (NullFieldException e) {
            var errorDTO = ErrorResponse.nullFieldResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    // Getting a benefit transaction by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getBenefitTransactionById(@PathVariable("id") String id) {

        try {
            // Validate the UUID format
            uuidValidator.validateUUID(id);


            // Get the benefit transaction by id from string to UUID
            UUID transactionIncomeID = UUID.fromString(id);

            // Receives an Optional of TransactionBenefitModel case the ID does not exist
            Optional<TransactionBenefitModel> transactionBenefitModelOptional = transactionBenefitService.getTransactionBenefitById(transactionIncomeID);

            // If the ID exists, map the entity to the DTO and return it
            return transactionBenefitService
                    .getTransactionBenefitById(transactionIncomeID)
                    .map(transaction -> {
                        TransactionBenefitResponseDTO transactionBenefitResponseDTO = transactionBenefitMapper.toResponseDTO(transaction);
                        return ResponseEntity.ok(transactionBenefitResponseDTO);
                    }).orElseGet(() -> ResponseEntity.notFound().build());
        }catch (InvalidUUIDException e) {
            var errorDTO = ErrorResponse.invalidFieldResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    // Getting benefit with params - name or description or amount or date
    @GetMapping
    public ResponseEntity<Object> getBenefitTransactionByParams(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "amount", required = false) BigDecimal amount,
            @RequestParam(value = "date", required = false) LocalDate date,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate
    ) {
        try {
            TransactionBenefitType transactionBenefitType = null;

            // Verify if the param 'name' has been provided and if is valid
            if (name != null && !name.isEmpty()) {
                try {
                    // Try to convert the value 'name' to enum, if not possible, throw an error.
                    transactionBenefitType = TransactionBenefitType.valueOf(name.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new InvalidFieldException("Invalid value provided for TransactionBenefitType: " + name);
                }
            }

            // Validate date range
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new InvalidFieldException("Start date cannot be after end date.");
            }

            // Call the service to find the transactions with the provided params
            List<TransactionBenefitModel> result = transactionBenefitService
                    .getTransactionBenefitByNameOrDescriptionOrAmountOrDate(transactionBenefitType, description, amount, date, startDate, endDate);

            // Map the found fields to a List of DTO to return a response of fields
            List<TransactionBenefitResponseDTO> response = result.stream()
                    .map(transactionBenefitMapper::toResponseDTO)
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

    // Update transaction benefit by id
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateTransactionBenefitById(
            @PathVariable("id") String id,
            @RequestBody TransactionBenefitRequestDTO transactionBenefitRequestDTO
    ) {
        try {
            // State the id from string
            UUID transactionBenefitId = UUID.fromString(id);

            // Retrieve existing transaction with an Optional
            Optional<TransactionBenefitModel> transactionBenefitModelOptional =
                    transactionBenefitService.getTransactionBenefitById(transactionBenefitId);

            if (transactionBenefitModelOptional.isPresent()) {
                // Creates a Model object with the found values
                TransactionBenefitModel existingTransaction = transactionBenefitModelOptional.get();

                // Update only fields provided in the request body
                if (transactionBenefitRequestDTO.name() != null) {
                    existingTransaction.setName(transactionBenefitRequestDTO.name());
                }
                if (transactionBenefitRequestDTO.description() != null) {
                    existingTransaction.setDescription(transactionBenefitRequestDTO.description());
                }
                if (transactionBenefitRequestDTO.amount() != null) {
                    existingTransaction.setAmount(transactionBenefitRequestDTO.amount());
                }
                if (transactionBenefitRequestDTO.date() != null) {
                    try {
                        // Validate date format explicitly
                        LocalDate parseDate = LocalDate.parse(transactionBenefitRequestDTO.date().toString());
                        existingTransaction.setDate(parseDate);
                    } catch (DateTimeParseException e) {
                        return ResponseEntity.badRequest().body("Invalid date format. Expected format is YYYY-MM-DD.");
                    }
                }

                // Call service to update
                transactionBenefitService.updateTransactionBenefit(existingTransaction);
                return ResponseEntity.noContent().build();
            }
            // Return 404 if the transaction is not found
            return ResponseEntity.notFound().build();
        }// Invalid name field catch Exception
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

    //Delete transaction benefit
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTransactionIncomeById(@PathVariable("id") String id){
        try{
            // States de ID from string
            UUID transactionIncomeID = UUID.fromString(id);
            // Create an Optional Object from the id provided
            Optional<TransactionBenefitModel> transactionBenefitModelOptional = transactionBenefitService.getTransactionBenefitById(transactionIncomeID);

            // If the object does not return values, throw a not found
            if (transactionBenefitModelOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // If the Optional has the values of the searched object, deletes it and return an ok no content
            transactionBenefitService.deleteTransactionBenefitById(transactionBenefitModelOptional.get());
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
