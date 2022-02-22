package com.virnect.uaa.domain.user.api;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.MethodOrderer;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.uaa.domain.user.dto.request.UserSecessionRequest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(
	scripts = {"classpath:data/users.sql"}
)
class UserInfoUpdateControllerTest {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void userSecessionRequest() throws Exception {
		String url = "/users/secession";

		UserSecessionRequest userSecessionRequest = new UserSecessionRequest();
		userSecessionRequest.setEmail("smic1");
		userSecessionRequest.setPassword("smic1234");
		userSecessionRequest.setPolicyAssigned(true);
		userSecessionRequest.setReason("test");
		userSecessionRequest.setUuid("498b1839dc29ed7bb2ee90ad6985c608");

		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(userSecessionRequest))
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.email").exists())
			.andExpect(jsonPath("$.data.email").value(userSecessionRequest.getEmail()))
			.andReturn();
	}
}