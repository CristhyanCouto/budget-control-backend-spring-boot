package com.budget.control.backend.mappers;

import com.budget.control.backend.controller.dto.request.UserRequestDTO;
import com.budget.control.backend.controller.dto.response.UserResponseDTO;
import com.budget.control.backend.model.UserModel;
import com.budget.control.backend.repository.GroupRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    GroupRepository groupRepository;

    @Mapping(source = "encryptedPassword", target = "encryptedPassword")
    @Mapping(target = "group", expression = "java(groupRepository.findById(userRequestDTO.groupId()).orElse(null))")
    public abstract  UserModel toRequestEntity(UserRequestDTO userRequestDTO);

    public abstract UserResponseDTO toResponseDTO(UserModel userModel);
}
