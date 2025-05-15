package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.EntityTestUtil;
import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.task.TaskCommentDTO;
import com.mladenov.projectmanagement.model.entity.TaskCommentEntity;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.repository.TaskCommentRepository;
import com.mladenov.projectmanagement.service.impl.TaskCommentService;
import com.mladenov.projectmanagement.util.MappingEntityUtil;
import com.mladenov.projectmanagement.util.UserPrincipalUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskCommentServiceTest {

    private TaskCommentRepository commentRepository;
    private IUserService userService;
    private ITaskService taskService;
    private TaskCommentService commentService;

    @BeforeEach
    void setup() {
        commentRepository = mock(TaskCommentRepository.class);
        userService = mock(IUserService.class);
        taskService = mock(ITaskService.class);
        commentService = new TaskCommentService(commentRepository, userService, taskService);
    }

    @Test
    void testGetTaskComments_ReturnsList() {
        TaskEntity task = new TaskEntity();
        task.setId(1L);

        TaskCommentEntity comment1 = new TaskCommentEntity();
        comment1.setId(1L);
        comment1.setTask(task);

        when(taskService.getTaskEntityById(1L)).thenReturn(task);
        when(commentRepository.findAllByTask(task)).thenReturn(List.of(comment1));

        try (MockedStatic<MappingEntityUtil> mapper = mockStatic(MappingEntityUtil.class)) {
            mapper.when(() -> MappingEntityUtil.mapCommentToDTO(any())).thenAnswer(inv -> {
                TaskCommentEntity entity = inv.getArgument(0);
                TaskCommentDTO dto = new TaskCommentDTO();
                dto.setId(entity.getId());
                return dto;
            });

            List<TaskCommentDTO> result = commentService.getTaskComments(1L);
            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).getId());
        }
    }

    @Test
    void testCreateTaskComment_Success() {
        TaskEntity task = new TaskEntity();
        task.setId(1L);
        UserEntity user = new UserEntity();
        user.setId(2L);

        TaskCommentDTO dto = new TaskCommentDTO();
        dto.setAuthorId(2L);
        dto.setContent("Test comment");

        when(taskService.getTaskEntityById(1L)).thenReturn(task);
        when(userService.getUserEntityById(2L)).thenReturn(user);
        when(commentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        try (MockedStatic<MappingEntityUtil> mapper = mockStatic(MappingEntityUtil.class)) {
            mapper.when(() -> MappingEntityUtil.mapCommentToDTO(any())).thenAnswer(inv -> {
                TaskCommentDTO result = new TaskCommentDTO();
                result.setContent("Test comment");
                return result;
            });

            TaskCommentDTO result = commentService.createTaskComment(1L, dto);
            assertEquals("Test comment", result.getContent());
        }
    }

    @Test
    void testUpdateTaskComment_Success() {
        TaskEntity task = new TaskEntity();
        task.setId(1L);
        UserEntity user = new UserEntity();
        user.setId(2L);
        TaskCommentEntity comment = new TaskCommentEntity();
        comment.setId(3L);
        comment.setAuthor(user);
        comment.setTask(task);

        TaskCommentDTO dto = new TaskCommentDTO();
        dto.setAuthorId(2L);
        dto.setContent("Updated");

        when(taskService.getTaskEntityById(1L)).thenReturn(task);
        when(userService.getUserEntityById(2L)).thenReturn(user);
        when(commentRepository.findById(3L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any())).thenReturn(comment);

        try (MockedStatic<MappingEntityUtil> mapper = mockStatic(MappingEntityUtil.class)) {
            mapper.when(() -> MappingEntityUtil.mapCommentToDTO(any())).thenAnswer(inv -> {
                TaskCommentDTO result = new TaskCommentDTO();
                result.setContent("Updated");
                return result;
            });

            TaskCommentDTO result = commentService.updateTaskComment(1L, 3L, dto);
            assertEquals("Updated", result.getContent());
        }
    }

    @Test
    void testUpdateTaskComment_InvalidAuthor_Throws() {
        TaskEntity task = new TaskEntity();
        task.setId(1L);
        UserEntity correctAuthor = EntityTestUtil.createUserEntity(1L);
        UserEntity wrongUser = EntityTestUtil.createUserEntity(2L);
        TaskCommentEntity comment = new TaskCommentEntity(task, "original", correctAuthor, null, null);
        comment.setId(10L);

        TaskCommentDTO dto = new TaskCommentDTO();
        dto.setAuthorId(2L);
        dto.setContent("Updated");

        when(taskService.getTaskEntityById(1L)).thenReturn(task);
        when(userService.getUserEntityById(2L)).thenReturn(wrongUser);
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateTaskComment(1L, 10L, dto));
    }

    @Test
    void testDeleteTaskComment_Success() {
        TaskEntity task = new TaskEntity();
        task.setId(1L);
        UserEntity user = new UserEntity();
        user.setId(7L);

        TaskCommentEntity comment = new TaskCommentEntity(task, "text", user, null, null);
        comment.setId(11L);

        when(taskService.getTaskEntityById(1L)).thenReturn(task);
        when(commentRepository.findById(11L)).thenReturn(Optional.of(comment));

        try (MockedStatic<UserPrincipalUtil> staticUtil = mockStatic(UserPrincipalUtil.class)) {
            staticUtil.when(UserPrincipalUtil::getCurrentLoggedUserId).thenReturn(7L);

            commentService.deleteTaskComment(1L, 11L);

            verify(commentRepository).delete(comment);
        }
    }

    @Test
    void testDeleteTaskComment_WrongTask_Throws() {
        TaskEntity task = new TaskEntity();
        task.setId(99L);
        TaskEntity wrongTask = new TaskEntity();
        wrongTask.setId(98L);

        UserEntity author = new UserEntity();
        author.setId(3L);
        TaskCommentEntity comment = new TaskCommentEntity(wrongTask, "content", author, null, null);
        comment.setId(5L);

        when(taskService.getTaskEntityById(99L)).thenReturn(task);
        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.deleteTaskComment(99L, 5L));
    }

    @Test
    void testDeleteTaskComment_WrongUser_Throws() {
        TaskEntity task = new TaskEntity();
        task.setId(1L);
        UserEntity user = new UserEntity();
        user.setId(1L);
        UserEntity otherUser = new UserEntity();
        otherUser.setId(2L);
        TaskCommentEntity comment = new TaskCommentEntity(task, "text", user, null, null);
        comment.setId(10L);

        when(taskService.getTaskEntityById(1L)).thenReturn(task);
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));

        try (MockedStatic<UserPrincipalUtil> util = mockStatic(UserPrincipalUtil.class)) {
            util.when(UserPrincipalUtil::getCurrentLoggedUserId).thenReturn(2L);

            assertThrows(IllegalArgumentException.class,
                    () -> commentService.deleteTaskComment(1L, 10L));
        }
    }

    @Test
    void testGetTaskCommentEntityById_NotFound() {
        when(commentRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> commentService.getTaskCommentEntityById(123L));
    }
}
