package com.budget.control.backend.mappers;

import com.budget.control.backend.controller.dto.request.TransactionIncomeRequestDTO;
import com.budget.control.backend.controller.dto.response.TransactionIncomeResponseDTO;
import com.budget.control.backend.model.TransactionIncomeModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionIncomeMapper {

    TransactionIncomeModel toRequestEntity(TransactionIncomeRequestDTO transactionIncomeRequestDTO);

    TransactionIncomeResponseDTO toResponseDTO(TransactionIncomeModel transactionIncomeModel);
}
