package com.virnect.uaa.infra.rest.billing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CouponRegisterRequest {
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

	@Builder
	public CouponRegisterRequest(
		int siteCode, int couponId, String userEmail, String userName, long userNumber
	) {
		this.siteCode = siteCode;
		this.couponId = couponId;
		this.userEmail = userEmail;
		this.userName = userName;
		this.userNumber = userNumber;
	}

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
