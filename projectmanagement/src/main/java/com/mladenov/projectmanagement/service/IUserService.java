package com.mladenov.projectmanagement.service;

import com.mladenov.projectmanagement.model.entity.UserEntity;

public interface IUserService {
    UserEntity getUserEntityById(Long userId);
}
