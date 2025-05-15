package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.EntityTestUtil;
import com.mladenov.projectmanagement.auth.service.CustomUserDetails;
import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.user.ChangeUserPasswordDTO;
import com.mladenov.projectmanagement.model.dto.user.UserDTO;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.model.entity.UserRoleEntity;
import com.mladenov.projectmanagement.repository.UserRepository;
import com.mladenov.projectmanagement.repository.UserRoleRepository;
import com.mladenov.projectmanagement.service.impl.UserService;
import com.mladenov.projectmanagement.util.MappingEntityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userRoleRepository = mock(UserRoleRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, userRoleRepository, passwordEncoder);
    }

    @Test
    void testGetUserEntityById_Found() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserEntity result = userService.getUserEntityById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetUserEntityById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserEntityById(1L));
    }

    @Test
    void testUpdateProfile_UsernameAndEmailChanged() {
        UserEntity existingUser = new UserEntity();
        existingUser.setId(1L);
        existingUser.setUsername("oldUser");
        existingUser.setEmail("old@mail.com");

        UserRoleEntity role = new UserRoleEntity();
        role.setUserRole("USER");
        existingUser.setUserRole(role);

        UserDTO dto = new UserDTO();
        dto.setUsername("newUser");
        dto.setEmail("new@mail.com");
        dto.setRole("ADMIN");
        dto.setActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(userRoleRepository.findUserRoleByUserRole("ADMIN"))
                .thenReturn(Optional.of(EntityTestUtil.createUserRoleEntity("ADMIN")));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        try (MockedStatic<MappingEntityUtil> mocked = mockStatic(MappingEntityUtil.class)) {
            mocked.when(() -> MappingEntityUtil.mapUserDTO(any()))
                    .thenAnswer(inv -> {
                        UserEntity arg = inv.getArgument(0);
                        UserDTO result = new UserDTO();
                        result.setUsername(arg.getUsername());
                        result.setEmail(arg.getEmail());
                        return result;
                    });

            UserDTO result = userService.updateProfile(1L, dto);

            assertEquals("newUser", result.getUsername());
            assertEquals("new@mail.com", result.getEmail());
        }
    }

    @Test
    void testUpdateProfile_UsernameAlreadyTaken() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("old");

        UserDTO dto = new UserDTO();
        dto.setUsername("taken");
        dto.setEmail("same@mail.com");
        dto.setRole("USER");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("taken")).thenReturn(Optional.of(new UserEntity()));

        assertThrows(IllegalArgumentException.class, () -> userService.updateProfile(1L, dto));
    }

    @Test
    void testChangePassword_Success() {
        ChangeUserPasswordDTO dto = new ChangeUserPasswordDTO();
        dto.setOldPassword("123");
        dto.setNewPassword("new123");

        UserEntity user = EntityTestUtil.createUserEntity(1L);

        CustomUserDetails principal = new CustomUserDetails(user);
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(principal, null)
        );

        when(userRepository.findByUsername("test1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123", "123")).thenReturn(true);
        when(passwordEncoder.encode("new123")).thenReturn("encodedNew");

        userService.changePassword(dto);

        assertEquals("encodedNew", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void testChangePassword_IncorrectOldPassword() {
        ChangeUserPasswordDTO dto = new ChangeUserPasswordDTO();
        dto.setOldPassword("wrong");
        dto.setNewPassword("new123");

        UserEntity user = EntityTestUtil.createUserEntity(1L);

        CustomUserDetails principal = new CustomUserDetails(user);
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(principal, null)
        );

        when(userRepository.findByUsername("test1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedOld")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.changePassword(dto));
    }
}
