package com.virnect.auth.validator;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BirthDateValidatorTest {

	@Test
	@DisplayName("생년월일 어노테이션 테스트")
	public void birtDateTest() {
		String[] successBirth = {
			"1996-02-01",
			"2910-02-28",
			"2021-04-31",
			"2021-04-30",
			"2021-04-01",
			"2021-01-12",
			"2021-12-31",
			"2021-01-01",
		};

		String[] failBirth = {
			"1996-32-01",
			"2910-32-28",
			"2021-01-33",
			"2021-13-31",
			"2021-64-01",
			"2021-11-00",
			"2021-00-00",
			"",
			"01-01",
		};

		for (String birth : successBirth) {
			assertThat(validation(birth)).isTrue();
			System.out.println("success birth = " + birth + " validation result = " + validation(birth));
		}

		for (String birth : failBirth) {
			assertThat(validation(birth)).isFalse();
			System.out.println("fail birth = " + birth + " validation result = " + validation(birth));
		}

	}

	public boolean validation(String birthDate) {
		if (!birthDate.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
			return false;
		}

		// number range check

		String[] dateArray = birthDate.split("-");
		int month = Integer.parseInt(dateArray[1]);
		int day = Integer.parseInt(dateArray[2]);

		return (month >= 1 && month <= 12) && (day >= 1 && day <= 31);
	}
}
