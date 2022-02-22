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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContentControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("get contents list by multiple TargetType")
	void getContentListByMultipleTargetType() throws Exception {
		String targetType1 = "VTarget";
		String targetType2 = "Image";
		String workspaceUUID = "4560ff80baf346af946f48b037f5af6b";
		String url = "/contents?target=" + targetType1 + "&target=" + targetType2 + "&workspaceUUID=" + workspaceUUID;

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.contentInfo").exists())
			.andExpect(jsonPath("$.data.contentInfo[*].targets[*].type").value("VTarget"))
			.andReturn();
	}


	@Test
	@DisplayName("get contents list by all TargetType")
	void getContentListByAllTargetType() throws Exception {
		String workspaceUUID = "4560ff80baf346af946f48b037f5af6b";
		String url = "/contents?target=ALL&workspaceUUID=" + workspaceUUID;

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.contentInfo").exists())
			.andReturn();
	}

}