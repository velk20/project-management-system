package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.model.dto.task.TaskCommentDTO;
import com.mladenov.projectmanagement.model.entity.TaskCommentEntity;

import java.util.List;

public interface ITaskCommentService {
    List<TaskCommentDTO> getTaskComments(Long taskId);

    TaskCommentDTO createTaskComment(Long taskId, TaskCommentDTO taskCommentDTO);

    TaskCommentDTO updateTaskComment(Long taskId, Long commentId, TaskCommentDTO taskCommentDTO);

    TaskCommentEntity getTaskCommentEntityById(Long commentId);

    void deleteTaskComment(Long taskId, Long commentId);
}
