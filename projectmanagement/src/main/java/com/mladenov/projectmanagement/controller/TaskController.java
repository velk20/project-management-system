package com.mladenov.projectmanagement.controller;

import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.task.PageableTasksDTO;
import com.mladenov.projectmanagement.model.dto.task.TaskDTO;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import com.mladenov.projectmanagement.service.ITaskService;
import com.mladenov.projectmanagement.util.AppResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Tasks Controller", description = "Operations related to tasks management")
@RestController
@RequestMapping("/api/v1/tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {
    private final ITaskService taskService;

    public TaskController(ITaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get task by id")
    public ResponseEntity<?> getTaskById(@Parameter(description = "ID of the task") @PathVariable Long taskId) {
        TaskDTO taskDTO = taskService.getTaskByID(taskId);

        return AppResponseUtil.success()
                .withData(taskDTO)
                .build();
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all tasks by project id")
    public ResponseEntity<?> getAllTasksByProject(@Parameter(description = "ID of the project") @PathVariable Long projectId,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "1000") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageableTasksDTO tasks = taskService.getTasksForProject(projectId, pageable);

        return AppResponseUtil.success()
                .withData(tasks)
                .build();
    }

    @GetMapping
    @Operation(summary = "Get all tasks")
    public ResponseEntity<?> getAllTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();

        return AppResponseUtil.success()
                .withData(tasks)
                .build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search task")
    public ResponseEntity<?> searchTask( @RequestParam(required = false) Long userId,
                                         @RequestParam(required = false) String title,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(required = false) String type) {
        List<TaskDTO> tasks = taskService.searchTasks(userId, title, status, type);

        return AppResponseUtil.success()
                .withData(tasks)
                .build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all tasks for user")
    public ResponseEntity<?> getAllTasksForUser(@Parameter(description = "ID of the user") @PathVariable Long userId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "1000") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageableTasksDTO tasks = taskService.getTasksForUser(userId, pageable);

        return AppResponseUtil.success()
                .withData(tasks)
                .build();
    }

    @PostMapping
    @Operation(summary = "Create task")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskDTO taskDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());

            return AppResponseUtil.error(HttpStatus.BAD_REQUEST)
                    .withErrors(errorMessages)
                    .build();
        }

        TaskDTO created = taskService.createTask(taskDTO);
        return AppResponseUtil.created()
                .withData(created)
                .withMessage("Task created successfully")
                .build();
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update task")
    public ResponseEntity<?> updateTask(@Parameter(description = "ID of the task") @PathVariable Long taskId,
                                          @Valid @RequestBody TaskDTO taskDTO,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());

            return AppResponseUtil.error(HttpStatus.BAD_REQUEST)
                    .withErrors(errorMessages)
                    .build();
        }

        TaskDTO updated = taskService.updateTask(taskId, taskDTO);
        return AppResponseUtil.success()
                .withData(updated)
                .withMessage("Task updated successfully")
                .build();
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete task")
    public ResponseEntity<?> deleteTask(@Parameter(description = "ID of the task") @PathVariable Long taskId) {
        taskService.deleteTaskById(taskId);

        return AppResponseUtil.success()
                .withMessage("Task was deleted.")
                .build();
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return AppResponseUtil.error(HttpStatus.BAD_REQUEST)
                .logStackTrace(Arrays.toString(ex.getStackTrace()))
                .withMessage(ex.getMessage())
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        return AppResponseUtil.error(HttpStatus.NOT_FOUND)
                .logStackTrace(Arrays.toString(ex.getStackTrace()))
                .withMessage(ex.getMessage())
                .build();
    }
}
