package com.mladenov.projectmanagement.model.dto.task;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TaskCommentDTO
{
    @NotNull(message = "Task Id is required")
    private Long taskId;
    @NotNull(message = "Author Id is required")
    private Long authorId;
    @NotEmpty(message = "Comment content is required")
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
