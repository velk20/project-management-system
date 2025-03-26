package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.model.dto.project.ProjectDTO;
import com.mladenov.projectmanagement.model.dto.project.UpdateProjectDTO;
import com.mladenov.projectmanagement.model.entity.ProjectEntity;

import java.util.List;

public interface IProjectService {
    ProjectDTO getProjectByID(Long projectId);

    ProjectEntity getProjectEntity(Long projectId);

    List<ProjectDTO> getAllProjects();

    ProjectDTO createProject(ProjectDTO projectDTO);

    void deleteProjectById(Long projectId);

    ProjectDTO updateProject(Long projectId, UpdateProjectDTO projectDTO);
}
