package com.mladenov.projectmanagement.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEnumValidator.class)
public @interface ValidEnum {
    String key();

    Class<? extends Enum<?>> enumClass();

    String message() default "Invalid value. Expected one of: {enumValues}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}