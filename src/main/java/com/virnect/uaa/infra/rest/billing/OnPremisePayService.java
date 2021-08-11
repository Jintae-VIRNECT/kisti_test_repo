package com.virnect.uaa.infra.rest.billing;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile(value = {"develop", "onpremise", "local"})
public class OnPremisePayService implements PayService {

	@PostConstruct
	public void payServiceInit() {
		log.info("============================================================================");
		log.info("--> PAY_LETTER_REST_SERVICE_DISABLE..");
		log.info("============================================================================");
	}

	@Override
	public void eventCouponRegisterToNewUser(String email, String name, long userId) {
		// nothing to do after sign up process. in onpremise or develop environment.
		log.info("PayService, CouponRegister Event ! email: [{}] name: [{}] userId: [{}]", email, name, userId);
	}
}
