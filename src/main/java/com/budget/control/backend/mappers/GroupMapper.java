package com.budget.control.backend.mappers;

import com.budget.control.backend.controller.dto.request.GroupRequestDTO;
import com.budget.control.backend.controller.dto.response.GroupResponseDTO;
import com.budget.control.backend.model.GroupModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    GroupModel toRequestEntity(GroupRequestDTO groupRequestDTO);

    GroupResponseDTO toResponseDTO(GroupModel groupModel);
}
