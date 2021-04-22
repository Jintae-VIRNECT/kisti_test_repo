package com.virnect.uaa.infra.rest.billing;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.infra.rest.billing.dto.BillingCouponRegisterRequest;
import com.virnect.uaa.infra.rest.billing.dto.BillingCouponRegisterResponse;
import com.virnect.uaa.infra.rest.billing.dto.BillingRestResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayApiService {
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@Value("${payletter.coupon:0}")
	private int couponId;
	@Value("${payletter.api:none}")
	private String billingApi;
	@Value("${spring.profiles.active:develop}")
	private String serverMode;
	@Value("${payletter.enabled:false}")
	private boolean payletterEvent;

	@PostConstruct
	public void payServiceInit() {
		log.info("============================================================================");
		log.info("--> PAY_LETTER_REST_SERVICE_ENABLE: {}", payletterEvent);
		log.info("--> COUPON_ID: {}", couponId);
		log.info("--> BILLING_API: {}", billingApi);
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
		if (serverMode.equals("onpremise") || serverMode.equals("develop") || !payletterEvent) {
			log.info(
				"[USER_EVENT_COUPON_REGISTER_INACTIVE] - eventEnabled: {} , serverMode: {} , email: {} , name: {}",
				payletterEvent, serverMode, email, name
			);
			return;
		}

		BillingCouponRegisterRequest billingCouponRegisterRequest = new BillingCouponRegisterRequest();
		billingCouponRegisterRequest.setSiteCode(1);
		billingCouponRegisterRequest.setCouponId(couponId);
		billingCouponRegisterRequest.setUserEmail(email);
		billingCouponRegisterRequest.setUserName(name);
		billingCouponRegisterRequest.setUserNumber(userId);

		log.info("[USER_EVENT_COUPON_REGISTER_START] - {}", billingCouponRegisterRequest.toString());
		try {
			BillingRestResponse<BillingCouponRegisterResponse> couponRegisterResponse = restTemplate.postForObject(
				billingApi + "/billing/coupon/registerbyid", billingCouponRegisterRequest, BillingRestResponse.class
			);
			if (couponRegisterResponse.getResult().getCode() != 0) {
				log.error("[USER_EVENT_COUPON_REGISTER_ERROR] - [{}]", couponRegisterResponse.getResult().toString());
			} else {
				log.info("[USER_EVENT_COUPON_REGISTER_SUCCESS] - [{}]", couponRegisterResponse.toString());
			}
		} catch (Exception e) {
			log.error("[USER_EVENT_COUPON_REGISTER_ERROR] - {}", billingCouponRegisterRequest.toString());
			log.error("[USER_EVENT_COUPON_REGISTER_ERROR]", e);
		}
		log.info("[USER_EVENT_COUPON_REGISTER_END]");
	}
}
