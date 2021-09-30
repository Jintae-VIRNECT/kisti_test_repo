package com.virnect.uaa.service;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.virnect.uaa.domain.user.application.InternalUserInformationServiceImpl;
import com.virnect.uaa.domain.user.dto.response.UserInfoListResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public class InternalUserInformationServiceTest {
	@Autowired
	private InternalUserInformationServiceImpl internalUserInformationService;

	@Test
	@DisplayName("internalUserInformationServiceImpl Test")
	void test() {
		List<String> userUUIDList = Arrays.asList(
			"2HhRIGWBF93qA", "haL4NHlQJ8SU7", "wsigZkkAK5blx","5m3EsEVyQ05sw", "Si0JileQIYUO5"
		);

		UserInfoListResponse usersInfoList = internalUserInformationService.getUsersInfoList(null, userUUIDList);
		assertThat(usersInfoList).isNotNull();
		assertThat(usersInfoList.getUserInfoList().size()).isEqualTo(5);

		for (UserInfoResponse userInfoResponse : usersInfoList.getUserInfoList()) {
			assertThat(userInfoResponse).isNotNull();
			System.out.println("userInfoResponse = " + userInfoResponse);
		}

	}

	@Test
	@DisplayName("ArrayList removeAll Test")
	void test2() {
		List<String> source = new ArrayList<>(Arrays.asList(
			"ozvkSJotvF17h", "4163b24f04b699efb817fa2df19245ss", "yYqW1ZujSTDGh", "yzUesA6kR2xbx", "zlQeYRg8ty1cY"
		));
		List<String> target = new ArrayList<>(Arrays.asList(
			"4163b24f04b699efb817fa2df19245ss", "yYqW1ZujSTDGh", "yzUesA6kR2xbx", "zlQeYRg8ty1cY"
		));

		source.removeAll(target);
		System.out.println("source = " + source);
	}
}
