package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.task.TaskCommentDTO;
import com.mladenov.projectmanagement.model.dto.task.TaskDTO;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.model.enums.TaskStatus;
import com.mladenov.projectmanagement.repository.TaskRepository;
import com.mladenov.projectmanagement.util.UserPrincipalUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskCommentService taskCommentService;

    public TaskService(TaskRepository taskRepository, UserService userService, TaskCommentService taskCommentService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.taskCommentService = taskCommentService;
    }

    private TaskDTO mapTaskToDTO(TaskEntity taskEntity) {
        List<TaskCommentDTO> taskComments = taskCommentService.getTaskComments(taskEntity.getId());
        return TaskDTO.builder()
                .id(taskEntity.getId())
                .title(taskEntity.getTitle())
                .description(taskEntity.getDescription())
                .status(taskEntity.getStatus())
                .type(taskEntity.getType())
                .createdAt(taskEntity.getCreatedAt())
                .updatedAt(taskEntity.getUpdatedAt())
                .creatorId(taskEntity.getCreatedBy().getId())
                .assigneeId(taskEntity.getAssignedTo() != null ? taskEntity.getAssignedTo().getId() : null)
                .comments(taskComments)
                .build();
    }

    public TaskDTO getTaskByID(Long taskId) {
        TaskEntity taskEntity = getTaskEntityById(taskId);
        return mapTaskToDTO(taskEntity);
    }

    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream().map(this::mapTaskToDTO).collect(Collectors.toList());
    }

    public TaskDTO createTask(TaskDTO taskDTO) {
        UserEntity creator = userService.getById(taskDTO.getCreatorId());

        TaskEntity task = TaskEntity.builder()
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .status(taskDTO.getStatus() != null ? taskDTO.getStatus() : TaskStatus.OPEN)
                .type(taskDTO.getType())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(creator)
                .assignedTo(null)
                .build();

        if(taskDTO.getAssigneeId() != null) {
            UserEntity assignee = userService.getById(taskDTO.getAssigneeId());
            task.setAssignedTo(assignee);
        }

        TaskEntity created = taskRepository.save(task);
        return mapTaskToDTO(created);
    }

    public TaskDTO updateTask(Long taskId, TaskDTO taskDTO) {
        TaskEntity taskEntity = getTaskEntityById(taskId);
        UserEntity author = userService.getById(taskDTO.getCreatorId());

        if (!Objects.equals(author.getId(), UserPrincipalUtil.getCurrentLoggedUserId())) {
            throw new IllegalArgumentException("You are not the author of this task");
        }

        if(taskDTO.getAssigneeId() != null) {
            UserEntity assignee = userService.getById(taskDTO.getAssigneeId());
            taskEntity.setAssignedTo(assignee);
        }

        taskEntity.setUpdatedAt(LocalDateTime.now());
        taskEntity.setTitle(taskDTO.getTitle());
        taskEntity.setDescription(taskDTO.getDescription());
        taskEntity.setStatus(taskDTO.getStatus());
        taskEntity.setType(taskDTO.getType());

        TaskEntity saved = taskRepository.save(taskEntity);
        return mapTaskToDTO(saved);
    }

    public TaskEntity getTaskEntityById(Long taskId) {
        return taskRepository
                .findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id=" + taskId + " not found"));
    }

    public void deleteTaskById(Long taskId) {
        TaskEntity taskEntity = getTaskEntityById(taskId);
        taskRepository.delete(taskEntity);
    }
}
