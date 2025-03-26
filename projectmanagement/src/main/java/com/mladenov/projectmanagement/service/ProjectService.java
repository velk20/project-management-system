package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.project.ProjectDTO;
import com.mladenov.projectmanagement.model.dto.project.UpdateProjectDTO;
import com.mladenov.projectmanagement.model.entity.ProjectEntity;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.repository.ProjectRepository;
import com.mladenov.projectmanagement.repository.TaskRepository;
import com.mladenov.projectmanagement.util.MappingEntityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final TaskRepository taskRepository;

    public ProjectService(ProjectRepository projectRepository, UserService userService, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.taskRepository = taskRepository;
    }

    public ProjectDTO getProjectByID(Long projectId) {
        ProjectEntity projectEntity = getProjectEntity(projectId);
        return MappingEntityUtil.mapProjectDTO(projectEntity);
    }

    public ProjectEntity getProjectEntity(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with id=" + projectId + " not found"));
    }

    private void isProjectWithNameExist(String name) {
        projectRepository.findByName(name).ifPresent(projectEntity -> {
            throw new IllegalArgumentException("Project with name=" + name + " already exists");
        });
    }

    public List<ProjectDTO> getAllProjects() {
        return this.projectRepository.findAll().stream().map(MappingEntityUtil::mapProjectDTO).toList();
    }

    public ProjectDTO createProject(ProjectDTO projectDTO) {
        isProjectWithNameExist(projectDTO.getName());
        UserEntity owner = userService.getUserEntityById(projectDTO.getOwnerId());
        ProjectEntity projectEntity = new ProjectEntity(
                projectDTO.getName(),
                projectDTO.getDescription(),
                owner,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        return MappingEntityUtil.mapProjectDTO(projectRepository.save(projectEntity));
    }

    public void deleteProjectById(Long projectId) {
        ProjectEntity projectEntity = getProjectEntity(projectId);
        projectRepository.delete(projectEntity);
    }

    public ProjectDTO updateProject(Long projectId, UpdateProjectDTO projectDTO) {
        ProjectEntity projectEntity = getProjectEntity(projectId);

        projectEntity.setDescription(projectDTO.getDescription());
        projectEntity.setUpdatedAt(LocalDateTime.now());


        validateProjectName(projectDTO, projectEntity);

        validateProjectTasks(projectDTO, projectEntity);

        validateProjectTeamMembers(projectDTO, projectEntity);

        ProjectEntity project = projectRepository.save(projectEntity);
        return MappingEntityUtil.mapProjectDTO(project);
    }

    private void validateProjectTeamMembers(UpdateProjectDTO projectDTO, ProjectEntity projectEntity) {
        if (!projectDTO.getTeamMembersId().isEmpty()) {
            List<Long> teamMembersId = projectDTO.getTeamMembersId();
            List<Long> existingTeamMembersIds = projectEntity.getTeamMembers()
                    .stream()
                    .map(UserEntity::getId)
                    .toList();
            
            teamMembersId = teamMembersId.stream()
                    .filter(id->!existingTeamMembersIds.contains(id))
                    .toList();

            for (Long id : teamMembersId) {
                UserEntity userEntity = userService.getUserEntityById(id);
                projectEntity.addTeamMember(userEntity);
            }

        }
    }

    private void validateProjectTasks(UpdateProjectDTO projectDTO, ProjectEntity projectEntity) {
        if (!projectDTO.getTasksId().isEmpty()) {
            List<Long> tasksId = projectDTO.getTasksId();
            Set<Long> existingTaskIds  = projectEntity.getTasks()
                                                      .stream()
                                                      .map(TaskEntity::getId)
                                                      .collect(Collectors.toSet());

            tasksId = tasksId.stream()
                    .filter(id -> !existingTaskIds.contains(id))
                    .toList();

            for (Long taskId : tasksId) {
                TaskEntity taskEntity = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Task with id=" + taskId + " not found"));
                projectEntity.addTask(taskEntity);
            }

        }
    }

    private void validateProjectName(UpdateProjectDTO projectDTO, ProjectEntity projectEntity) {
        if (!projectEntity.getName().equals(projectDTO.getName())) {
            isProjectWithNameExist(projectDTO.getName());
            projectEntity.setName(projectDTO.getName());
        }
    }
}
