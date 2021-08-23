package com.virnect.content.global.common;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-20
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Documented
@Constraint(validatedBy = PropertyStringValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyValidated {
	String message() default "{com.virnect.content.global.common.PropertyValidated.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
