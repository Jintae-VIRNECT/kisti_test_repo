package com.virnect.uaa.global.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordFormatPolicyValidator implements ConstraintValidator<PasswordFormatPolicyValidate, String> {
	private static final String ALLOWED_LETTER_CHECK_REGEXP = "[a-zA-Z0-9\\.!@#$%가-힣ㄱ-ㅎ]+";
	private static final String REPEATED_LETTER_CHECK_REGEXP = "(\\w)\\1{3,}";
	private boolean emptyIgnore;

	@Override
	public void initialize(PasswordFormatPolicyValidate annotation) {
		ConstraintValidator.super.initialize(annotation);
		emptyIgnore = annotation.emptyIgnore();
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		if (emptyIgnore && StringUtils.isEmpty(password)) {
			return true;
		}

		// 1. length check
		if (password.length() < 8 || password.length() > 21) {
			log.error("password length not valid. current password length is {}", password.length());
			return false;
		}

		// 2. check possible letter is using
		if (!password.matches(ALLOWED_LETTER_CHECK_REGEXP)) {
			log.info("password is consist of not allow letter. letter check regexp: [{}]", ALLOWED_LETTER_CHECK_REGEXP);
			return false;
		}

		// 3. find 4 time repeat character is using
		if (password.matches(REPEATED_LETTER_CHECK_REGEXP)) {
			log.info(
				"password exist repeated letter at least 4 times. repeated letter check regexp: [{}]",
				REPEATED_LETTER_CHECK_REGEXP
			);
			return false;
		}

		return true;
	}
}
