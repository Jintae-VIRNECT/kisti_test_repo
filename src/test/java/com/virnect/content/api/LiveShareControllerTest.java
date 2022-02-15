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
@Import({TestRabbitmqConfiguration.class})
class LiveShareControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("join live share room")
	void joinLiveShareRoom() throws Exception {
		String contentUUID = "1016b7fe-88b2-497f-a04c-98be8fbcccce";
		String userUUID = "nnTzbBEvPMZpt";
		String url = String.format("/contents/%s/liveShare?userUUID=%s", contentUUID, userUUID);

		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andReturn();
	}

	@Test
	@DisplayName("join live share room initialize room")
	void joinLiveShareRoom_initializeRoom() throws Exception {
		String contentUUID = "557aec82-18ec-464d-8723-5b699a1910fd";
		String userUUID = "nnTzbBEvPMZpt";
		String url = String.format("/contents/%s/liveShare?userUUID=%s", contentUUID, userUUID);

		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andReturn();
	}
	@Test
	@DisplayName("join live share room by invalid workspace user")
	void joinLiveShareRoom_invalidWorkspaceUser() throws Exception {
		String contentUUID = "1016b7fe-88b2-497f-a04c-98be8fbcccce";
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

	@Test
	@DisplayName("join live share room duplicate user")
	void joinLiveShareRoom_DuplicateUser() throws Exception {
		String contentUUID = "1016b7fe-88b2-497f-a04c-98be8fbcccce";
		String userUUID = "498b1839dc29ed7bb2ee90ad6985c608";
		String url = String.format("/contents/%s/liveShare?userUUID=%s", contentUUID, userUUID);

		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(4033))
			.andReturn();
	}

	@Test
	@DisplayName("leave live share room")
	void leaveLiveShareRoom() throws Exception {
		String contentUUID = "1016b7fe-88b2-497f-a04c-98be8fbcccce";
		String userUUID = "ouXLjBs6Rmd95";
		long roomId = 3;
		String url = String.format("/contents/%s/liveShare/room/%s?userUUID=%s", contentUUID, roomId, userUUID);

		mockMvc.perform(
				MockMvcRequestBuilders
					.delete(url)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.result").value(true))
			.andReturn();
	}
	@Test
	@DisplayName("leave live share room inactive room")
	void leaveLiveShareRoom_inactiveRoom() throws Exception {
		String contentUUID = "557aec82-18ec-464d-8723-5b699a1910fd";
		String userUUID = "ouXLjBs6Rmd95";
		long roomId = 4;
		String url = String.format("/contents/%s/liveShare/room/%s?userUUID=%s", contentUUID, roomId, userUUID);

		mockMvc.perform(
				MockMvcRequestBuilders
					.delete(url)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.result").value(true))
			.andReturn();
	}
}