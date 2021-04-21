package com.virnect.uaa.domain.auth.event.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.uaa.domain.auth.domain.user.LoginAttempt;

@Getter
@RequiredArgsConstructor
public class AccountLockEvent {
	private final User user;
	private final LoginAttempt loginAttemptInfo;

	@Override
	public String toString() {
		return "AccountLockEvent{" +
			"user=" + user +
			", loginAttemptInfo=" + loginAttemptInfo +
			'}';
	}
}
