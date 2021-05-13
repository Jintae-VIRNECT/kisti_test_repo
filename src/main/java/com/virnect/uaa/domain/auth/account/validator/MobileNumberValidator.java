package com.virnect.uaa.domain.auth.account.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MobileNumberValidator implements ConstraintValidator<MobileNumberValidate, String> {
	@Override
	public void initialize(MobileNumberValidate constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(String mobileNumber, ConstraintValidatorContext context) {
		// empty ignore
		if (mobileNumber == null || mobileNumber.isEmpty()) {
			return true;
		}
		return mobileNumber.matches("^\\+\\d+-\\d{10,}$");
	}
}
