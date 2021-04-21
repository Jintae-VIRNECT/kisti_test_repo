package com.virnect.uaa.infra.rest.billing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BillingCouponRegisterRequest {
	@JsonProperty(value = "sitecode")
	private int siteCode;
	@JsonProperty(value = "couponid")
	private int couponId;
	@JsonProperty(value = "userid")
	private String userEmail;
	@JsonProperty(value = "username")
	private String userName;
	@JsonProperty(value = "userno")
	private long userNumber;

	@Override
	public String toString() {
		return "BillingCouponRegisterRequest{" +
			"siteCode=" + siteCode +
			", userNumber=" + userNumber +
			", userEmail='" + userEmail + '\'' +
			", userName='" + userName + '\'' +
			", couponId=" + couponId +
			'}';
	}
}
