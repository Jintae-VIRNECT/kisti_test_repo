package com.virnect.uaa.mapper;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.dao.useraccesslog.UserAccessLogRepository;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.domain.UserAccessLog;
import com.virnect.uaa.domain.user.dto.response.UserAccessDeviceInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;
import com.virnect.uaa.domain.user.mapper.UserAccessDeviceInfoMapper;
import com.virnect.uaa.domain.user.mapper.UserInfoMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import(TestGsonConfiguration.class)
@SpringBootTest
public class UserAccessDeviceInfoMapperTest {
	@Autowired
	private UserAccessLogRepository userAccessLogRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserAccessDeviceInfoMapper userAccessDeviceInfoMapper;

	@Autowired
	private UserInfoMapper userInfoMapper;

	@Autowired
	private Gson gson;

	@Test
	@DisplayName("User Access Log Information Mapper Test")
	public void userAccessLogInfoMapperTest() {
		// given
		List<UserAccessLog> userAccessLogs = userAccessLogRepository.findAll();

		// when
		List<UserAccessDeviceInfoResponse> userAccessDeviceInfos = userAccessLogs.stream()
			.map(userAccessDeviceInfoMapper::ofUserAccessLog)
			.collect(Collectors.toList());

		for (UserAccessDeviceInfoResponse deviceInfo : userAccessDeviceInfos) {
			System.out.println("deviceInfo = " + deviceInfo);
			assertThat(deviceInfo).isNotNull();
			assertThat(deviceInfo.getDevice()).isNotNull();
			assertThat(deviceInfo.getDevice()).isNotBlank();
		}
	}

	@Test
	@DisplayName("User Mapper Null Value Test")
	public void userInfoMapperNullValueTest() {
		User user = userRepository.findByEmail("sky456139@virnect.com").orElseThrow(() -> new RuntimeException("와 못찾았따!"));
		UserInfoResponse userInfoResponse = userInfoMapper.toUserInfoResponse(user);
		System.out.println("userInfoResponse = " + userInfoResponse);

		assertThat(userInfoResponse.getMobile()).isNotNull();
		assertThat(userInfoResponse.getNickname()).isNotNull();
		assertThat(userInfoResponse.getRecoveryEmail()).isNotNull();
		assertThat(userInfoResponse.getDescription()).isNotNull();
		assertThat(userInfoResponse.getQuestion()).isNotNull();
		assertThat(userInfoResponse.getAnswer()).isNotNull();

		System.out.println(System.identityHashCode(gson));

		System.out.println(gson.toJson(userInfoResponse));
	}
}
