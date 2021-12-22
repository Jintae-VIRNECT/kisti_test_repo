package com.virnect.uaa.infra.rest.billing.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PayletterApiResponse<T> {
	private T data;
	private BillingRestResult result;

	@Override
	public String toString() {
		return "BillingRestResponse{" +
			"data=" + data +
			", result=" + result +
			'}';
	}
}
