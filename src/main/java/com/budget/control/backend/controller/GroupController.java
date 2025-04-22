package com.budget.control.backend.controller;

import com.budget.control.backend.controller.dto.error.ErrorResponse;
import com.budget.control.backend.controller.dto.request.GroupRequestDTO;
import com.budget.control.backend.controller.dto.request.UserRequestDTO;
import com.budget.control.backend.controller.dto.response.GroupResponseDTO;
import com.budget.control.backend.controller.dto.response.UserResponseDTO;
import com.budget.control.backend.exception.*;
import com.budget.control.backend.mappers.GroupMapper;
import com.budget.control.backend.model.GroupModel;
import com.budget.control.backend.model.UserModel;
import com.budget.control.backend.service.GroupService;
import com.budget.control.backend.type.UserRoleType;
import com.budget.control.backend.validator.UUIDValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final UUIDValidator uuidValidator;
    private final GroupMapper groupMapper;

    @PostMapping
    public ResponseEntity<Object> saveGroup(@RequestBody @Valid GroupRequestDTO groupRequestDTO) {
        try {
            GroupModel groupModel = groupMapper.toRequestEntity(groupRequestDTO);
            groupService.saveGroup(groupModel);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(groupModel.getId())
                    .toUri();
            return ResponseEntity.created(location).build();
        } catch (DuplicatedRegisterException e) {
            var errorDTO = ErrorResponse.conflictResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        } catch (NullFieldException e) {
            var errorDTO = ErrorResponse.nullFieldResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable("id") String id) {
        try {
            uuidValidator.validateUUID(id);
            UUID groupID = UUID.fromString(id);
            return groupService.getGroupById(groupID)
                    .map(group -> {
                        GroupResponseDTO groupResponseDTO = groupMapper.toResponseDTO(group);
                        return ResponseEntity.ok(groupResponseDTO);
                    }).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (InvalidUUIDException e) {
            var errorDTO = ErrorResponse.invalidFieldResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getGroupByParams(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "user_id", required = false) UUID userId,
            @RequestParam(value = "reference_id", required = false) UUID referenceId
    ) {
        try {
            List<GroupModel> result = groupService
                    .getGroupByNameOrUserIdOrReferenceId(
                            name, userId, referenceId);
            List<GroupResponseDTO> response = result.stream()
                    .map(groupMapper::toResponseDTO)
                    .toList();
            if (response.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(response);
        } catch (InvalidFieldException e) {
            var errorDTO = ErrorResponse.invalidFieldResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateGroupById(
            @PathVariable("id") String id,
            @RequestBody GroupRequestDTO groupRequestDTO
    ) {
        try {
            UUID groupID = UUID.fromString(id);
            Optional<GroupModel> groupModelOptional = groupService.getGroupById(groupID);
            if (groupModelOptional.isPresent()) {
                GroupModel existingGroup = groupModelOptional.get();
                if (groupRequestDTO.name() != null) {
                    existingGroup.setName(groupRequestDTO.name());
                }
                if (groupRequestDTO.description() != null) {
                    existingGroup.setDescription(groupRequestDTO.description());
                }
                groupService.updateGroupById(existingGroup);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (InvalidFieldException e) {
            var errorDTO = ErrorResponse.invalidFieldResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid UUID: " + id);
        } catch (DuplicatedRegisterException e) {
            var errorDTO = ErrorResponse.conflictResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        } catch (Exception e) {
            var errorDTO = ErrorResponse.unexpectedErrorResponse("An unexpected error occurred.");
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteGroupById(@PathVariable("id") String id) {
        try {
            UUID groupID = UUID.fromString(id);
            Optional<GroupModel> groupModelOptional = groupService.getGroupById(groupID);
            if (groupModelOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            groupService.deleteGroupById(groupModelOptional.get());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            var errorDTO = ErrorResponse.invalidUUIDResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        } catch (NonAuthorizedException e) {
            var errorDTO = ErrorResponse.standardResponse(e.getMessage());
            return ResponseEntity.status(errorDTO.status()).body(errorDTO);
        }
    }
}
