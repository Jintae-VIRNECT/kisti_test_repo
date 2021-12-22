package com.virnect.uaa.infra.rest.billing;

import org.springframework.http.ResponseEntity;

import com.virnect.uaa.infra.rest.billing.dto.CouponRegisterResponse;
import com.virnect.uaa.infra.rest.billing.dto.PayletterApiResponse;

public interface PayService {
	ResponseEntity<PayletterApiResponse<CouponRegisterResponse>> welcomeEventCouponRegister(
		final String email, final String name, final long userId
	);
}
