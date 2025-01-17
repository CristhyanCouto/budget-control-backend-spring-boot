package com.budget.control.backend.controller;

import com.budget.control.backend.controller.dto.TransactionBenefitDTO;
import com.budget.control.backend.model.TransactionBenefitModel;
import com.budget.control.backend.service.TransactionBenefitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/transaction-benefit")
public class TransactionBenefitController {

    //Dependency Injection
    private final TransactionBenefitService transactionBenefitService;

    //Constructor Injection
    public TransactionBenefitController(TransactionBenefitService transactionBenefitService) {
        this.transactionBenefitService = transactionBenefitService;
    }

    //Saving a benefit transaction in the database
    @PostMapping
    public ResponseEntity<Void> saveBenefitTransaction(@RequestBody TransactionBenefitDTO transactionBenefitDTO) {
        //Map the DTO to the entity
        TransactionBenefitModel transactionBenefitEntity = transactionBenefitDTO.mapToTransactionBenefitModel();
        //Save the benefit transaction
        transactionBenefitService.saveTransactionBenefit(transactionBenefitEntity);

        //Return a response with the status code 201 and the URL location of the new resource in the header
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(transactionBenefitEntity.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }
}
