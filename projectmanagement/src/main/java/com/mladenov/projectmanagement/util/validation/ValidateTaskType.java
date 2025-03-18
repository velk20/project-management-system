package com.mladenov.projectmanagement.util.validation;

import com.mladenov.projectmanagement.model.enums.TaskType;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = ValidateTaskTypeValidator.class)
public @interface ValidateTaskType {
    TaskType[] anyOf();

    String message() default "Invalid Ticket Type, must be any of {anyOf}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}