package com.mladenov.projectmanagement.model.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdateProjectDTO {
    @NotBlank(message = "name is required.")
    private String name;
    private String description;
    private List<Long> tasksId = new ArrayList<>();
    private List<Long> teamMembersId = new ArrayList<>();
}
