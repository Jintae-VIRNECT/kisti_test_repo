package com.virnect.content.api;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(EmbeddedRabbitmqConfiguration.class)
class LiveShareControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("join live share room")
	void joinLiveShareRoom() throws Exception {
		String contentUUID = "3ac931f7-5b3b-4807-ac6e-61ae5d138204";
		String userUUID = "4a65aa94523efe5391b0541bbbcf97a3";
		String url = String.format("/contents/%s/liveShare?userUUID=%s", contentUUID, userUUID);

		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("join live share room by invalid workspace user")
	void joinLiveShareRoom_invalidWorkspaceUser() throws Exception {
		String contentUUID = "3ac931f7-5b3b-4807-ac6e-61ae5d138204";
		String userUUID = "bbbb";
		String url = String.format(
			"/contents/%s/liveShare?userUUID=%s", contentUUID, userUUID);

		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(9999))
			.andReturn();
	}

}