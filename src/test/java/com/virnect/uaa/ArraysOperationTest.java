package com.virnect.uaa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ArraysOperationTest {
	@Test
	@DisplayName("Arrays removeAll 테스트")
	void arraysRemoveAll() {
		// given
		List<Integer> score1 = Arrays.asList(1, 2, 3, 4, 5);
		List<Integer> score2 = new ArrayList<>();
		score2.add(1);
		score2.add(2);

		try {
			// when
			score1.removeAll(score2);
		} catch (Exception e) {
			System.out.println("----------------------------------");
			System.out.println("ArraysOperationTest.arraysRemoveAll");
			System.out.println("Exception = " + e);
			// then
			Assertions.assertThat(e).isInstanceOf(UnsupportedOperationException.class);
		}
	}

	@Test
	@DisplayName("Arrays add 테스트")
	void arraysAddTest() {
		// given
		List<Integer> score = Arrays.asList(1, 2, 3, 4, 5);

		try {
			// when
			score.add(6);
		} catch (Exception e) {
			System.out.println("----------------------------------");
			System.out.println("ArraysOperationTest.arraysAddTest");
			System.out.println("Exception = " + e);
			// then
			Assertions.assertThat(e).isInstanceOf(UnsupportedOperationException.class);
		}
	}

	@Test
	@DisplayName("Arrays remove 테스트")
	void arraysRemoveTest() {
		// given
		List<Integer> score = Arrays.asList(1, 2, 3, 4, 5);

		try {
			// when
			score.remove(5);
		} catch (Exception e) {
			System.out.println("----------------------------------");
			System.out.println("ArraysOperationTest.arraysRemoveTest");
			System.out.println("Exception = " + e);
			// then
			Assertions.assertThat(e).isInstanceOf(UnsupportedOperationException.class);
		}
	}
}
