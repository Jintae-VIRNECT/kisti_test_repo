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
 * @description 컨텐츠 공유 테스트
 * @since 2020.06.29
 */
@SpringBootTest
@ActiveProfiles("test")
@SqlGroup({
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql"),
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
})
@AutoConfigureMockMvc
public class ContentSharedTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@Transactional
	public void contentShare_InvalidUserUUID_ContentServiceException() throws Exception {
		RequestBuilder request = put("/contents/info/{contentUUID}", "6993fa89-7bff-4414-b186-d8719730f25f")
			.contentType(MediaType.APPLICATION_JSON)
			.content(
				"{\"contentType\":\"AUGMENTED_REALITY\", \"shared\":\"YES\", \"userUUID\":\"4ccfd56eba9a4d46939a1efed99098b8\"}")
			.accept(MediaType.APPLICATION_JSON);

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
	public void contentShare_InvalidContentUUID_ContentServiceException() throws Exception {
		RequestBuilder request = put("/contents/info/{contentUUID}", "0d668d4f-7a78-43c0-999d-f0e304a")
			.contentType(MediaType.APPLICATION_JSON)
			.content(
				"{\"contentType\":\"AUGMENTED_REALITY\", \"shared\":\"YES\", \"userUUID\":\"498b1839dc29ed7bb2ee90ad6985c608\"}")
			.accept(MediaType.APPLICATION_JSON);

		this.mockMvc.perform(request)
			.andDo(print())
			.andExpect(
				result -> assertTrue(result.getResponse().getContentAsString().contains("4003"))) // Content not found.
			.andExpect(result -> assertTrue(
				result.getResolvedException().getClass().isAssignableFrom(ContentServiceException.class)));
	}
}
