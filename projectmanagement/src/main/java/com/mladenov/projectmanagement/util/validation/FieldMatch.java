package com.mladenov.projectmanagement.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = FieldMatchValidator.class)
public @interface FieldMatch  {
    String first();

    String second();
    String message() default "Invalid Field";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
