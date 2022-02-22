package com.virnect.uaa.domain.user.api;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.uaa.domain.user.dto.request.GuestMemberDeleteRequest;
import com.virnect.uaa.domain.user.dto.request.GuestMemberRegistrationRequest;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;
import com.virnect.uaa.global.common.ApiResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(
	scripts = {"classpath:data/users.sql"}
)
class MemberUserInfoControllerTest {
	private static final String MASTER_USER_UUID = "498b1839dc29ed7bb2ee90ad6985c608";
	private static final String WORKSPACE_UUID = "asdjalsd";
	private static final String GUEST_USER1_UUID = "857828ed3143481dba4abfdbb382cd75";
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	UserInfoResponse guestUser;

	@Test
	@Order(1)
	@DisplayName("POST /users/members/guest OK")
	void registerGuestMember() throws Exception {
		// given
		GuestMemberRegistrationRequest registrationRequest = new GuestMemberRegistrationRequest();
		registrationRequest.setMasterUserUUID(MASTER_USER_UUID);
		registrationRequest.setWorkspaceUUID(WORKSPACE_UUID);

		//when
		String url = "/users/members/guest";
		MvcResult guestUser = mockMvc.perform(MockMvcRequestBuilders
				.post(url)
				.header("serviceID", "workspace-server")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registrationRequest))
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.nickname").value("GuestUser-3"))
			.andReturn();

		this.guestUser = objectMapper.readValue(
			guestUser.getResponse().getContentAsString(), new TypeReference<ApiResponse<UserInfoResponse>>() {
			}).getData();
	}

	@Test
	@Order(2)
	@DisplayName("DELETE /users/members/guest OK")
	void deleteGuestMember() throws Exception {
		// given
		GuestMemberDeleteRequest deleteRequest = new GuestMemberDeleteRequest();
		deleteRequest.setMasterUUID(MASTER_USER_UUID);
		deleteRequest.setGuestUserUUID(GUEST_USER1_UUID);

		String url = "/users/members/guest";
		mockMvc.perform(MockMvcRequestBuilders
				.delete(url)
				.header("serviceID", "workspace-server")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(deleteRequest))
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.userUUID").exists())
			.andExpect(jsonPath("$.data.userUUID").value(GUEST_USER1_UUID))
			.andReturn();
	}

	@Test
	@Order(3)
	@DisplayName("Guest User Nickname Sequence Check")
	void guestUserNicknameSequenceCheck() throws Exception {
		// given
		GuestMemberDeleteRequest deleteRequest = new GuestMemberDeleteRequest();
		deleteRequest.setGuestUserUUID(GUEST_USER1_UUID);
		deleteRequest.setMasterUUID(MASTER_USER_UUID);

		String deleteUrl = "/users/members/guest";

		// when delete guest user 1
		mockMvc.perform(MockMvcRequestBuilders
				.delete(deleteUrl)
				.header("serviceID", "workspace-server")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(deleteRequest))
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.userUUID").exists())
			.andExpect(jsonPath("$.data.userUUID").value(GUEST_USER1_UUID))
			.andReturn();

		// given
		GuestMemberRegistrationRequest registrationRequest = new GuestMemberRegistrationRequest();
		registrationRequest.setMasterUserUUID(MASTER_USER_UUID);
		registrationRequest.setWorkspaceUUID(WORKSPACE_UUID);

		String registerUrl = "/users/members/guest";

		// when
		mockMvc.perform(MockMvcRequestBuilders
				.post(registerUrl)
				.header("serviceID", "workspace-server")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registrationRequest))
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.nickname").value("GuestUser-1"))
			.andReturn();
	}
}