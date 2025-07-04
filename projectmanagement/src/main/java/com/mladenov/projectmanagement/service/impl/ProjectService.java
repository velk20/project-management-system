package com.mladenov.projectmanagement.service.impl;

import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.project.ProjectDTO;
import com.mladenov.projectmanagement.model.dto.project.UpdateProjectDTO;
import com.mladenov.projectmanagement.model.dto.user.UserDTO;
import com.mladenov.projectmanagement.model.entity.ProjectEntity;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.repository.ProjectRepository;
import com.mladenov.projectmanagement.service.IProjectService;
import com.mladenov.projectmanagement.service.ITaskService;
import com.mladenov.projectmanagement.service.IUserService;
import com.mladenov.projectmanagement.util.MappingEntityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectService implements IProjectService {
    @Value("${kafka.topic.project.invitation}")
    private String projectInvitationTopic;

    private final ProjectRepository projectRepository;
    private final IUserService userService;
    private final ITaskService taskService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public ProjectService(ProjectRepository projectRepository, IUserService userService, @Lazy ITaskService taskService, KafkaTemplate<String, String> kafkaTemplate) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.taskService = taskService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public ProjectDTO getProjectByID(Long projectId) {
        ProjectEntity projectEntity = getProjectEntity(projectId);
        return MappingEntityUtil.mapProjectDTO(projectEntity);
    }

    @Override
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        this.isProjectWithNameExist(projectDTO.getName());

        UserEntity owner = userService.getUserEntityById(projectDTO.getOwnerId());

        ProjectEntity projectEntity = new ProjectEntity(
                projectDTO.getName(),
                projectDTO.getDescription(),
                owner,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new ArrayList<>(),
                List.of(owner)
        );

        return MappingEntityUtil.mapProjectDTO(projectRepository.save(projectEntity));
    }

    @Override
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

    @Override
    public List<ProjectDTO> getAllProjects() {
        return this.projectRepository.findAll().stream().map(MappingEntityUtil::mapProjectDTO).toList();
    }

    @Override
    public void deleteProjectById(Long projectId) {
        ProjectEntity projectEntity = getProjectEntity(projectId);
        projectRepository.delete(projectEntity);
    }

    @Override
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

    @Override
    public List<ProjectDTO> getAllProjectsForUser(Long userId) {
        UserEntity userEntity = userService.getUserEntityById(userId);

        List<ProjectEntity> projects = projectRepository.findProjectsByUserOwnershipOrTeamMembership(userEntity);

        return projects.stream().map(MappingEntityUtil::mapProjectDTO).toList();
    }

    @Override
    public List<UserDTO> getProjectMemebersByID(Long projectId) {
        ProjectEntity projectEntity = this.getProjectEntity(projectId);
        List<UserEntity> teamMembers = projectEntity.getTeamMembers();

        return teamMembers.stream().map(MappingEntityUtil::mapUserDTO).toList();
    }

    private void validateProjectTeamMembers(UpdateProjectDTO projectDTO, ProjectEntity projectEntity) {
        List<UserEntity> updatedTeamMembers = new ArrayList<>();

        if (!projectDTO.getTeamMembersId().isEmpty()) {
            List<Long> teamMembersId = projectDTO.getTeamMembersId();

            Set<Long> existingMemberIds = projectEntity.getTeamMembers().stream()
                    .map(UserEntity::getId)
                    .collect(Collectors.toSet());

            List<Long> newMemberIds = teamMembersId.stream()
                    .filter(id -> !existingMemberIds.contains(id))
                    .toList();

            for (Long id : newMemberIds) {
                UserEntity userEntity = userService.getUserEntityById(id);
                updatedTeamMembers.add(userEntity);

                String message = userEntity.getEmail() + ":" + projectEntity.getName();
                kafkaTemplate.send(projectInvitationTopic, message);
            }

            updatedTeamMembers.addAll(projectEntity.getTeamMembers());
            projectEntity.setTeamMembers(updatedTeamMembers);
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
                TaskEntity taskEntity = taskService.getTaskEntityById(taskId);
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
