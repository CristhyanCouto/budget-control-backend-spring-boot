package com.budget.control.backend.mappers;

import com.budget.control.backend.controller.dto.request.TransactionExpenseRequestDTO;
import com.budget.control.backend.controller.dto.response.TransactionExpenseResponseDTO;
import com.budget.control.backend.model.TransactionExpenseModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionExpenseMapper {

    TransactionExpenseModel toRequestEntity(TransactionExpenseRequestDTO transactionExpenseRequestDTO);

    TransactionExpenseResponseDTO toResponseDTO(TransactionExpenseModel transactionExpenseModel);
}
