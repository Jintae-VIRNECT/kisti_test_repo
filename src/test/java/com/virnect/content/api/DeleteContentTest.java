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

import lombok.extern.slf4j.Slf4j;

import com.virnect.content.exception.ContentServiceException;

/**
 * @author jiyong.heo
 * @project PF-ContentManagement
 * @email jiyong.heo@virnect.com
 * @description 컨텐츠 삭제 테스트
 * @since 2020.06.29
 */
@SpringBootTest
@ActiveProfiles("test")
@SqlGroup({
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql"),
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
})
@AutoConfigureMockMvc
@Slf4j
public class DeleteContentTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	@Transactional
	public void deleteContent_convertedContents_test() throws Exception {

		RequestBuilder request = delete("/contents")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(
				"{\"contentUUIDs\": [\"4e2cfebd-5b16-4dd6-96a4-f2c93e5e241e\"], \"workspaceUUID\":\"4d6eab0860969a50acbfa4599fbb5ae8\"}");
		//.content("contentDeleteRequest", "{\"contentUUIDs\": [\"72e10229-0be8-4c64-a319-7c7a9612fe23\", \"0d668d4f-7a78-43c0-999d-f0e304acbd14\"], \"workspaceUUID\":\"4d6eab0860969a50acbfa4599fbb5ae8\"}");

		this.mockMvc.perform(request)
			.andDo(print())
			.andExpect(
				result -> assertTrue(result.getResponse().getContentAsString().contains("Content deletion failed.")));
	}

	@Test
	@Transactional
	public void deleteContent_contentNotFound_test() throws Exception {
		RequestBuilder request = delete("/contents")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(
				"{\"contentUUIDs\": [\"72e10229-0be8-4c64-\", \"0d668d4f--999d-f0e304acbd14\"], \"workspaceUUID\":\"4d6eab0860969a50acbfa4599fbb5ae8\"}");

		this.mockMvc.perform(request)
			.andDo(print())
			.andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Content not found.")))
			.andExpect(result -> assertTrue(
				result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
	}

	@Test
	@Transactional
	public void deleteContent_workspace_test() throws Exception {
		RequestBuilder request = delete("/contents")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content("{\"contentUUIDs\": [\"6993fa89-7bff-4414-b186-d8719730f25f\"], \"workspaceUUID\":\"433\"}");

		this.mockMvc.perform(request)
			.andDo(print())
			.andExpect(result -> assertTrue(
				result.getResponse().getContentAsString().contains("Because Workspace is different.")));
	}
}
