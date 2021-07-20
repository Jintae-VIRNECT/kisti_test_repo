package com.virnect.uaa.mapper;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.virnect.uaa.domain.user.dao.useraccesslog.UserAccessLogRepository;
import com.virnect.uaa.domain.user.domain.UserAccessLog;
import com.virnect.uaa.domain.user.dto.response.UserAccessDeviceInfoResponse;
import com.virnect.uaa.domain.user.mapper.UserAccessDeviceInfoMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("local")
@SpringBootTest
public class UserAccessDeviceInfoMapperTest {
	@Autowired
	private UserAccessLogRepository userAccessLogRepository;

	@Autowired
	private UserAccessDeviceInfoMapper userAccessDeviceInfoMapper;

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
}
