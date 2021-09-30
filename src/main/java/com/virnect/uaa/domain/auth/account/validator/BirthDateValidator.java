package com.virnect.uaa.domain.auth.account.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BirthDateValidator implements ConstraintValidator<BirthDateValidate, String> {
	private static final String BIRTH_DATE_FORMAT_REGEXP = "^\\d{4}-\\d{2}-\\d{2}$";

	@Override
	public void initialize(BirthDateValidate constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(String birthDate, ConstraintValidatorContext context) {

		if (!birthDate.matches(BIRTH_DATE_FORMAT_REGEXP)) {
			log.error(
				"Birth Date format invalid. current birth date: [{}], format: [{}}]", birthDate,
				BIRTH_DATE_FORMAT_REGEXP
			);
			return false;
		}

		String[] dateArray = birthDate.split("-");
		int month = Integer.parseInt(dateArray[1]);
		int day = Integer.parseInt(dateArray[2]);

		return (month >= 1 && month <= 12) && (day >= 1 && day <= 31);
	}
}
