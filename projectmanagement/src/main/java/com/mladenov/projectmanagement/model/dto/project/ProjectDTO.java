package com.mladenov.projectmanagement.model.dto.project;

import com.mladenov.projectmanagement.model.dto.task.TaskDTO;
import com.mladenov.projectmanagement.model.dto.user.UserDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProjectDTO {
    private Long id;
    @NotBlank(message = "name is required.")
    private String name;
    private String description;
    @NotNull(message = "ownerId is required.")
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TaskDTO> tasks;
    private List<UserDTO> teamMembers;
}
