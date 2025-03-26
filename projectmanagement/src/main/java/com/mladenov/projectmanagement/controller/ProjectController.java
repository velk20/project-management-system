package com.mladenov.projectmanagement.controller;

import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.project.ProjectDTO;
import com.mladenov.projectmanagement.model.dto.project.UpdateProjectDTO;
import com.mladenov.projectmanagement.model.dto.task.TaskDTO;
import com.mladenov.projectmanagement.service.ProjectService;
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

@Tag(name = "Project Controller", description = "Operations related to projects management")
@RestController
@RequestMapping("/api/v1/projects")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Get project by id")
    public ResponseEntity<?> getProjectById(@PathVariable("projectId") Long projectId) {
        ProjectDTO projectDTO = projectService.getProjectByID(projectId);

        return AppResponseUtil.success()
                .withData(projectDTO)
                .build();
    }

    @GetMapping
    @Operation(summary = "Get all projects")
    public ResponseEntity<?> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();

        return AppResponseUtil.success()
                .withData(projects)
                .build();
    }

    @PostMapping
    @Operation(summary = "Create project")
    public ResponseEntity<?> createProject(@Valid @RequestBody ProjectDTO projectDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());

            return AppResponseUtil.error(HttpStatus.BAD_REQUEST)
                    .withErrors(errorMessages)
                    .build();
        }

        ProjectDTO created = projectService.createProject(projectDTO);
        return AppResponseUtil.created()
                .withData(created)
                .withMessage("Project created successfully")
                .build();
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "Update project")
    public ResponseEntity<?> updateProject(@Parameter(description = "ID of the project") @PathVariable Long projectId,
                                           @Valid @RequestBody UpdateProjectDTO projectDTO,
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

        ProjectDTO updated = projectService.updateProject(projectId, projectDTO);
        return AppResponseUtil.success()
                .withData(updated)
                .withMessage("Project updated successfully")
                .build();
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "Delete project")
    public ResponseEntity<?> deleteProject(@Parameter(description = "ID of the project") @PathVariable Long projectId) {
        projectService.deleteProjectById(projectId);

        return AppResponseUtil.success()
                .withMessage("Project was deleted.")
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
