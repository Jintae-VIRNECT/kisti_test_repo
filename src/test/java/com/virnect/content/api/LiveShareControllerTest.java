package com.virnect.content.api;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import com.virnect.content.dto.request.LiveShareRoomLeaveRequest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({TestRabbitmqConfiguration.class})
class LiveShareControllerTest {
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;
	@Value("${security.jwt-config.secret}")
	private String secret;

	@Test
	@DisplayName("join live share room")
	void joinLiveShareRoom() throws Exception {
		String contentUUID = "1016b7fe-88b2-497f-a04c-98be8fbcccce";
		String userUUID = "nnTzbBEvPMZpt";
		String url = String.format("/contents/%s/liveShare", contentUUID);

		mockMvc.perform(MockMvcRequestBuilders.post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + createAccessToken(userUUID)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andReturn();
	}

	@Test
	@DisplayName("join live share room initialize room")
	void joinLiveShareRoom_initializeRoom() throws Exception {
		String contentUUID = "557aec82-18ec-464d-8723-5b699a1910fd";
		String userUUID = "nnTzbBEvPMZpt";
		String url = String.format("/contents/%s/liveShare", contentUUID);

		mockMvc.perform(MockMvcRequestBuilders.post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + createAccessToken(userUUID)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andReturn();
	}

	@Test
	@DisplayName("join live share room by invalid workspace user")
	void joinLiveShareRoom_invalidWorkspaceUser() throws Exception {
		String contentUUID = "1016b7fe-88b2-497f-a04c-98be8fbcccce";
		String userUUID = "bbbb";
		String url = String.format("/contents/%s/liveShare", contentUUID);

		mockMvc.perform(MockMvcRequestBuilders.post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + createAccessToken(userUUID)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(9999))
			.andReturn();
	}

	@Test
	@DisplayName("join live share room duplicate user")
	void joinLiveShareRoom_DuplicateUser() throws Exception {
		String contentUUID = "1016b7fe-88b2-497f-a04c-98be8fbcccce";
		String userUUID = "498b1839dc29ed7bb2ee90ad6985c608";
		String url = String.format("/contents/%s/liveShare", contentUUID);

		mockMvc.perform(MockMvcRequestBuilders.post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + createAccessToken(userUUID)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(4033))
			.andReturn();
	}

	@Test
	@DisplayName("leave live share room")
	void leaveLiveShareRoom() throws Exception {
		String contentUUID = "1016b7fe-88b2-497f-a04c-98be8fbcccce";
		String userUUID = "ouXLjBs6Rmd95";
		long roomId = 3;
		String url = String.format("/contents/%s/liveShare/rooms/%s/leave", contentUUID, roomId);

		mockMvc.perform(MockMvcRequestBuilders.post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new LiveShareRoomLeaveRequest()))
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + createAccessToken(userUUID)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.result").value(true))
			.andReturn();
	}

	@Test
	@DisplayName("leave live share room inactive room")
	void leaveLiveShareRoom_inactiveRoom() throws Exception {
		String contentUUID = "1016b7fe-88b2-497f-a04c-98be8fbcccce";
		String userUUID = "ouXLjBs6Rmd95";
		long roomId = 4;
		String url = String.format("/contents/%s/liveShare/rooms/%s/leave", contentUUID, roomId);
		LiveShareRoomLeaveRequest liveShareRoomLeaveRequest = new LiveShareRoomLeaveRequest();
		liveShareRoomLeaveRequest.setData("123");

		mockMvc.perform(MockMvcRequestBuilders.post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(liveShareRoomLeaveRequest))
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + createAccessToken(userUUID)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.result").value(true))
			.andReturn();
	}

	@Test
	@DisplayName("update live share user role")
	void updateLiveShareUserRole() throws Exception {
		String contentUUID = "1016b7fe-88b2-497f-a04c-98be8fbcccce";
		String userUUID = "XpzzPw8dQOjgS";
		long roomId = 3;
		String url = String.format(
			"/contents/%s/liveShare/rooms/%s/users/%s/role/LEADER", contentUUID, roomId, userUUID);

		mockMvc.perform(MockMvcRequestBuilders.put(url)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + createAccessToken("498b1839dc29ed7bb2ee90ad6985c608")))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.result").value(true))
			.andReturn();
	}

	private String createAccessToken(String userUUID) {
		Date now = new Date();
		Date expireDate = new Date((now.getTime() + 3900));

		return Jwts.builder()
			.setSubject("userName")
			.claim("userId", 1L)
			.claim("uuid", userUUID)
			.claim("name", "name")
			.claim("authorities", new ArrayList<>())
			.claim("jwtId", "jwtId")
			.setIssuedAt(now)
			.setExpiration(expireDate)
			.setIssuer("issuer")
			.signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes()))
			.compact();
	}
}