package com.virnect.uaa.infra.rest.billing;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.virnect.uaa.infra.rest.billing.dto.BillingCouponRegisterRequest;
import com.virnect.uaa.infra.rest.billing.dto.BillingCouponRegisterResponse;
import com.virnect.uaa.infra.rest.billing.dto.BillingRestResponse;

@FeignClient(name = "${infra.billing.api:dev}", url = "${infra.billing.api:localhost}", fallbackFactory = BillingRestFallbackFactory.class)
public interface BillingRestService {
	@PostMapping("/billing/coupon/registerbyid")
	BillingRestResponse<BillingCouponRegisterResponse> registerBillingEventCouponToUser(
		@RequestBody BillingCouponRegisterRequest billingCouponRegisterRequest
	);
}
