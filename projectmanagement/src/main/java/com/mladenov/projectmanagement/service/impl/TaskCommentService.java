package com.mladenov.projectmanagement.service.impl;

import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.task.TaskCommentDTO;
import com.mladenov.projectmanagement.model.entity.TaskCommentEntity;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.repository.TaskCommentRepository;
import com.mladenov.projectmanagement.service.ITaskCommentService;
import com.mladenov.projectmanagement.service.ITaskService;
import com.mladenov.projectmanagement.service.IUserService;
import com.mladenov.projectmanagement.util.MappingEntityUtil;
import com.mladenov.projectmanagement.util.UserPrincipalUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TaskCommentService implements ITaskCommentService {
    private final TaskCommentRepository taskCommentRepository;
    private final IUserService userService;
    private final ITaskService taskService;

    public TaskCommentService(TaskCommentRepository taskCommentRepository, IUserService userService, ITaskService taskService) {
        this.taskCommentRepository = taskCommentRepository;
        this.userService = userService;
        this.taskService = taskService;
    }

    @Override
    public List<TaskCommentDTO> getTaskComments(Long taskId) {
        TaskEntity task = taskService.getTaskEntityById(taskId);
        List<TaskCommentEntity> taskComments = taskCommentRepository.findAllByTask(task);
        return taskComments.stream().map(MappingEntityUtil::mapCommentToDTO).collect(Collectors.toList());
    }



    @Override
    public TaskCommentDTO createTaskComment(Long taskId, TaskCommentDTO taskCommentDTO) {
        TaskEntity taskEntity = taskService.getTaskEntityById(taskId);
        UserEntity authorEntity = userService.getUserEntityById(taskCommentDTO.getAuthorId());

        TaskCommentEntity taskCommentEntity = new TaskCommentEntity(taskEntity,
                                                                    taskCommentDTO.getContent(),
                                                                    authorEntity,
                                                                    LocalDateTime.now(),
                                                                    LocalDateTime.now());

        return MappingEntityUtil.mapCommentToDTO(taskCommentRepository.save(taskCommentEntity));
    }

    @Override
    public TaskCommentDTO updateTaskComment(Long taskId, Long commentId, TaskCommentDTO taskCommentDTO) {
        TaskEntity taskEntity = taskService.getTaskEntityById(taskId);
        UserEntity authorEntity = userService.getUserEntityById(taskCommentDTO.getAuthorId());
        TaskCommentEntity taskComment = getTaskCommentEntityById(commentId);

        if (!Objects.equals(taskComment.getAuthor().getId(), authorEntity.getId())) {
            throw new IllegalArgumentException("You are not the author of this task");
        }

        if (!Objects.equals(taskComment.getTask().getId(), taskEntity.getId())) {
            throw new IllegalArgumentException("Comment is not part of this task");
        }

        taskComment.setUpdatedAt(LocalDateTime.now());
        taskComment.setContent(taskCommentDTO.getContent());

        return MappingEntityUtil.mapCommentToDTO(taskCommentRepository.save(taskComment));
    }

    @Override
    public TaskCommentEntity getTaskCommentEntityById(Long commentId) {
        return taskCommentRepository
                .findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id=" + commentId + " was not found."));
    }

    @Override
    public void deleteTaskComment(Long taskId, Long commentId) {
        TaskEntity taskEntity = taskService.getTaskEntityById(taskId);
        TaskCommentEntity taskComment = getTaskCommentEntityById(commentId);

        if (!Objects.equals(taskComment.getTask().getId(), taskEntity.getId())) {
            throw new IllegalArgumentException("Comment is not part of this task");
        }

        if (!Objects.equals(taskComment.getAuthor().getId(), UserPrincipalUtil.getCurrentLoggedUserId())) {
            throw new IllegalArgumentException("You are not the author of this task");
        }

        taskCommentRepository.delete(taskComment);
    }


}
