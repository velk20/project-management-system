package com.mladenov.projectmanagement.controller;

import com.mladenov.projectmanagement.model.dto.user.UserDTO;
import com.mladenov.projectmanagement.service.IUserService;
import com.mladenov.projectmanagement.util.AppResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> searchUsers(@RequestParam("username") String username) {
        List<UserDTO> users = userService.searchUsersByUsername(username);

        return AppResponseUtil.success()
                .withData(users)
                .build();
    }
}
