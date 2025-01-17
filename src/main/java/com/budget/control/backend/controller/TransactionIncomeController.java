package com.budget.control.backend.controller;

import com.budget.control.backend.controller.dto.TransactionIncomeDTO;
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
    public ResponseEntity<Void> saveIncomeTransaction(@RequestBody TransactionIncomeDTO transactionIncomeDTO) {
        //Map the DTO to the entity
        TransactionIncomeModel transactionIncomeEntity = transactionIncomeDTO.mapToTransactionIncomeModel();
        //Save the income transaction
        transactionIncomeService.saveTransactionIncome(transactionIncomeEntity);

        //Return a response with the status code 201 and the URL location of the new resource in the header
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(transactionIncomeEntity.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public String getIncomeTransaction() {
        return "GET Income Transaction";
    }
}
