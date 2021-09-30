package com.virnect.uaa.domain.auth.account.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = MobileNumberValidator.class)
public @interface MobileNumberValidate {
	String message() default "Mobile Number Format Is Wrong. Please Check Mobile Phone Number Format.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
