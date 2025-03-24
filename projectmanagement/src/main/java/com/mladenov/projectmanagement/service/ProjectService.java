package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.project.ProjectDTO;
import com.mladenov.projectmanagement.model.dto.task.TaskDTO;
import com.mladenov.projectmanagement.model.dto.user.UserDTO;
import com.mladenov.projectmanagement.model.entity.ProjectEntity;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.repository.ProjectRepository;
import com.mladenov.projectmanagement.util.MappingEntityUtil;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final TaskService taskService;
    private final UserService userService;

    public ProjectService(ProjectRepository projectRepository, TaskService taskService, UserService userService) {
        this.projectRepository = projectRepository;
        this.taskService = taskService;
        this.userService = userService;
    }

    public ProjectDTO getProjectByID(Long projectId) {
        ProjectEntity projectEntity = getProjectEntity(projectId);
        return mapProject(projectEntity);
    }

    private ProjectDTO mapProject(ProjectEntity projectEntity) {
        List<TaskDTO> taskDTOS = projectEntity.getTasks().stream().map(taskDTO -> taskService.getTaskByID(taskDTO.getId())).toList();
        List<UserDTO> userDTOS = projectEntity.getTeamMembers().stream().map(MappingEntityUtil::mapUserDTO).toList();

        return MappingEntityUtil.mapProjectDTO(projectEntity, taskDTOS, userDTOS);
    }

    private ProjectEntity getProjectEntity(Long projectId) {
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
        return this.projectRepository.findAll().stream().map(this::mapProject).toList();
    }

    public ProjectDTO createProject(ProjectDTO projectDTO) {
        isProjectWithNameExist(projectDTO.getName());
        UserEntity owner = userService.getById(projectDTO.getOwnerId());
        ProjectEntity projectEntity = new ProjectEntity(
                projectDTO.getName(),
                projectDTO.getDescription(),
                owner,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        return MappingEntityUtil.mapProjectDTO(projectRepository.save(projectEntity), new ArrayList<>(), new ArrayList<>());
    }

    public void deleteProjectById(Long projectId) {
        ProjectEntity projectEntity = getProjectEntity(projectId);
        projectRepository.delete(projectEntity);
    }

    public ProjectDTO updateProject(Long projectId, ProjectDTO projectDTO) {
        ProjectEntity projectEntity = getProjectEntity(projectId);
        if (!projectEntity.getName().equals(projectDTO.getName())) {
            isProjectWithNameExist(projectDTO.getName());
            projectEntity.setName(projectDTO.getName());
        }
        projectEntity.setDescription(projectDTO.getDescription());
        projectEntity.setUpdatedAt(LocalDateTime.now());

        ProjectEntity project = projectRepository.save(projectEntity);
        return mapProject(project);
    }
}
