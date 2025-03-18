package com.mladenov.projectmanagement.util.validation;

import com.mladenov.projectmanagement.repository.UserRoleRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidateUserRoleValidator implements ConstraintValidator<ValidateUserRole, String> {
    private final UserRoleRepository userRoleRepository;

    public ValidateUserRoleValidator(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return userRoleRepository.findUserRoleByUserRole(value).isPresent();
    }
}
