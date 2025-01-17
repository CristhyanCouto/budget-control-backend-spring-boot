package com.budget.control.backend.controller;

import com.budget.control.backend.controller.dto.TransactionExpenseDTO;
import com.budget.control.backend.model.TransactionExpenseModel;
import com.budget.control.backend.service.TransactionExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/transaction-expense")
public class TransactionExpenseController {

    //Dependency Injection
    private final TransactionExpenseService transactionExpenseService;

    //Constructor Injection
    public TransactionExpenseController(TransactionExpenseService transactionExpenseService) {
        this.transactionExpenseService = transactionExpenseService;
    }

    //Saving an expense transaction in the database
    @PostMapping
    public ResponseEntity<Void> saveExpenseTransaction(@RequestBody TransactionExpenseDTO transactionExpenseDTO) {
        //Map the DTO to the entity
        TransactionExpenseModel transactionExpenseEntity = transactionExpenseDTO.mapToTransactionExpenseModel();
        //Save the expense transaction
        transactionExpenseService.saveTransactionExpense(transactionExpenseEntity);

        //Return a response with the status code 201 and the URL location of the new resource in the header
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(transactionExpenseEntity.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }
}
