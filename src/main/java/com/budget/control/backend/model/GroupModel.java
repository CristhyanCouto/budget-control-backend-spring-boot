package com.budget.control.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Table(name = "groups")
@Data
@ToString
@EntityListeners(AuditingEntityListener.class)
public class GroupModel {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "reference_id")
    private UUID referenceId;

    @PrePersist
    public void generateReferenceId() {
        if (this.referenceId == null) {
            this.referenceId = UUID.randomUUID();
        }
    }

}
