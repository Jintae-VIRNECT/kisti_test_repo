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

/**
 * @author jiyong.heo
 * @project PF-ContentManagement
 * @email jiyong.heo@virnect.com
 * @description 컨텐츠 목록 조회 테스트
 * @since 2020.06.29
 */
@SpringBootTest
@ActiveProfiles("test")
@SqlGroup({
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql"),
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
})
@AutoConfigureMockMvc
public class GetContentsTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getContents_InvalidParameter_ContentsException() throws Exception {
		// given
		RequestBuilder request = get("/contents")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.param("converted", "YES")
			.param("workspaceUUID", "");

		// when
		this.mockMvc.perform(request)
			.andDo(print())
			// then
			.andExpect((result -> assertTrue(result.getResponse().getContentAsString().contains("complete"))));
	}
}
