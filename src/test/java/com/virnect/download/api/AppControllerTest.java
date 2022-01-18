package com.virnect.download.api;

import org.junit.jupiter.api.DisplayName;
import org.slf4j.MDC;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.download.dto.request.AdminAppUploadRequest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("admin app upload")
	void adminAppUpload() throws Exception {
		String url = "/download/app/register/admin";

		MockMultipartFile uploadAppFile = new MockMultipartFile(
			"uploadAppFile", "makeInstaller.exe", "application/octet-stream", "make".getBytes());

		MultiValueMap<String, String> adminAppUploadRequest = new LinkedMultiValueMap<>();
		adminAppUploadRequest.set("deviceModel", "WINDOWS_10");
		adminAppUploadRequest.set("deviceType", "PC");
		adminAppUploadRequest.set("operationSystem", "WINDOWS");
		adminAppUploadRequest.set("productName", "MAKE");
		adminAppUploadRequest.set("versionName", "1.3.3");
		MDC.put("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url)
				.file(uploadAppFile)
				.params(adminAppUploadRequest)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.uuid").exists())
			.andReturn();
	}

	@Test
	@DisplayName("admin app upload with low version")
	void adminAppUpload_lowVersion() throws Exception {
		String url = "/download/app/register/admin";
		MockMultipartFile uploadAppFile = new MockMultipartFile(
			"uploadAppFile", "makeInstaller.exe", "application/octet-stream", "make".getBytes());

		MultiValueMap<String, String> adminAppUploadRequest = new LinkedMultiValueMap<>();
		adminAppUploadRequest.set("deviceModel", "WINDOWS_10");
		adminAppUploadRequest.set("deviceType", "PC");
		adminAppUploadRequest.set("operationSystem", "WINDOWS");
		adminAppUploadRequest.set("productName", "MAKE");
		adminAppUploadRequest.set("versionName", "1.3.1");
		MDC.put("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url)
				.file(uploadAppFile)
				.params(adminAppUploadRequest)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.uuid").exists())
			.andReturn();
	}
}