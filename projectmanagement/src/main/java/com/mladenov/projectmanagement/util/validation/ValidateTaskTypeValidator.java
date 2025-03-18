package com.mladenov.projectmanagement.util.validation;

import com.mladenov.projectmanagement.model.enums.TaskType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class ValidateTaskTypeValidator implements ConstraintValidator<ValidateTaskType, TaskType> {
    private TaskType[] anyOf;

    @Override
    public void initialize(ValidateTaskType constraintAnnotation) {
        this.anyOf = constraintAnnotation.anyOf();
    }

    @Override
    public boolean isValid(TaskType value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(anyOf).contains(value);
    }
}
