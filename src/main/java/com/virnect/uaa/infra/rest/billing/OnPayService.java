package com.virnect.uaa.infra.rest.billing;

import java.util.Collections;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.infra.rest.billing.dto.CouponRegisterRequest;
import com.virnect.uaa.infra.rest.billing.dto.CouponRegisterResponse;
import com.virnect.uaa.infra.rest.billing.dto.PayletterApiResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnPayService implements PayService {
	private static final String COUPON_REGISTER_API_URL = "/billing/coupon/registerbyid";
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@Value("${payletter.coupon:0}")
	private int couponId;
	@Value("${payletter.api:none}")
	private String billingApiEndpoint;
	@Value("${payletter.enabled:false}")
	private boolean payletterApiEnabled;

	@PostConstruct
	public void payServiceInit() {
		log.info("============================================================================");
		log.info("--> PAY_LETTER_REST_SERVICE");
		log.info("--> COUPON_ID: {}", couponId);
		log.info("--> BILLING_API: {}", billingApiEndpoint);
		log.info("--> ENABLED_MODE: {}", payletterApiEnabled);
		log.info("============================================================================");
	}

	/**
	 *  @param email - 사용자 이메일
	 * @param name - 사용자 이름
	 * @param userId - 사용자 식별 번호
	 * @return
	 */
	public ResponseEntity<PayletterApiResponse<CouponRegisterResponse>> welcomeEventCouponRegister(
		final String email, final String name, final long userId
	) {
		if (!payletterApiEnabled) {
			return null;
		}

		CouponRegisterRequest welcomeCouponRegisterRequest = CouponRegisterRequest.builder()
			.siteCode(1)
			.couponId(couponId)
			.userEmail(email)
			.userName(name)
			.userNumber(userId)
			.build();

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			String couponRegisterJsonRequest = objectMapper.writeValueAsString(welcomeCouponRegisterRequest);
			HttpEntity<String> couponRegisterReqHttpEntity = new HttpEntity<>(couponRegisterJsonRequest, headers);

			log.info("[USER_EVENT_COUPON_REGISTER_REQUEST] - [{}]", couponRegisterReqHttpEntity);

			return restTemplate.exchange(
				billingApiEndpoint + COUPON_REGISTER_API_URL, HttpMethod.POST, couponRegisterReqHttpEntity,
				new ParameterizedTypeReference<PayletterApiResponse<CouponRegisterResponse>>() {
				}
			);
		} catch (Exception e) {
			log.error("[USER_EVENT_COUPON_REGISTER_ERROR]", e);
			return null;
		}
	}
}
