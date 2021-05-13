package com.virnect.uaa.global.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;

@Target({ElementType.FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordFormatPolicyValidator.class)
public @interface PasswordFormatPolicyValidate {
}
