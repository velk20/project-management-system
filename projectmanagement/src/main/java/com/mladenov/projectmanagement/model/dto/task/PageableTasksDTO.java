package com.mladenov.projectmanagement.model.dto.task;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageableTasksDTO {
    List<TaskDTO> tasks;
    int totalPages;
    long totalElements;
}
