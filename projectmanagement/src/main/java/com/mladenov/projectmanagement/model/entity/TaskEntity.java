package com.mladenov.projectmanagement.model.entity;

import com.mladenov.projectmanagement.model.enums.TaskStatus;
import com.mladenov.projectmanagement.model.enums.TaskType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TaskEntity extends BaseEntity {
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    private TaskType type;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity createdBy;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private UserEntity assignedTo;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
