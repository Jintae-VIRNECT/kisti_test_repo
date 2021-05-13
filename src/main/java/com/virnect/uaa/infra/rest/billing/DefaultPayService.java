package com.virnect.uaa.infra.rest.billing;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.infra.rest.billing.dto.BillingCouponRegisterRequest;
import com.virnect.uaa.infra.rest.billing.dto.BillingCouponRegisterResponse;
import com.virnect.uaa.infra.rest.billing.dto.BillingRestResponse;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile(value = {"!onpremise", "!develop"})
public class DefaultPayService implements PayService {
	private static final String COUPON_REGISTER_API_URL = "/billing/coupon/registerbyid";
	private final RestTemplate restTemplate;

	@Value("${payletter.coupon:0}")
	private int couponId;
	@Value("${payletter.api:none}")
	private String BILLING_API_ENDPOINT;
	@Value("${spring.profiles.active:develop}")
	private String serverMode;

	@PostConstruct
	public void payServiceInit() {
		log.info("============================================================================");
		log.info("--> PAY_LETTER_REST_SERVICE_ENABLE..");
		log.info("--> COUPON_ID: {}", couponId);
		log.info("--> BILLING_API: {}", BILLING_API_ENDPOINT);
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

		log.info("[USER_EVENT_COUPON_REGISTER_START] - {}", billingCouponRegisterRequest);
		try {
			BillingRestResponse<BillingCouponRegisterResponse> couponRegisterResponse = restTemplate.postForObject(
				BILLING_API_ENDPOINT + COUPON_REGISTER_API_URL, billingCouponRegisterRequest, BillingRestResponse.class
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
