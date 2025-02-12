package com.budget.control.backend.mappers;

import com.budget.control.backend.controller.dto.request.TransactionBenefitRequestDTO;
import com.budget.control.backend.controller.dto.response.TransactionBenefitResponseDTO;
import com.budget.control.backend.model.TransactionBenefitModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionBenefitMapper {

    TransactionBenefitModel toRequestEntity(TransactionBenefitRequestDTO transactionBenefitRequestDTO);

    TransactionBenefitResponseDTO toResponseDTO(TransactionBenefitModel transactionBenefitModel);
}
