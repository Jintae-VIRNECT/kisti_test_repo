package com.virnect.uaa.domain.user.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.dto.request.MemberRegistrationRequest;
import com.virnect.uaa.domain.user.mapper.UserInfoMapper;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("local")
class MemberUserInformationServiceTest {
	@Autowired
	UserRepository userRepository;
	@Autowired
	MemberUserInformationService memberUserInformationService;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	UserInfoMapper userInfoMapper;

	@Test
	@Transactional
	void registerNewMember() {
		User masterUser = userRepository.findByEmail("sky4561393@virnect.com")
			.orElseThrow(() -> new RuntimeException("마스터 사용자 못찾음"));

		MemberRegistrationRequest memberRegistrationRequest = new MemberRegistrationRequest();
		memberRegistrationRequest.setMasterUUID(masterUser.getUuid());
		memberRegistrationRequest.setEmail("LoveAndCode2");
		memberRegistrationRequest.setPassword("123456");

		User workspaceOnlyUser = User.ByRegisterMemberUserBuilder()
			.masterUser(masterUser)
			.memberRegistrationRequest(memberRegistrationRequest)
			.encodedPassword(passwordEncoder.encode(memberRegistrationRequest.getPassword()))
			.build();

		userRepository.save(workspaceOnlyUser);

		assertThat(workspaceOnlyUser.getMaster()).isEqualTo(masterUser);

		System.out.println("----------------------------");
		System.out.println(userInfoMapper.toUserInfoResponse(masterUser));
		System.out.println("----------------------------");
		System.out.println(userInfoMapper.toUserInfoResponse(workspaceOnlyUser));
		System.out.println("+++++++++++++++++++++++++++++");
		System.out.println(userInfoMapper.toUserInfoResponse(workspaceOnlyUser));
		System.out.println(userInfoMapper.toUserInfoResponse(workspaceOnlyUser.getMaster()));
		System.out.println("+++++++++++++++++++++++++++++");
		masterUser.getSeatUsers().forEach(user -> System.out.println(userInfoMapper.toUserInfoResponse(user)));
		System.out.println("+++++++++++++++++++++++++++++");
	}
}