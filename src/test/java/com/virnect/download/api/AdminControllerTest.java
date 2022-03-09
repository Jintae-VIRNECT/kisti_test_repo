package com.virnect.download.api;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.ByteArrayApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import net.dongliu.apk.parser.struct.AndroidConstants;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.converters.Auto;

import com.virnect.download.application.download.ApkService;
import com.virnect.download.application.download.AppService;
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
	@MockBean
	ApkService apkService;
	@Mock
	ApkMeta apkMeta;

	@Test
	@DisplayName("admin app upload")
	void uploadApplication() throws Exception {
		String url = "/download/app/register/admin";

		MockMultipartFile uploadAppFile = new MockMultipartFile(
			"uploadAppFile", "make_pc_1030202.exe", "application/x-msdownload", "make".getBytes());

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
			.andExpect(jsonPath("$.data.version").value("1.3.2.2"))
			.andExpect(jsonPath("$.data.versionCode").value(1030202))
			.andReturn();
	}

	@Test
	@DisplayName("admin app upload with invalid file name")
	void uploadApplication_emptyVersion() throws Exception {
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
	void uploadApplication_lowerVersion() throws Exception {
		String url = "/download/app/register/admin";
		MockMultipartFile uploadAppFile = new MockMultipartFile(
			"uploadAppFile", "make_pc_1030200.exe", "application/octet-stream", "make".getBytes());

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
	void uploadApplication_invalidMimeType() throws Exception {
		String url = "/download/app/register/admin";
		MockMultipartFile uploadAppFile = new MockMultipartFile(
			"uploadAppFile", "make_pc_1302002.exe", "application/json", "make".getBytes());

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
	void uploadApplication_remoteApk() throws Exception {
		String url = "/download/app/register/admin";

		MockMultipartFile uploadAppFile = new MockMultipartFile(
			"uploadAppFile", "remote_mobile_2700000.apk", "application/vnd.android.package-archive",
			"remote".getBytes()
		);
		when(apkService.parsingAppInfo(uploadAppFile)).thenReturn(apkMeta);
		when(apkMeta.getPackageName()).thenReturn("com.virnect.remote.mobile2");

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
			.andExpect(jsonPath("$.data.version").value("2.7.0"))
			.andExpect(jsonPath("$.data.versionCode").value(2700000))
			.andReturn();
	}

	@Test
	@DisplayName("admin app upload with remote hololens app")
	void uploadApplication_remoteHololens() throws Exception {
		String url = "/download/app/register/admin";

		MockMultipartFile uploadAppFile = new MockMultipartFile(
			"uploadAppFile", "remote_hololens_2703.appx", "application/octet-stream",
			"remote".getBytes()
		);

		MultiValueMap<String, String> adminAppUploadRequest = new LinkedMultiValueMap<>();
		adminAppUploadRequest.set("deviceModel", "HOLOLENS_2");
		adminAppUploadRequest.set("deviceType", "HOLOLENS");
		adminAppUploadRequest.set("operationSystem", "WINDOWS_UWP");
		adminAppUploadRequest.set("productName", "REMOTE");
		MDC.put("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url)
				.file(uploadAppFile)
				.params(adminAppUploadRequest)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.version").value("2.7.0.3"))
			.andExpect(jsonPath("$.data.versionCode").value(2703))
			.andReturn();
	}

	@Test
	@DisplayName("admin app delete")
	void deleteApplication() throws Exception {
		String appUUID = "5ca2-5bf164fc3c76";
		String url = String.format("/download/app/%s/admin", appUUID);

		MDC.put("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");

		mockMvc.perform(MockMvcRequestBuilders
				.delete(url)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andReturn();
	}
}