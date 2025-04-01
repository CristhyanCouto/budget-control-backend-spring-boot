package com.budget.control.backend.mappers;

import com.budget.control.backend.controller.dto.request.UserRequestDTO;
import com.budget.control.backend.controller.dto.response.UserResponseDTO;
import com.budget.control.backend.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "encryptedPassword", target = "encryptedPassword")
    UserModel toRequestEntity(UserRequestDTO userRequestDTO);

    UserResponseDTO toResponseDTO(UserModel userModel);
}
