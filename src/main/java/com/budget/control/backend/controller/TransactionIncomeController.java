package com.budget.control.backend.controller;

import com.budget.control.backend.controller.dto.error.ErrorResponse;
import com.budget.control.backend.controller.dto.request.TransactionIncomeRequestDTO;
import com.budget.control.backend.controller.dto.response.TransactionIncomeResponseDTO;
import com.budget.control.backend.exception.DuplicatedRegisterException;
import com.budget.control.backend.exception.InvalidUUIDException;
import com.budget.control.backend.exception.NullFieldException;
import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.service.TransactionIncomeService;
import com.budget.control.backend.validator.UUIDValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/transaction-income")
public class TransactionIncomeController {

    //Dependency Injection
    private final TransactionIncomeService transactionIncomeService;
    private UUIDValidator uuidValidator = new UUIDValidator();

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
            uuidValidator.validateUUID(id);

            var transactionIncomeID = UUID.fromString(id);
            Optional<TransactionIncomeModel> transactionIncomeModelOptional = transactionIncomeService.getTransactionIncomeById(transactionIncomeID);
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
            return ResponseEntity.notFound().build();
        }catch (InvalidUUIDException e) {
            var errorDTO = ErrorResponse.invalidUUIDResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }
}
