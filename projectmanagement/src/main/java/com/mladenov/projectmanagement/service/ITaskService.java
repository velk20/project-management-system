package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.model.dto.task.TaskDTO;
import com.mladenov.projectmanagement.model.entity.TaskEntity;

import java.util.List;

public interface ITaskService {
    TaskDTO getTaskByID(Long taskId);

    List<TaskDTO> getAllTasks();

    TaskDTO createTask(TaskDTO taskDTO);

    TaskDTO updateTask(Long taskId, TaskDTO taskDTO);

    TaskEntity getTaskEntityById(Long taskId);

    void deleteTaskById(Long taskId);

    List<TaskDTO> getTasksForUser(Long userId);

    List<TaskDTO> getTasksForProject(Long projectId);
}
