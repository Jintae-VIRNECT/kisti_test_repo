package com.virnect.uaa.infra.rest.billing;

import java.util.Collections;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.infra.rest.billing.dto.BillingCouponRegisterRequest;
import com.virnect.uaa.infra.rest.billing.dto.BillingRestResponse;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile(value = {"staging", "production"})
public class OnPayService implements PayService {
	private static final String COUPON_REGISTER_API_URL = "/billing/coupon/registerbyid";
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@Value("${payletter.coupon:0}")
	private int couponId;
	@Value("${payletter.api:none}")
	private String billingApiEndpoint;
	@Value("${spring.profiles.active:develop}")
	private String serverMode;

	@PostConstruct
	public void payServiceInit() {
		log.info("============================================================================");
		log.info("--> PAY_LETTER_REST_SERVICE_ENABLE..");
		log.info("--> COUPON_ID: {}", couponId);
		log.info("--> BILLING_API: {}", billingApiEndpoint);
		log.info("--> SERVICE_MODE: {}", serverMode);
		log.info("============================================================================");
	}

	/**
	 *
	 * @param email - 사용자 이메일
	 * @param name - 사용자 이름
	 * @param userId - 사용자 식별 번호
	 */
	public void eventCouponRegisterToNewUser(final String email, final String name, final long userId) {
		BillingCouponRegisterRequest billingCouponRegisterRequest = new BillingCouponRegisterRequest();
		billingCouponRegisterRequest.setSiteCode(1);
		billingCouponRegisterRequest.setCouponId(couponId);
		billingCouponRegisterRequest.setUserEmail(email);
		billingCouponRegisterRequest.setUserName(name);
		billingCouponRegisterRequest.setUserNumber(userId);

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			String couponRegisterJsonReqBody = objectMapper.writeValueAsString(billingCouponRegisterRequest);
			HttpEntity<String> couponRegisterReqHttpEntity = new HttpEntity<>(couponRegisterJsonReqBody, headers);

			log.info("[USER_EVENT_COUPON_REGISTER_REQUEST] - [{}]", couponRegisterReqHttpEntity);

			BillingRestResponse<?> couponRegisterResponse = restTemplate.postForObject(
				billingApiEndpoint + COUPON_REGISTER_API_URL, couponRegisterReqHttpEntity, BillingRestResponse.class
			);

			if (couponRegisterResponse == null || couponRegisterResponse.getResult().getCode() != 0) {
				log.error("[USER_EVENT_COUPON_REGISTER_ERROR] - [{}]", couponRegisterResponse);
			} else {
				log.info("[USER_EVENT_COUPON_REGISTER_SUCCESS] - [{}]", couponRegisterResponse);
			}
		} catch (Exception e) {
			log.error("[USER_EVENT_COUPON_REGISTER_ERROR]", e);
		}
	}
}
