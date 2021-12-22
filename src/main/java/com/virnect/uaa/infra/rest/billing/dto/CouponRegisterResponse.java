package com.virnect.uaa.infra.rest.billing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponRegisterResponse {
	@JsonProperty(value = "CouponName")
	private String couponName;
	@JsonProperty(value = "CouponType")
	private String couponType;

	@Override
	public String toString() {
		return "BillingCouponRegisterResponse{" +
			"couponName='" + couponName + '\'' +
			", couponType='" + couponType + '\'' +
			'}';
	}
}
