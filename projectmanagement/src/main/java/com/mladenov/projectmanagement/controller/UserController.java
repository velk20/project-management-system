package com.mladenov.projectmanagement.controller;

import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.task.TaskDTO;
import com.mladenov.projectmanagement.model.dto.user.ChangeUserPasswordDTO;
import com.mladenov.projectmanagement.model.dto.user.UserDTO;
import com.mladenov.projectmanagement.service.IUserService;
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

@Tag(name = "Users Controller", description = "Operations related to user management")
@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by id")
    public ResponseEntity<?> getUserById(@Parameter(description = "ID of the user") @PathVariable Long userId) {
        UserDTO userDTO = userService.getUserById(userId);

        return AppResponseUtil.success()
                .withData(userDTO)
                .build();
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<?> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();

        return AppResponseUtil.success()
                .withData(users)
                .build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by username")
    public ResponseEntity<?> searchUsers(@RequestParam("username") String username) {
        List<UserDTO> users = userService.searchUsersByUsername(username);

        return AppResponseUtil.success()
                .withData(users)
                .build();
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user profile")
    public ResponseEntity<?> updateProfile(@Parameter(description = "ID of the user") @PathVariable Long userId,
                                           @Valid @RequestBody UserDTO userDTO,
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

        UserDTO user = userService.updateProfile(userId, userDTO);

        return AppResponseUtil.success()
                .withMessage("User profile updated successfully")
                .withData(user)
                .build();
    }

    @PutMapping("/change-password")
    @Operation(summary = "Update user password")
    public ResponseEntity<?> updateUserPassword(@Valid @RequestBody ChangeUserPasswordDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());

            return AppResponseUtil.error(HttpStatus.BAD_REQUEST)
                    .withErrors(errorMessages)
                    .build();
        }

        this.userService.changePassword(dto);
        return AppResponseUtil.success()
                .withMessage("Password updated successfully")
                .build();
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user profile")
    public ResponseEntity<?> deleteProfile(@Parameter(description = "ID of the user") @PathVariable Long userId) {
        userService.deleteProfile(userId);

        return AppResponseUtil.success()
                .withMessage("Profile deleted successfully!")
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
