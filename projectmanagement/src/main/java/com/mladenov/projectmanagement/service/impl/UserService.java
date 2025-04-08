package com.mladenov.projectmanagement.service.impl;

import com.mladenov.projectmanagement.auth.service.CustomUserDetails;
import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.user.ChangeUserPasswordDTO;
import com.mladenov.projectmanagement.model.dto.user.UserDTO;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.model.entity.UserRoleEntity;
import com.mladenov.projectmanagement.repository.UserRepository;
import com.mladenov.projectmanagement.repository.UserRoleRepository;
import com.mladenov.projectmanagement.service.IUserService;
import com.mladenov.projectmanagement.util.MappingEntityUtil;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity getUserEntityById(Long userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " not found."));
    }

    @Override
    public UserEntity getUserEntityByEmail(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email=" + email + " not found."));
    }

    @Override
    public UserEntity getUserEntityByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User with username=" + username + " not found."));
    }

    @Override
    public UserEntity saveUserEntity(UserEntity userEntity) {
        return this.userRepository.save(userEntity);
    }

    @Override
    public UserRoleEntity getUserRoleEntityByUserRole(String userRole) {
       return userRoleRepository.findUserRoleByUserRole(userRole)
                .orElseThrow(() -> new EntityNotFoundException("No role=" + userRole + " was found!"));
    }

    @Override
    public UserDTO getUserById(Long userId) {
        UserEntity user = this.getUserEntityById(userId);
        return MappingEntityUtil.mapUserDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<UserEntity> users = this.userRepository.findAll();
        return users.stream().map(MappingEntityUtil::mapUserDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> searchUsersByUsername(String username) {
        List<UserEntity> userEntities = userRepository.searchByUsername(username);
        return userEntities.stream().map(MappingEntityUtil::mapUserDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO updateProfile(Long userId, UserDTO userDTO) {
        UserEntity userEntity = getUserEntityById(userId);

        validateProfileForUpdate(userDTO, userEntity);

        userEntity.setUsername(userDTO.getUsername());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setFirstName(userDTO.getFirstName());
        userEntity.setLastName(userDTO.getLastName());

        UserEntity save = userRepository.save(userEntity);
        return MappingEntityUtil.mapUserDTO(save);
    }

    private void validateProfileForUpdate(UserDTO dto, UserEntity userEntity) {
        if (!userEntity.getUsername().equals(dto.getUsername())) {
            Optional<UserEntity> user = this.userRepository.findByUsername(dto.getUsername());
            if (user.isPresent()) {
                throw new IllegalArgumentException("Username " + dto.getUsername() + " is already taken");
            } else {
                userEntity.setUsername(dto.getUsername());
            }
        }

        if (!userEntity.getEmail().equals(dto.getEmail())) {
            Optional<UserEntity> user = this.userRepository.findByEmail(dto.getEmail());
            if (user.isPresent()) {
                throw new IllegalArgumentException("Email " + dto.getEmail() + " is already taken");
            } else {
                userEntity.setEmail(dto.getEmail());
            }
        }

    }

    @Override
    public void deleteProfile(Long userId) {
        UserEntity userEntity = this.getUserEntityById(userId);

        this.userRepository.delete(userEntity);
    }

    @Override
    public void changePassword(ChangeUserPasswordDTO dto) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity userEntity = getUserEntityByUsername(userDetails.getUsername());

        if (this.passwordEncoder.matches(dto.getOldPassword(), userEntity.getPassword())) {
            userEntity.setPassword(this.passwordEncoder.encode(dto.getNewPassword()));
            this.userRepository.save(userEntity);
        } else {
            throw new IllegalArgumentException("Old password is incorrect");
        }
    }
}
