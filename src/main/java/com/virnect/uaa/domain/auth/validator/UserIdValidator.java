package com.virnect.uaa.domain.auth.validator;

import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.internal.constraintvalidators.AbstractEmailValidator;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class UserIdValidator extends AbstractEmailValidator<UserLoginIdValidation> {
	private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
	@Value("${spring.profiles.active:develop}")
	private String profiles;
	private java.util.regex.Pattern pattern;

	@Override
	public void initialize(UserLoginIdValidation userLoginIdValidation) {
		super.initialize(userLoginIdValidation);

		Pattern.Flag[] flags = userLoginIdValidation.flags();
		int intFlag = 0;
		for (Pattern.Flag flag : flags) {
			intFlag = intFlag | flag.getValue();
		}

		// we only apply the regexp if there is one to apply
		if (!".*".equals(userLoginIdValidation.regexp()) || userLoginIdValidation.flags().length > 0) {
			try {
				pattern = java.util.regex.Pattern.compile(userLoginIdValidation.regexp(), intFlag);
			} catch (PatternSyntaxException e) {
				throw LOG.getInvalidRegularExpressionException(e);
			}
		}
	}

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		// if server mode is onpremise
		if (profiles.equals("onpremise")) {
			return true;
		}

		boolean isValid = super.isValid(value, context);
		if (pattern == null || !isValid) {
			return isValid;
		}

		Matcher m = pattern.matcher(value);
		return m.matches();
	}
}
