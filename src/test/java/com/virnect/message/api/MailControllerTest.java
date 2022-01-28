package com.virnect.message.api;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.message.dto.request.AttachmentMailRequest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MailControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("send attachment mail")
	void sendAttachmentMail() throws Exception {
		String url = "/messages/mail/attachment";

		ArrayList<String> receiver = new ArrayList<>();
		receiver.add("ljk@aaaa.com");
		AttachmentMailRequest attachmentMailRequest = new AttachmentMailRequest();
		attachmentMailRequest.setReceivers(receiver);
		attachmentMailRequest.setSender("no-reply@virnect.com");
		attachmentMailRequest.setHtml("<html></html>");
		attachmentMailRequest.setSubject("subject");
		attachmentMailRequest.setMultipartFile(
			"https://192.168.6.3:2838/virnect-homepagestorage/roi/37e3ab9f8dd847b88d9ddf3e5c094dd8.pdf");

		mockMvc.perform(MockMvcRequestBuilders
				.post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(attachmentMailRequest))
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true))
			.andReturn();
	}
}