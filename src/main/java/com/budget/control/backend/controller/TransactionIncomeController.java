package com.budget.control.backend.controller;

import com.budget.control.backend.controller.dto.error.ErrorResponse;
import com.budget.control.backend.controller.dto.request.TransactionIncomeRequestDTO;
import com.budget.control.backend.exception.DuplicatedRegisterException;
import com.budget.control.backend.exception.NullFieldException;
import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.service.TransactionIncomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/transaction-income")
public class TransactionIncomeController {

    //Dependency Injection
    private final TransactionIncomeService transactionIncomeService;

    public TransactionIncomeController(TransactionIncomeService transactionIncomeService) {
        this.transactionIncomeService = transactionIncomeService;
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

    @GetMapping
    public String getIncomeTransaction() {
        return "GET Income Transaction";
    }
}
