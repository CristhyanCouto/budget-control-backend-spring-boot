package com.budget.control.backend.repository;

import com.budget.control.backend.model.GroupModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<GroupModel, UUID>, JpaSpecificationExecutor<GroupModel> {

    Optional<GroupModel> findByNameAndUserIdAndReferenceId(String name, UUID userId, UUID referenceId);
    Optional<GroupModel> findByIdAndUserId(UUID id, UUID userId);
    Optional<GroupModel> findByIdAndReferenceId(UUID id, UUID referenceId);
}
