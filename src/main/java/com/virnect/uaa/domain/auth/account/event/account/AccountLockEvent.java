package com.virnect.uaa.domain.auth.account.event.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.uaa.domain.auth.account.domain.LoginAttempt;
import com.virnect.uaa.domain.user.domain.User;

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
