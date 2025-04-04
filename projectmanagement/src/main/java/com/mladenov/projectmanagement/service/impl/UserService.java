package com.mladenov.projectmanagement.service.impl;

import com.mladenov.projectmanagement.exception.EntityNotFoundException;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.model.entity.UserRoleEntity;
import com.mladenov.projectmanagement.repository.UserRepository;
import com.mladenov.projectmanagement.repository.UserRoleRepository;
import com.mladenov.projectmanagement.service.IUserService;
import org.springframework.stereotype.Service;

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
}
