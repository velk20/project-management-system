package com.mladenov.projectmanagement.auth.controller;

import com.mladenov.projectmanagement.auth.models.AuthRequest;
import com.mladenov.projectmanagement.auth.models.AuthResponse;
import com.mladenov.projectmanagement.auth.models.RegisterDTO;
import com.mladenov.projectmanagement.auth.service.AuthService;
import com.mladenov.projectmanagement.auth.service.TokenInvalidationService;
import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.util.AppResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Authentication Controller", description = "Operations related to authentication")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final TokenInvalidationService tokenInvalidationService;

    public AuthController(AuthService authService, TokenInvalidationService tokenInvalidationService) {
        this.authService = authService;
        this.tokenInvalidationService = tokenInvalidationService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register User")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());

            return AppResponseUtil.error(HttpStatus.BAD_REQUEST)
                    .withErrors(errorMessages)
                    .build();
        }

        AuthResponse registered = authService.register(dto);
        return AppResponseUtil.created()
                .withData(registered)
                .withMessage("Registration was successfully")
                .build();
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate/Login User")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthRequest authRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());

            return AppResponseUtil.error(HttpStatus.BAD_REQUEST)
                    .withErrors(errorMessages)
                    .build();
        }
        AuthResponse authenticated = authService.authenticate(authRequest);
        return AppResponseUtil.success()
                .withMessage("User successfully logged in")
                .withData(authenticated)
                .build();
    }

    @PostMapping("/invalidate-all-tokens")
    @Operation(summary = "Invalidate all tokens")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> invalidateAllTokens() {
        tokenInvalidationService.invalidateAllTokens();

        return AppResponseUtil.success()
                .withMessage("All tokens were invalidated")
                .build();
    }

    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<?> handleAuthenticationServiceException(AuthenticationServiceException ex) {
        return AppResponseUtil.error(HttpStatus.UNAUTHORIZED)
                .withMessage(ex.getMessage())
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        return AppResponseUtil.error(HttpStatus.NOT_FOUND)
                .withMessage(ex.getMessage())
                .build();
    }

}
