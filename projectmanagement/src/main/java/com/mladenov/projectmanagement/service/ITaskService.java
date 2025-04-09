package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.model.dto.task.PageableTasksDTO;
import com.mladenov.projectmanagement.model.dto.task.TaskDTO;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITaskService {
    TaskDTO getTaskByID(Long taskId);

    List<TaskDTO> getAllTasks();

    TaskDTO createTask(TaskDTO taskDTO);

    TaskDTO updateTask(Long taskId, TaskDTO taskDTO);

    TaskEntity getTaskEntityById(Long taskId);

    void deleteTaskById(Long taskId);

    PageableTasksDTO getTasksForUser(Long userId, Pageable pageable);

    List<TaskDTO> getTasksForProject(Long projectId, Pageable pageable);
}
