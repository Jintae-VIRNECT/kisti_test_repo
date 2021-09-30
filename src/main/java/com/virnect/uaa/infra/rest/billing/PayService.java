package com.virnect.uaa.infra.rest.billing;

public interface PayService {
	void eventCouponRegisterToNewUser(final String email, final String name, final long userId);
}
