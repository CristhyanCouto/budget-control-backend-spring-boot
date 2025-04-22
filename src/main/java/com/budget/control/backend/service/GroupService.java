package com.budget.control.backend.service;

import com.budget.control.backend.model.GroupModel;
import com.budget.control.backend.repository.GroupRepository;
import com.budget.control.backend.validator.UUIDValidator;
import com.budget.control.backend.validator.request.GroupValidatorRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupValidatorRequest groupValidatorRequest;
    private final UUIDValidator uuidValidator;

    public GroupService(GroupRepository groupRepository, GroupValidatorRequest groupValidatorRequest, UUIDValidator uuidValidator) {
        this.groupRepository = groupRepository;
        this.groupValidatorRequest = groupValidatorRequest;
        this.uuidValidator = uuidValidator;
    }

    public void saveGroup(GroupModel groupModel) {
        groupValidatorRequest.validate(groupModel);
        groupRepository.save(groupModel);
    }

    public Optional<GroupModel> getGroupById(UUID groupId) {
        uuidValidator.validateUUID(groupId.toString());
        return groupRepository.findById(groupId);
    }

    public List<GroupModel> getGroupByNameOrUserIdOrReferenceId(
            String name, UUID userId, UUID referenceId
    ) {
        return groupRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) {
                predicates.add(criteriaBuilder.equal(root.get("name"), name));
            }
            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
            }
            if (referenceId != null) {
                predicates.add(criteriaBuilder.equal(root.get("referenceId"), referenceId));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public void updateGroupById(GroupModel groupModel){
        if(groupModel.getId() == null){
            throw new IllegalArgumentException("Group ID cannot be null");
        }
        groupValidatorRequest.validate(groupModel);
        groupRepository.save(groupModel);
    }

    public void deleteGroupById(GroupModel groupModel) {
        if(groupModel.getId() != null){
            uuidValidator.validateUUID(groupModel.getId().toString());
        }
        groupRepository.delete(groupModel);
    }

}
