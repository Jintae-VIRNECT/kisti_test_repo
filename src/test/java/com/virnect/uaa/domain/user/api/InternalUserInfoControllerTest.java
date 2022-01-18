package com.virnect.uaa.domain.user.api;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/users.sql",
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InternalUserInfoControllerTest {
	@Autowired
	public MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("전체 사용자 조회 - 페이징 OK")
	void findAllUserInfoRequestHandler() throws Exception {
		int page = 1;
		int size = 20;

		String url = String.format("/users/all?page=%s&size=%s", page, size);
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.pageMeta").exists())
			.andExpect(jsonPath("$.data.pageMeta.currentPage").value(page))
			.andExpect(jsonPath("$.data.pageMeta.currentSize").value(size))
		;
	}

	@Test
	@DisplayName("전체 사용자 조회 - 페이징 with search OK")
	void findAllUserInfoRequestHandler_with_search() throws Exception {
		int page = 1;
		int size = 20;
		String search = "virnect.com";

		String url = String.format("/users/all?page=%s&size=%s&search=%s", page, size, search);
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.pageMeta").exists())
			.andExpect(jsonPath("$.data.pageMeta.currentPage").value(page))
			.andExpect(jsonPath("$.data.pageMeta.currentSize").value(size))
			.andExpect(jsonPath("$.data.pageMeta.totalElements").value(2))
		;
	}

	@Test
	@DisplayName("user uuid 리스트 기반 회원 정보 조회 - with 검색어 OK ")
	void getUsersInfoList_search_keyword() throws Exception {
		String search = "virnect";
		List<String> userIds = Arrays.asList(
			"4122a7b8e1d31aee921d36298b7e8709", "rtR0MD7SXx4Du", "2JzEzFpeoyFGO", "NEJqNNdPUTxUT", "YtRO0fhoUlYax");

		String url = String.format("/users/list?search=%s", search);
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(userIds))
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.userInfoList").exists())
			.andExpect(jsonPath("$.data.userInfoList").isArray())
		;
	}

	@Test
	@DisplayName("user uuid 리스트 기반 회원 정보 조회 - with 검색어 없음 OK ")
	void getUsersInfoList_no_search_keyword() throws Exception {
		Map<String, Object> payload = new HashMap<>();
		List<String> userIds = Arrays.asList("4122a7b8e1d31aee921d36298b7e8709", "rtR0MD7SXx4Du", "2JzEzFpeoyFGO", "NEJqNNdPUTxUT", "YtRO0fhoUlYax");
		payload.put(
			"workspaceUserIdList",
			Arrays.asList("4122a7b8e1d31aee921d36298b7e8709", "rtR0MD7SXx4Du", "2JzEzFpeoyFGO", "NEJqNNdPUTxUT", "YtRO0fhoUlYax")
		);

		String url = "/users/list";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(userIds))
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.userInfoList").exists())
			.andExpect(jsonPath("$.data.userInfoList").isArray())
		;
	}

	@Test
	@DisplayName("(빌링)user pk로 사용자 정보 조회 - OK")
	void getUserInfoByUserIdFromBillingSystem() throws Exception {
		int userId = 1;
		String url = String.format("/users/billing/%s", userId);

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.email").value("sky456139@virnect.com"));
	}
}