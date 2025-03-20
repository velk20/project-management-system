package com.mladenov.projectmanagement.controller;

import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.task.TaskCommentDTO;
import com.mladenov.projectmanagement.model.dto.task.TaskDTO;
import com.mladenov.projectmanagement.service.TaskCommentService;
import com.mladenov.projectmanagement.util.AppResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Tasks Comments Controller", description = "Operations related to tasks comments management")
@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
@SecurityRequirement(name = "bearerAuth")
public class TaskCommentController {
    private final TaskCommentService taskCommentService;

    public TaskCommentController(TaskCommentService taskCommentService) {
        this.taskCommentService = taskCommentService;
    }

    @GetMapping
    @Operation(summary = "Get task comments")
    public ResponseEntity<?> getTaskComments(@Parameter(description = "ID of the task") @PathVariable Long taskId) {
        List<TaskCommentDTO> taskComments = taskCommentService.getTaskComments(taskId);

        return AppResponseUtil.success()
                .withData(taskComments)
                .build();
    }

    @PostMapping
    @Operation(summary = "Create task comment")
    public ResponseEntity<?> createTaskComment(@Parameter(description = "ID of the task") @PathVariable Long taskId,
                                               @Valid @RequestBody TaskCommentDTO taskCommentDTO,
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

        TaskCommentDTO comment = taskCommentService.createTaskComment(taskId, taskCommentDTO);

        return AppResponseUtil.created()
                .withData(comment)
                .withMessage("Comment was created")
                .build();
    }

    @PutMapping("{/commentId}")
    @Operation(summary = "Update task comment")
    public ResponseEntity<?> updateTaskComment(@Parameter(description = "ID of the task") @PathVariable Long taskId,
                                               @Parameter(description = "Comment ID of the task") @PathVariable Long commentId,
                                               @Valid @RequestBody TaskCommentDTO taskCommentDTO,
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

        TaskCommentDTO comment = taskCommentService.updateTaskComment(taskId, commentId, taskCommentDTO);

        return AppResponseUtil.created()
                .withData(comment)
                .withMessage("Comment was updated")
                .build();
    }

    @DeleteMapping("{/commentId}")
    @Operation(summary = "Delete task comment")
    public ResponseEntity<?> deleteTaskComment(@Parameter(description = "ID of the task") @PathVariable Long taskId,
                                               @Parameter(description = "Comment ID of the task") @PathVariable Long commentId){
        taskCommentService.deleteTaskComment(taskId, commentId);

        return AppResponseUtil.success()
                .withMessage("Task comment was deleted.")
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
