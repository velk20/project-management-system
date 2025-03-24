package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.task.TaskCommentDTO;
import com.mladenov.projectmanagement.model.dto.task.TaskDTO;
import com.mladenov.projectmanagement.model.entity.ProjectEntity;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.model.enums.TaskStatus;
import com.mladenov.projectmanagement.model.enums.TaskType;
import com.mladenov.projectmanagement.repository.ProjectRepository;
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
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final TaskCommentService taskCommentService;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, UserService userService, TaskCommentService taskCommentService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.taskCommentService = taskCommentService;
    }

    public TaskDTO getTaskByID(Long taskId) {
        TaskEntity taskEntity = getTaskEntityById(taskId);
        List<TaskCommentDTO> commentDTOS = taskCommentService.getTaskComments(taskEntity.getId());

        return MappingEntityUtil.mapTaskToDTO(taskEntity, commentDTOS);
    }

    public List<TaskDTO> getAllTasks() {
        return taskRepository
                .findAll()
                .stream()
                .map(t->{
                    List<TaskCommentDTO> commentDTOS = taskCommentService.getTaskComments(t.getId());
                    return MappingEntityUtil.mapTaskToDTO(t, commentDTOS);
                })
                .collect(Collectors.toList());
    }

    public TaskDTO createTask(TaskDTO taskDTO) {
        UserEntity creator = userService.getById(taskDTO.getCreatorId());
        ProjectEntity projectEntity = this.getProjectEntityById(taskDTO.getProjectId());

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
            UserEntity assignee = userService.getById(taskDTO.getAssigneeId());
            task.setAssignedTo(assignee);
        }

        TaskEntity created = taskRepository.save(task);
        List<TaskCommentDTO> commentDTOS = taskCommentService.getTaskComments(created.getId());

        return MappingEntityUtil.mapTaskToDTO(created, commentDTOS);
    }

    public TaskDTO updateTask(Long taskId, TaskDTO taskDTO) {
        TaskEntity taskEntity = getTaskEntityById(taskId);
        UserEntity author = userService.getById(taskDTO.getCreatorId());
        ProjectEntity projectEntity = this.getProjectEntityById(taskDTO.getProjectId());

        if (!Objects.equals(author.getId(), UserPrincipalUtil.getCurrentLoggedUserId())) {
            throw new IllegalArgumentException("You are not the author of this task");
        }

        if(taskDTO.getAssigneeId() != null) {
            UserEntity assignee = userService.getById(taskDTO.getAssigneeId());
            taskEntity.setAssignedTo(assignee);
        }

        taskEntity.setProject(projectEntity);
        taskEntity.setUpdatedAt(LocalDateTime.now());
        taskEntity.setTitle(taskDTO.getTitle());
        taskEntity.setDescription(taskDTO.getDescription());
        taskEntity.setStatus(TaskStatus.valueOf(taskDTO.getStatus()));
        taskEntity.setType(TaskType.valueOf(taskDTO.getType()));

        TaskEntity saved = taskRepository.save(taskEntity);
        List<TaskCommentDTO> commentDTOS = taskCommentService.getTaskComments(saved.getId());

        return MappingEntityUtil.mapTaskToDTO(saved, commentDTOS);
    }

    public TaskEntity getTaskEntityById(Long taskId) {
        return taskRepository
                .findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id=" + taskId + " not found"));
    }

    private ProjectEntity getProjectEntityById(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with id=" + projectId + " not found"));
    }

    public void deleteTaskById(Long taskId) {
        TaskEntity taskEntity = getTaskEntityById(taskId);
        taskRepository.delete(taskEntity);
    }
}
