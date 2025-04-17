package com.mladenov.projectmanagement.service.impl;

import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.task.PageableTasksDTO;
import com.mladenov.projectmanagement.model.dto.task.TaskDTO;
import com.mladenov.projectmanagement.model.entity.ProjectEntity;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.model.enums.TaskStatus;
import com.mladenov.projectmanagement.model.enums.TaskType;
import com.mladenov.projectmanagement.repository.TaskRepository;
import com.mladenov.projectmanagement.service.IProjectService;
import com.mladenov.projectmanagement.service.ITaskService;
import com.mladenov.projectmanagement.service.IUserService;
import com.mladenov.projectmanagement.util.MappingEntityUtil;
import com.mladenov.projectmanagement.util.UserPrincipalUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TaskService implements ITaskService {
    private final TaskRepository taskRepository;
    private final IUserService userService;
    private final IProjectService projectService;

    public TaskService(TaskRepository taskRepository, IUserService userService, IProjectService projectService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.projectService = projectService;
    }

    @Override
    public TaskDTO getTaskByID(Long taskId) {
        TaskEntity taskEntity = getTaskEntityById(taskId);

        return MappingEntityUtil.mapTaskToDTO(taskEntity);
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        return taskRepository
                .findAll()
                .stream()
                .map(MappingEntityUtil::mapTaskToDTO)
                .collect(Collectors.toList());
    }

    @Override
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

    @Override
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

    @Override
    public TaskEntity getTaskEntityById(Long taskId) {
        return taskRepository
                .findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id=" + taskId + " not found"));
    }

    @Override
    public void deleteTaskById(Long taskId) {
        TaskEntity taskEntity = getTaskEntityById(taskId);
        taskRepository.delete(taskEntity);
    }

    @Override
    public PageableTasksDTO searchTasks(Long userId, String title, String status, String type, Pageable pageable) {
        Specification<TaskEntity> spec = Specification.where(null);

        UserEntity userEntity = this.userService.getUserEntityById(userId);

        if (userEntity != null ) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("assignedTo"), userEntity));
        }

        if (title != null && !title.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%")
            );
        }

        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("status")), "%" + status.toLowerCase() + "%")
            );
        }

        if (type != null && !type.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("type")), "%" + type.toLowerCase() + "%")
            );
        }

        Page<TaskEntity> entityPage = taskRepository.findAll(spec, pageable);
        return new PageableTasksDTO(
                entityPage.stream().map(MappingEntityUtil::mapTaskToDTO).toList(),
                entityPage.getTotalPages(),
                entityPage.getTotalElements());
    }

    @Override
    public PageableTasksDTO getTasksForUser(Long userId, Pageable pageable) {
        UserEntity userEntity = userService.getUserEntityById(userId);
        Page<TaskEntity> entityPage = taskRepository.findAllByCreatedByOrAssignedTo(userEntity, userEntity, pageable);
        return new PageableTasksDTO(
                entityPage.stream().map(MappingEntityUtil::mapTaskToDTO).toList(),
                entityPage.getTotalPages(),
                entityPage.getTotalElements()
        );
    }

    @Override
    public PageableTasksDTO getTasksForProject(Long projectId, Pageable pageable) {
        ProjectEntity projectEntity = projectService.getProjectEntity(projectId);
        Page<TaskEntity> allByProject = taskRepository.findAllByProject(projectEntity, pageable);
        return new PageableTasksDTO(
                allByProject.stream().map(MappingEntityUtil::mapTaskToDTO).toList(),
                allByProject.getTotalPages(),
                allByProject.getTotalElements()
        );
    }
}
