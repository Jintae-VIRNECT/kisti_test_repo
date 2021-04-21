package com.virnect.uaa.domain.user.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = UserIdValidator.class)
@Repeatable(UserLoginIdValidation.List.class)
public @interface UserLoginIdValidation {
	String message() default "Please provide a valid user login id";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	/**
	 * @return an additional regular expression the annotated element must match. The default
	 * is any string ('.*')
	 */
	String regexp() default ".*";

	/**
	 * @return used in combination with {@link #regexp()} in order to specify a regular
	 * expression option
	 */
	Pattern.Flag[] flags() default {};

	/**
	 * Defines several {@code @Email} constraints on the same element.
	 *
	 * @see UserLoginIdValidation
	 */
	@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
	@Retention(RUNTIME)
	@Documented
	@interface List {
		UserLoginIdValidation[] value();
	}
}
