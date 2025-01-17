package com.budget.control.backend.repository;

import com.budget.control.backend.model.TransactionIncomeModel;
import com.budget.control.backend.type.TransactionIncomeType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest
public class TransactionIncomeRepositoryTest {

    @Autowired
    private TransactionIncomeRepository transactionIncomeRepository;

    @Test
    void saveTest(){
        TransactionIncomeModel transactionIncomeModel = new TransactionIncomeModel();
        transactionIncomeModel.setAmount(BigDecimal.valueOf(100));
        transactionIncomeModel.setName(TransactionIncomeType.BONUS);
        transactionIncomeModel.setDescription("lalalala");
        transactionIncomeModel.setDate(LocalDate.of(2021, 1, 1));

        transactionIncomeRepository.save(transactionIncomeModel);
    }
}
