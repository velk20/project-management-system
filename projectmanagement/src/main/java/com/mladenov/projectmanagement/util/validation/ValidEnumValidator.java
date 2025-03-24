package com.mladenov.projectmanagement.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ValidEnumValidator implements ConstraintValidator<ValidEnum, String> {
    private String key;
    private String message;
    private Enum<?>[] enumValues;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        key = constraintAnnotation.key();
        enumValues = constraintAnnotation.enumClass().getEnumConstants();
        message = "Invalid value for " + key + ". Expected one of: " + Arrays.stream(enumValues)
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null cases
        }
        boolean isValid = Arrays.stream(enumValues)
                .map(Enum::name)
                .anyMatch(enumValue -> enumValue.equalsIgnoreCase(value));
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }
        return isValid;
    }
}
