package com.mladenov.projectmanagement.model.dto.task;

import com.mladenov.projectmanagement.model.enums.TaskStatus;
import com.mladenov.projectmanagement.model.enums.TaskType;
import com.mladenov.projectmanagement.util.validation.ValidateTaskType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDTO {
    private Long id;
    @NotEmpty(message = "Title is required.")
    private String title;
    private String description;
    private TaskStatus status;
    @ValidateTaskType(anyOf = {TaskType.Bug, TaskType.Idea, TaskType.Epic, TaskType.Feature, TaskType.Test, TaskType.Story})
    private TaskType type;
    @NotNull(message = "creatorId is required.")
    private Long creatorId;

    private Long assigneeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
