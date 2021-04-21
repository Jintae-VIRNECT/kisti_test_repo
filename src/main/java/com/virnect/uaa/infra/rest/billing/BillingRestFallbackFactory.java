package com.virnect.uaa.infra.rest.billing;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.user.dto.rest.billing.BillingCouponRegisterResponse;
import com.virnect.user.dto.rest.billing.BillingRestResponse;
import com.virnect.user.dto.rest.billing.BillingRestResult;

@Slf4j
@Component
public class BillingRestFallbackFactory implements FallbackFactory<BillingRestService> {
	@Override
	public BillingRestService create(Throwable cause) {
		return billingCouponRegisterRequest -> {
			log.error("[BILLING_COUPON_REGISTER_ERROR] - {}", billingCouponRegisterRequest.toString());
			log.error(cause.getMessage(), cause);
			BillingCouponRegisterResponse response = new BillingCouponRegisterResponse();
			response.setCouponName("쿠폰 명도 망했다");
			response.setCouponType("쿠폰 타입도 망했다");
			BillingRestResult errorResult = new BillingRestResult();
			errorResult.setCode(999);
			BillingRestResponse<BillingCouponRegisterResponse> errorResponse = new BillingRestResponse<>();
			errorResponse.setData(response);
			errorResponse.setResult(errorResult);
			return errorResponse;
		};
	}
}
