package com.mladenov.projectmanagement.service.impl;

import com.mladenov.projectmanagement.model.dto.search.SearchResultDto;
import com.mladenov.projectmanagement.model.entity.ProjectEntity;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import com.mladenov.projectmanagement.repository.ProjectRepository;
import com.mladenov.projectmanagement.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public SearchService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public List<SearchResultDto> search(String query) {
        List<SearchResultDto> results = new ArrayList<>();

        List<TaskEntity> tasks = taskRepository.findByTitleContainingIgnoreCaseOrId(query, parseId(query));
        for (TaskEntity task : tasks) {
            results.add(new SearchResultDto(task.getId(), task.getTitle(), "task"));
        }

        List<ProjectEntity> projects = projectRepository.findByNameContainingIgnoreCaseOrId(query, parseId(query));
        for (ProjectEntity project : projects) {
            results.add(new SearchResultDto(project.getId(), project.getName(), "project"));
        }

        return results;
    }

    private Long parseId(String input) {
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            return -1L;
        }
    }
}
