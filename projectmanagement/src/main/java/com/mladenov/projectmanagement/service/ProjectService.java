package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.project.ProjectDTO;
import com.mladenov.projectmanagement.model.entity.ProjectEntity;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.repository.ProjectRepository;
import com.mladenov.projectmanagement.util.MappingEntityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;

    public ProjectService(ProjectRepository projectRepository, UserService userService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
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

    public ProjectDTO updateProject(Long projectId, ProjectDTO projectDTO) {
        ProjectEntity projectEntity = getProjectEntity(projectId);
        if (!projectEntity.getName().equals(projectDTO.getName())) {
            isProjectWithNameExist(projectDTO.getName());
            projectEntity.setName(projectDTO.getName());
        }
        projectEntity.setDescription(projectDTO.getDescription());
        projectEntity.setUpdatedAt(LocalDateTime.now());

        ProjectEntity project = projectRepository.save(projectEntity);
        return MappingEntityUtil.mapProjectDTO(project);
    }
}
