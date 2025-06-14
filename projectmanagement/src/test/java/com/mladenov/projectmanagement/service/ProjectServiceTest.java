package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.EntityTestUtil;
import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.project.ProjectDTO;
import com.mladenov.projectmanagement.model.dto.project.UpdateProjectDTO;
import com.mladenov.projectmanagement.model.entity.ProjectEntity;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.repository.ProjectRepository;
import com.mladenov.projectmanagement.service.impl.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    private ProjectRepository projectRepository;
    private IUserService userService;
    private ITaskService taskService;
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        userService = mock(IUserService.class);
        taskService = mock(ITaskService.class);
        projectService = new ProjectService(projectRepository, userService, taskService);
    }

    @Test
    void testGetProjectByID_WhenExists_ReturnsDTO() {
        ProjectEntity project = EntityTestUtil.createProjectEntity(1L);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        ProjectDTO result = projectService.getProjectByID(1L);

        assertNotNull(result);
        assertEquals("Test 1", result.getName());
    }

    @Test
    void testGetProjectByID_WhenNotExists_ThrowsException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectByID(1L));
    }

    @Test
    void testCreateProject_Success() {
        ProjectDTO dto = new ProjectDTO();
        dto.setName("New Project");
        dto.setDescription("Description");
        dto.setOwnerId(1L);

        UserEntity owner = EntityTestUtil.createUserEntity(1L);

        when(userService.getUserEntityById(1L)).thenReturn(owner);
        when(projectRepository.findByName("New Project")).thenReturn(Optional.empty());
        when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProjectDTO result = projectService.createProject(dto);

        assertEquals("New Project", result.getName());
        assertEquals("Description", result.getDescription());
        assertEquals(1L, result.getOwnerId());
    }

    @Test
    void testUpdateProject_ChangesDescriptionAndName() {
        Long projectId = 1L;
        ProjectEntity existing = EntityTestUtil.createProjectEntity(projectId);
        existing.setTasks(new ArrayList<>());
        existing.setTeamMembers(new ArrayList<>());

        UpdateProjectDTO updateDTO = new UpdateProjectDTO();
        updateDTO.setName("New Name");
        updateDTO.setDescription("New Desc");
        updateDTO.setTasksId(Collections.emptyList());
        updateDTO.setTeamMembersId(Collections.emptyList());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existing));
        when(projectRepository.findByName("New Name")).thenReturn(Optional.empty());
        when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProjectDTO result = projectService.updateProject(projectId, updateDTO);

        assertEquals("New Name", result.getName());
        assertEquals("New Desc", result.getDescription());
    }

    @Test
    void testDeleteProjectById_Success() {
        ProjectEntity project = new ProjectEntity();
        project.setId(2L);
        when(projectRepository.findById(2L)).thenReturn(Optional.of(project));

        projectService.deleteProjectById(2L);

        verify(projectRepository).delete(project);
    }

    @Test
    void testUpdateProject_TasksAddedOnlyIfNew() {
        Long projectId = 1L;
        ProjectEntity projectEntity = EntityTestUtil.createProjectEntity(projectId);
        UserEntity userEntity = EntityTestUtil.createUserEntity(projectId);
        TaskEntity task1 = EntityTestUtil.createTaskEntity(1L, userEntity, projectEntity);
        TaskEntity task2 =  EntityTestUtil.createTaskEntity(2L, userEntity, projectEntity);


        projectEntity.setId(projectId);
        projectEntity.setName("Project");
        projectEntity.setTasks(new ArrayList<>(List.of(task1)));
        projectEntity.setTeamMembers(new ArrayList<>());

        UpdateProjectDTO dto = new UpdateProjectDTO();
        dto.setName("Project");
        dto.setDescription("Updated");
        dto.setTasksId(List.of(1L, 2L));
        dto.setTeamMembersId(Collections.emptyList());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectEntity));
        when(taskService.getTaskEntityById(2L)).thenReturn(task2);
        when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProjectDTO result = projectService.updateProject(projectId, dto);

        assertEquals(2, projectEntity.getTasks().size());
    }
}
