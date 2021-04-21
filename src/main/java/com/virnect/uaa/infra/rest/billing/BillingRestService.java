package com.virnect.uaa.infra.rest.billing;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.virnect.user.dto.rest.billing.BillingCouponRegisterRequest;
import com.virnect.user.dto.rest.billing.BillingCouponRegisterResponse;
import com.virnect.user.dto.rest.billing.BillingRestResponse;

@FeignClient(name = "${infra.billing.api:dev}", url = "${infra.billing.api:localhost}", fallbackFactory = BillingRestFallbackFactory.class)
public interface BillingRestService {
	@PostMapping("/billing/coupon/registerbyid")
	BillingRestResponse<BillingCouponRegisterResponse> registerBillingEventCouponToUser(
		@RequestBody BillingCouponRegisterRequest billingCouponRegisterRequest
	);
}
