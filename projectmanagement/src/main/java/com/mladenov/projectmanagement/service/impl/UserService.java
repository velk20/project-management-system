package com.mladenov.projectmanagement.service.impl;

import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.dto.user.UserDTO;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.model.entity.UserRoleEntity;
import com.mladenov.projectmanagement.repository.UserRepository;
import com.mladenov.projectmanagement.repository.UserRoleRepository;
import com.mladenov.projectmanagement.service.IUserService;
import com.mladenov.projectmanagement.util.MappingEntityUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
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
}
