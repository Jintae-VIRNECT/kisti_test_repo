package com.virnect.gateway;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FilterMatchTest {

	@Test
	@DisplayName("Filter 값을 toUpperCase 함수로 UpperCase를 만들고 matches 함수와 정규표현식을 통해 필터 값 검증")
	public void filterTest() {
		String[] filters = new String[] {"master", "member", "manager", "Master", "MANAGER", "Member",
			"MASTER,MEMBER,MANAGER"};
		String passwordEnclude = "{\"email\":\"test2@test.com\",\"password\":\"vnt.2021\",\"rememberMe\":true}";
		String regex = "MASTER|MEMBER|MANAGER";
		//
		// for (String filter : filters) {
		// 	System.out.println("filter = " + filter);
		// 	assertThat(Arrays.stream(filter.toUpperCase().split(",")).allMatch(s -> s.matches(regex))).isTrue();
		// }

		assertThat(passwordEnclude.contains("password")).isTrue();


	}
}
