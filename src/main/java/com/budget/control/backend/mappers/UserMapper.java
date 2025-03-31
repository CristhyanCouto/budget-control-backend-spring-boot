package com.budget.control.backend.mappers;

import com.budget.control.backend.controller.dto.request.UserRequestDTO;
import com.budget.control.backend.controller.dto.response.UserResponseDTO;
import com.budget.control.backend.model.UserModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserModel toRequestEntity(UserRequestDTO userRequestDTO);

    UserResponseDTO toResponseDTO(UserModel userModel);
}
