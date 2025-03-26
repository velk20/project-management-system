package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.task.TaskDTO;
import com.mladenov.projectmanagement.model.entity.ProjectEntity;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.model.enums.TaskStatus;
import com.mladenov.projectmanagement.model.enums.TaskType;
import com.mladenov.projectmanagement.repository.TaskRepository;
import com.mladenov.projectmanagement.util.MappingEntityUtil;
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
    private final ProjectService projectService;

    public TaskService(TaskRepository taskRepository, UserService userService, ProjectService projectService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.projectService = projectService;
    }

    public TaskDTO getTaskByID(Long taskId) {
        TaskEntity taskEntity = getTaskEntityById(taskId);

        return MappingEntityUtil.mapTaskToDTO(taskEntity);
    }

    public List<TaskDTO> getAllTasks() {
        return taskRepository
                .findAll()
                .stream()
                .map(MappingEntityUtil::mapTaskToDTO)
                .collect(Collectors.toList());
    }

    public TaskDTO createTask(TaskDTO taskDTO) {
        UserEntity creator = userService.getUserEntityById(taskDTO.getCreatorId());
        ProjectEntity projectEntity = projectService.getProjectEntity(taskDTO.getProjectId());

        TaskEntity task = TaskEntity.builder()
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .status(TaskStatus.valueOf(taskDTO.getStatus()))
                .type(TaskType.valueOf(taskDTO.getType()))
                .project(projectEntity)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(creator)
                .assignedTo(null)
                .build();

        if(taskDTO.getAssigneeId() != null) {
            UserEntity assignee = userService.getUserEntityById(taskDTO.getAssigneeId());
            task.setAssignedTo(assignee);
        }

        TaskEntity created = taskRepository.save(task);

        return MappingEntityUtil.mapTaskToDTO(created);
    }

    public TaskDTO updateTask(Long taskId, TaskDTO taskDTO) {
        TaskEntity taskEntity = getTaskEntityById(taskId);
        UserEntity author = userService.getUserEntityById(taskDTO.getCreatorId());
        ProjectEntity projectEntity = projectService.getProjectEntity(taskDTO.getProjectId());

        if (!Objects.equals(author.getId(), UserPrincipalUtil.getCurrentLoggedUserId())) {
            throw new IllegalArgumentException("You are not the author of this task");
        }

        if(taskDTO.getAssigneeId() != null) {
            UserEntity assignee = userService.getUserEntityById(taskDTO.getAssigneeId());
            taskEntity.setAssignedTo(assignee);
        }

        taskEntity.setProject(projectEntity);
        taskEntity.setUpdatedAt(LocalDateTime.now());
        taskEntity.setTitle(taskDTO.getTitle());
        taskEntity.setDescription(taskDTO.getDescription());
        taskEntity.setStatus(TaskStatus.valueOf(taskDTO.getStatus()));
        taskEntity.setType(TaskType.valueOf(taskDTO.getType()));

        TaskEntity saved = taskRepository.save(taskEntity);

        return MappingEntityUtil.mapTaskToDTO(saved);
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
