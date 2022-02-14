package com.virnect.download.api;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.download.global.error.ErrorCode;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("admin app upload")
	void adminApkAppUploadRequestHandler() throws Exception {
		String url = "/download/app/register/admin";

		MockMultipartFile uploadAppFile = new MockMultipartFile(
			"uploadAppFile", "MAKE_PC_1302002.exe", "application/x-msdownload", "make".getBytes());

		MultiValueMap<String, String> adminAppUploadRequest = new LinkedMultiValueMap<>();
		adminAppUploadRequest.set("deviceModel", "WINDOWS_10");
		adminAppUploadRequest.set("deviceType", "PC");
		adminAppUploadRequest.set("operationSystem", "WINDOWS");
		adminAppUploadRequest.set("productName", "MAKE");
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
	@DisplayName("admin app upload with invalid file name")
	void adminApkAppUploadRequestHandler_emptyVersion() throws Exception {
		String url = "/download/app/register/admin";
		MockMultipartFile uploadAppFile = new MockMultipartFile(
			"uploadAppFile", "makeInstaller.exe", "application/octet-stream", "make".getBytes());

		MultiValueMap<String, String> adminAppUploadRequest = new LinkedMultiValueMap<>();
		adminAppUploadRequest.set("deviceModel", "WINDOWS_10");
		adminAppUploadRequest.set("deviceType", "PC");
		adminAppUploadRequest.set("operationSystem", "WINDOWS");
		adminAppUploadRequest.set("productName", "MAKE");
		MDC.put("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url)
				.file(uploadAppFile)
				.params(adminAppUploadRequest)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(ErrorCode.ERR_APP_UPLOAD_INVALID_FILE_NAME.getCode()))
			.andReturn();
	}

	@Test
	@DisplayName("admin app upload with lower version")
	void adminApkAppUploadRequestHandler_lowerVersion() throws Exception {
		String url = "/download/app/register/admin";
		MockMultipartFile uploadAppFile = new MockMultipartFile(
			"uploadAppFile", "MAKE_PC_1302000.exe", "application/octet-stream", "make".getBytes());

		MultiValueMap<String, String> adminAppUploadRequest = new LinkedMultiValueMap<>();
		adminAppUploadRequest.set("deviceModel", "WINDOWS_10");
		adminAppUploadRequest.set("deviceType", "PC");
		adminAppUploadRequest.set("operationSystem", "WINDOWS");
		adminAppUploadRequest.set("productName", "MAKE");
		MDC.put("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url)
				.file(uploadAppFile)
				.params(adminAppUploadRequest)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(ErrorCode.ERR_APP_UPLOAD_FAIL_VERSION_IS_LOWER.getCode()))
			.andReturn();
	}

	@Test
	@DisplayName("admin app upload with invalid file mime type")
	void adminApkAppUploadRequestHandler_invalidMimeType() throws Exception {
		String url = "/download/app/register/admin";
		MockMultipartFile uploadAppFile = new MockMultipartFile(
			"uploadAppFile", "MAKE_PC_1302002.exe", "application/json", "make".getBytes());

		MultiValueMap<String, String> adminAppUploadRequest = new LinkedMultiValueMap<>();
		adminAppUploadRequest.set("deviceModel", "WINDOWS_10");
		adminAppUploadRequest.set("deviceType", "PC");
		adminAppUploadRequest.set("operationSystem", "WINDOWS");
		adminAppUploadRequest.set("productName", "MAKE");
		MDC.put("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url)
				.file(uploadAppFile)
				.params(adminAppUploadRequest)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(ErrorCode.ERR_APP_UPLOAD_FILE_EXTENSION_NOT_SUPPORT.getCode()))
			.andReturn();
	}

	@Test
	@DisplayName("admin app upload with remote apk")
	void adminApkAppUploadRequestHandler_remoteApk() throws Exception {
		String url = "/download/app/register/admin";

		MockMultipartFile uploadAppFile = new MockMultipartFile(
			"uploadAppFile", "REMOTE_MOBILE_2700000.apk", "application/vnd.android.package-archive",
			"remote".getBytes()
		);

		MultiValueMap<String, String> adminAppUploadRequest = new LinkedMultiValueMap<>();
		adminAppUploadRequest.set("deviceModel", "SMARTPHONE_TABLET");
		adminAppUploadRequest.set("deviceType", "MOBILE");
		adminAppUploadRequest.set("operationSystem", "ANDROID");
		adminAppUploadRequest.set("productName", "REMOTE");
		MDC.put("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url)
				.file(uploadAppFile)
				.params(adminAppUploadRequest)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(ErrorCode.ERR_APP_UPLOAD_FAIL.getCode()))
			.andReturn();
	}
}