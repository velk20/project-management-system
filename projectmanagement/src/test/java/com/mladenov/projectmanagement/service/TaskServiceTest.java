package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.EntityTestUtil;
import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.task.TaskDTO;
import com.mladenov.projectmanagement.model.entity.ProjectEntity;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.repository.TaskRepository;
import com.mladenov.projectmanagement.service.impl.TaskService;
import com.mladenov.projectmanagement.util.MappingEntityUtil;
import com.mladenov.projectmanagement.util.UserPrincipalUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    private TaskRepository taskRepository;
    private IUserService userService;
    private IProjectService projectService;
    private TaskService taskService;
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        userService = mock(IUserService.class);
        projectService = mock(IProjectService.class);
        kafkaTemplate = mock(KafkaTemplate.class);
        taskService = new TaskService(taskRepository, userService, projectService, kafkaTemplate);
    }

    @Test
    void testGetTaskByID_Success() {
        TaskEntity task = new TaskEntity();
        task.setId(1L);
        task.setTitle("Test Task");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        try (MockedStatic<MappingEntityUtil> mocked = mockStatic(MappingEntityUtil.class)) {
            TaskDTO mockDto = new TaskDTO();
            mockDto.setId(1L);
            mockDto.setTitle("Test Task");

            mocked.when(() -> MappingEntityUtil.mapTaskToDTO(task)).thenReturn(mockDto);

            TaskDTO result = taskService.getTaskByID(1L);

            assertEquals(1L, result.getId());
            assertEquals("Test Task", result.getTitle());
        }
    }

    @Test
    void testGetTaskByID_NotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> taskService.getTaskByID(99L));
    }

    @Test
    void testCreateTask_WithAssignee() {
        TaskDTO dto = new TaskDTO();
        dto.setTitle("Create task");
        dto.setDescription("Some description");
        dto.setStatus("New");
        dto.setType("Bug");
        dto.setCreatorId(1L);
        dto.setAssigneeId(2L);
        dto.setProjectId(100L);

        UserEntity creator = EntityTestUtil.createUserEntity(1L);
        UserEntity assignee = EntityTestUtil.createUserEntity(2L);
        ProjectEntity project = EntityTestUtil.createProjectEntity(100L);

        when(userService.getUserEntityById(1L)).thenReturn(creator);
        when(userService.getUserEntityById(2L)).thenReturn(assignee);
        when(projectService.getProjectEntity(100L)).thenReturn(project);
        when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        try (MockedStatic<MappingEntityUtil> mocked = mockStatic(MappingEntityUtil.class)) {
            mocked.when(() -> MappingEntityUtil.mapTaskToDTO(any())).thenReturn(dto);

            TaskDTO result = taskService.createTask(dto);

            assertEquals("Create task", result.getTitle());
            verify(taskRepository).save(any(TaskEntity.class));
        }
    }

    @Test
    void testUpdateTask_AuthorizedAndSuccess() {
        Long taskId = 42L;
        ProjectEntity projectEntity = EntityTestUtil.createProjectEntity(taskId);
        UserEntity userEntity = EntityTestUtil.createUserEntity(taskId);
        TaskEntity existingTask = EntityTestUtil.createTaskEntity(taskId, userEntity, projectEntity);

        TaskDTO dto = new TaskDTO();
        dto.setTitle("Updated");
        dto.setDescription("Updated desc");
        dto.setStatus("New");
        dto.setType("Bug");
        dto.setCreatorId(42L);
        dto.setProjectId(42L);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(userService.getUserEntityById(42L)).thenReturn(existingTask.getCreatedBy());
        when(projectService.getProjectEntity(42L)).thenReturn(existingTask.getProject());
        when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        try (MockedStatic<UserPrincipalUtil> userStatic = mockStatic(UserPrincipalUtil.class);
             MockedStatic<MappingEntityUtil> mapStatic = mockStatic(MappingEntityUtil.class)) {

            userStatic.when(UserPrincipalUtil::getCurrentLoggedUserId).thenReturn(42L);
            mapStatic.when(() -> MappingEntityUtil.mapTaskToDTO(any())).thenReturn(dto);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(taskId, dto));

            assertEquals("You are not member of the project to change this task", exception.getMessage());
        }
    }

    @Test
    void testUpdateTask_Unauthorized_Throws() {
        UserEntity creator = EntityTestUtil.createUserEntity(1L);
        ProjectEntity projectEntity = EntityTestUtil.createProjectEntity(1L);
        TaskEntity task = EntityTestUtil.createTaskEntity(1L, creator, projectEntity);

        TaskDTO dto = new TaskDTO();
        dto.setCreatorId(1L);
        dto.setProjectId(1L);

        when(projectService.getProjectEntity(1L)).thenReturn(task.getProject());
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserEntityById(1L)).thenReturn(creator);

        try (MockedStatic<UserPrincipalUtil> userStatic = mockStatic(UserPrincipalUtil.class)) {
            userStatic.when(UserPrincipalUtil::getCurrentLoggedUserId).thenReturn(2L);

            assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(1L, dto));
        }
    }

    @Test
    void testDeleteTaskById() {
        TaskEntity task = new TaskEntity();
        task.setId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTaskById(1L);

        verify(taskRepository).delete(task);
    }
}
