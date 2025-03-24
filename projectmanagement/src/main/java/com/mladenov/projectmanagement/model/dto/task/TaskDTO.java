package com.mladenov.projectmanagement.model.dto.task;

import com.mladenov.projectmanagement.model.enums.TaskStatus;
import com.mladenov.projectmanagement.model.enums.TaskType;
import com.mladenov.projectmanagement.util.validation.ValidEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    @NotNull(message = "status is required")
    @ValidEnum(key="status", enumClass = TaskStatus.class, message = "Invalid status. Allowed values: Open, In_Progress, Resolved, Closed")
    private String status;
    @NotNull(message = "type is required")
    @ValidEnum(key="type", enumClass = TaskType.class, message = "Invalid type. Allowed values: Feature, Story, Bug, Epic, Test, Idea")
    private String type;
    @NotNull(message = "creatorId is required.")
    private Long creatorId;
    private Long assigneeId;
    @NotNull(message = "projectId is required.")
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TaskCommentDTO> comments;
}
