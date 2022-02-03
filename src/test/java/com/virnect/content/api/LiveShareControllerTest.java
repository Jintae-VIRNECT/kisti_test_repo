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
class LiveShareControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("join live share room")
	void joinLiveShareRoom() throws Exception {
		String contentUUID = "3ac931f7-5b3b-4807-ac6e-61ae5d138204";
		String workspaceUUID = "4d6eab0860969a50acbfa4599fbb5ae8";
		String userUUID = "498b1839dc29ed7bb2ee90ad6985c608";
		String url = String.format(
			"/contents/%s/liveShare?workspaceUUID=%s&userUUID=%s", contentUUID, workspaceUUID, userUUID);

		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn();
	}
}