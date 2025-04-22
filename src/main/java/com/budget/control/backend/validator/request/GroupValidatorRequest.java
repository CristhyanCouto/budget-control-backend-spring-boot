package com.budget.control.backend.validator.request;

import com.budget.control.backend.exception.DuplicatedRegisterException;
import com.budget.control.backend.exception.NullFieldException;
import com.budget.control.backend.model.GroupModel;
import com.budget.control.backend.repository.GroupRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GroupValidatorRequest {

    private final GroupRepository groupRepository;

    public GroupValidatorRequest(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public void validate(GroupModel groupModel) {
        if (isGroupDuplicated(groupModel)) {
            throw new DuplicatedRegisterException("Group already exists");
        }
        isGroupNull(groupModel);
    }

    private boolean isGroupDuplicated(GroupModel groupModel){
        Optional<GroupModel> groupModelOptional = groupRepository.findByNameAndUserIdAndReferenceId(
                groupModel.getName(),
                groupModel.getUserId(),
                groupModel.getReferenceId()
        );
        if (groupModel.getId() == null) {
            return groupModelOptional.isPresent();
        }
        return groupModelOptional.isPresent() && !groupModel.getId().equals(groupModelOptional.get().getId());
    }

    private void isGroupNull(GroupModel groupModel) {
        if (groupModel.getName() == null) {
            throw new NullFieldException("Group name cannot be null");
        }
        if (groupModel.getUserId() == null) {
            throw new NullFieldException("Group user ID cannot be null");
        }
    }
}
