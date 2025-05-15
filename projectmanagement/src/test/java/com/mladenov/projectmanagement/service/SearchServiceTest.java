package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.model.dto.search.SearchResultDto;
import com.mladenov.projectmanagement.model.entity.ProjectEntity;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import com.mladenov.projectmanagement.repository.ProjectRepository;
import com.mladenov.projectmanagement.repository.TaskRepository;
import com.mladenov.projectmanagement.service.impl.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchServiceTest {

    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private SearchService searchService;

    @BeforeEach
    void setup() {
        taskRepository = mock(TaskRepository.class);
        projectRepository = mock(ProjectRepository.class);
        searchService = new SearchService(taskRepository, projectRepository);
    }

    @Test
    void testSearch_WithTextQuery_ReturnsTasksAndProjects() {
        String query = "test";

        TaskEntity task = new TaskEntity();
        task.setId(1L);
        task.setTitle("Test Task");

        ProjectEntity project = new ProjectEntity();
        project.setId(2L);
        project.setName("Test Project");

        when(taskRepository.findByTitleContainingIgnoreCaseOrId(query, -1L)).thenReturn(List.of(task));
        when(projectRepository.findByNameContainingIgnoreCaseOrId(query, -1L)).thenReturn(List.of(project));

        List<SearchResultDto> results = searchService.search(query);

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(r -> r.type().equals("task")));
        assertTrue(results.stream().anyMatch(r -> r.type().equals("project")));
    }

    @Test
    void testSearch_WithNumericIdQuery_ReturnsMatchingEntities() {
        String query = "5";

        TaskEntity task = new TaskEntity();
        task.setId(5L);
        task.setTitle("Some");

        ProjectEntity project = new ProjectEntity();
        project.setId(5L);
        project.setName("Other");

        when(taskRepository.findByTitleContainingIgnoreCaseOrId(query, 5L)).thenReturn(List.of(task));
        when(projectRepository.findByNameContainingIgnoreCaseOrId(query, 5L)).thenReturn(List.of(project));

        List<SearchResultDto> results = searchService.search(query);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r -> r.id() == 5L));
    }

    @Test
    void testSearch_WithInvalidId_ReturnsOnlyByTitle() {
        String query = "abc";

        TaskEntity task = new TaskEntity();
        task.setId(10L);
        task.setTitle("abc Task");

        when(taskRepository.findByTitleContainingIgnoreCaseOrId(query, -1L)).thenReturn(List.of(task));
        when(projectRepository.findByNameContainingIgnoreCaseOrId(query, -1L)).thenReturn(List.of());

        List<SearchResultDto> results = searchService.search(query);

        assertEquals(1, results.size());
        assertEquals("task", results.get(0).type());
        assertEquals("abc Task", results.get(0).title());
    }

    @Test
    void testSearch_EmptyResults_ReturnsEmptyList() {
        String query = "nothing";

        when(taskRepository.findByTitleContainingIgnoreCaseOrId(query, -1L)).thenReturn(List.of());
        when(projectRepository.findByNameContainingIgnoreCaseOrId(query, -1L)).thenReturn(List.of());

        List<SearchResultDto> results = searchService.search(query);

        assertTrue(results.isEmpty());
    }
}
