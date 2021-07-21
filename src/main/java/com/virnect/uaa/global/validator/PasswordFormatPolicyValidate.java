package com.virnect.uaa.global.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordFormatPolicyValidator.class)
public @interface PasswordFormatPolicyValidate {
	Class<?>[] groups() default { };
	Class<? extends Payload>[] payload() default { };
	String message() default "Password Format Is Not Valid.";
	boolean emptyIgnore() default false;
}
