package com.mladenov.projectmanagement.util;

import com.mladenov.projectmanagement.model.dto.project.ProjectDTO;
import com.mladenov.projectmanagement.model.dto.task.TaskCommentDTO;
import com.mladenov.projectmanagement.model.dto.task.TaskDTO;
import com.mladenov.projectmanagement.model.dto.user.UserDTO;
import com.mladenov.projectmanagement.model.entity.ProjectEntity;
import com.mladenov.projectmanagement.model.entity.TaskCommentEntity;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import com.mladenov.projectmanagement.model.entity.UserEntity;

import java.util.List;

public class MappingEntityUtil {
    public static UserDTO mapUserDTO(UserEntity userEntity) {
        return UserDTO.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .active(userEntity.isActive())
                .build();
    }
    public static ProjectDTO mapProjectDTO(ProjectEntity projectEntity, List<TaskDTO> tasksDTO, List<UserDTO> teamMembersDTO) {
        return ProjectDTO.builder()
                .id(projectEntity.getId())
                .name(projectEntity.getName())
                .description(projectEntity.getDescription())
                .ownerId(projectEntity.getOwner().getId())
                .createdAt(projectEntity.getCreatedAt())
                .updatedAt(projectEntity.getUpdatedAt())
                .tasks(tasksDTO)
                .teamMembers(teamMembersDTO)
                .build();
    }
    public static TaskDTO mapTaskToDTO(TaskEntity taskEntity, List<TaskCommentDTO> taskComments) {
        return TaskDTO.builder()
                .id(taskEntity.getId())
                .title(taskEntity.getTitle())
                .description(taskEntity.getDescription())
                .status(taskEntity.getStatus().name())
                .type(taskEntity.getType().name())
                .projectId(taskEntity.getProject().getId())
                .createdAt(taskEntity.getCreatedAt())
                .updatedAt(taskEntity.getUpdatedAt())
                .creatorId(taskEntity.getCreatedBy().getId())
                .assigneeId(taskEntity.getAssignedTo() != null ? taskEntity.getAssignedTo().getId() : null)
                .comments(taskComments)
                .build();
    }

    public static TaskCommentDTO mapCommentToDTO(TaskCommentEntity taskCommentEntity) {
        return TaskCommentDTO.builder()
                .authorId(taskCommentEntity.getAuthor().getId())
                .taskId(taskCommentEntity.getTask().getId())
                .content(taskCommentEntity.getContent())
                .createdAt(taskCommentEntity.getCreatedAt())
                .updatedAt(taskCommentEntity.getUpdatedAt())
                .build();
    }
}
