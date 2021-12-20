package com.virnect.uaa.infra.rest.billing;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PayServiceTest {
	@MockBean
	PayService payService;

	@Test
	@DisplayName("PayService Parameter Mocking Test")
	void eventCouponRegisterToNewUser() {
		String testEmail = "sky456139@virnect.com";
		String testName = "장정현";
		int testUserId = 11;

		doAnswer(invocation -> {
			String email = invocation.getArgument(0);
			String name = invocation.getArgument(1);
			int userId = invocation.getArgument(2);

			assertEquals(email, testEmail);
			assertEquals(name, testName);
			assertEquals(userId, testUserId);
			return null;
		}).when(payService).eventCouponRegisterToNewUser(anyString(), anyString(), anyInt());

		payService.eventCouponRegisterToNewUser(testEmail, testName, testUserId);
	}
}