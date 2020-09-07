package com.virnect.content.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import com.virnect.content.exception.ContentServiceException;

/**
 * @author jiyong.heo
 * @project PF-ContentManagement
 * @email jiyong.heo@virnect.com
 * @description 컨텐츠 상세 정보 조회 테스트
 * @since 2020.06.29
 */
@SpringBootTest
@ActiveProfiles("test")
@SqlGroup({
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql"),
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
})
@AutoConfigureMockMvc
public class ContentTargetAddTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@Transactional
	public void contentTargetAdd_InvalidContentUUID_ContentServiceException() throws Exception {
		RequestBuilder request = post("/contents/target/{contentUUID}", "02a67ae7-a464-4e99-bf15-ddc948")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.param("targetData", "0jXPVGTgaHBUXHFoTJwi0bLcK7XxmdrCXp0%2ft9pkT%2bQ%3d")
			.param("targetType", "QR")
			.param("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");

		this.mockMvc.perform(request)
			.andDo(print())
			.andExpect(result -> assertTrue(
				result.getResponse().getContentAsString().contains("4004"))) // Content Update fail.
			.andExpect(result -> assertTrue(
				result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
	}

	@Test
	@Transactional
	public void contentTargetAdd_ContentState_ContentServiceException() throws Exception {
		RequestBuilder request = post("/contents/target/{contentUUID}", "4e2cfebd-5b16-4dd6-96a4-f2c93e5e241e")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.param("targetData", "0jXPVGTgaHBUXHFoTJwi0bLcK7XxmdrCXp0%2ft9pkT%2bQ%3d")
			.param("targetType", "QR")
			.param("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");

		this.mockMvc.perform(request)
			.andDo(print())
			.andExpect(result -> assertTrue(result.getResponse()
				.getContentAsString()
				.contains(
					"4009"))) // Content deletion failed. Because it is managed. Delete the process created with this content and try again.
			.andExpect(result -> assertTrue(
				result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
	}

	@Test
	@Transactional
	public void contentTargetAdd_InvalidUserUUID_ContentServiceException() throws Exception {
		RequestBuilder request = post("/contents/target/{contentUUID}", "4e2cfebd-5b16-4dd6-96a4-f2c93e5e241e")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.param("targetData", "0jXPVGTgaHBUXHFoTJwi0bLcK7XxmdrCXp0%2ft9pkT%2bQ%3d")
			.param("targetType", "QR")
			.param("userUUID", "498b1839dc29ed7bb2ee90ad6");

		this.mockMvc.perform(request)
			.andDo(print())
			.andExpect(result -> assertTrue(result.getResponse()
				.getContentAsString()
				.contains("4015"))) // An error occurred in the request. Because it is NOT ownership.
			.andExpect(result -> assertTrue(
				result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
	}

	@Test
	@Transactional
	public void contentTargetAdd_DuplicateTargetData_ContentServiceException() throws Exception {
		RequestBuilder request = post("/contents/target/{contentUUID}", "6993fa89-7bff-4414-b186-d8719730f25f")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.param("targetData", "testTargetData")
			.param("targetType", "QR")
			.param("userUUID", "498b1839dc29ed7bb2ee90ad6985c608");

		this.mockMvc.perform(request)
			.andDo(print())
			.andExpect(result -> assertTrue(result.getResponse()
				.getContentAsString()
				.contains("4101"))) // Target insert fail. Because this target data already exist.
			.andExpect(result -> assertTrue(
				result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
	}
}
