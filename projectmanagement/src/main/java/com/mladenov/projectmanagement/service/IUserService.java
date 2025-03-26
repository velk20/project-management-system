package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.model.entity.UserRoleEntity;

public interface IUserService {
    UserEntity getUserEntityById(Long userId);
    UserEntity getUserEntityByEmail(String email);
    UserEntity getUserEntityByUsername(String username);
    UserEntity saveUserEntity(UserEntity userEntity);
    UserRoleEntity getUserRoleEntityByUserRole(String userRole);
}
