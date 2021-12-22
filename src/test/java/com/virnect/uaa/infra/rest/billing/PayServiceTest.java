package com.virnect.uaa.infra.rest.billing;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class PayServiceTest {
	@Autowired
	PayService payService;

	@Test
	@DisplayName("PayApiService - CouponRegister 200_OK")
	void couponRegisterWith_200_OK() {
		String testEmail = "sky456139@virnect.com";
		String testName = "장정현";
		int testUserId = 11;

		ResponseEntity<?> responseEntity = payService.welcomeEventCouponRegister(testEmail, testName, testUserId);

		assertThat(responseEntity.getStatusCode()).isSameAs(HttpStatus.OK);
	}
}