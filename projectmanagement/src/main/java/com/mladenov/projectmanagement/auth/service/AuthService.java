package com.mladenov.projectmanagement.auth.service;


import com.mladenov.projectmanagement.auth.models.AuthRequest;
import com.mladenov.projectmanagement.auth.models.AuthResponse;
import com.mladenov.projectmanagement.auth.models.RegisterDTO;
import com.mladenov.projectmanagement.auth.util.JwtUtil;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.model.entity.UserRoleEntity;
import com.mladenov.projectmanagement.service.IUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(IUserService userService,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterDTO dto) {
        UserRoleEntity userRoleEntity = userService.getUserRoleEntityByUserRole(dto.getRole());

        UserEntity user = UserEntity.builder()
                .username(dto.getUsername())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .createdAt(LocalDateTime.now())
                .lastModified(LocalDateTime.now())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .userRole(userRoleEntity)
                .active(true)
                .build();

        UserEntity saved = userService.saveUserEntity(user);
        String jwtToken = jwtUtil.generateToken(saved);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        UserEntity user = userService.getUserEntityByUsername(request.getUsername());
        String jwtToken = jwtUtil.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}
