package com.virnect.uaa.infra.rest.billing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingRestResult {
	private int code;
	private String message;
	private String detail;

	@Override
	public String toString() {
		return "BillingRestResult{" +
			"code=" + code +
			", message='" + message + '\'' +
			", detail='" + detail + '\'' +
			'}';
	}
}
