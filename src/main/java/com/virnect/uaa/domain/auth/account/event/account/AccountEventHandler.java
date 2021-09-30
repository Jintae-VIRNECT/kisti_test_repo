package com.virnect.uaa.domain.auth.account.event.account;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.dao.LoginAttemptRepository;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.domain.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventHandler {
	private final UserRepository userRepository;
	private final LoginAttemptRepository loginAttemptRepository;

	@Async
	@EventListener(AccountLockEvent.class)
	public void accountLockEventListener(AccountLockEvent accountLockEvent) {
		User user = accountLockEvent.getUser();
		log.info("Account Login Attempt Info - {}", accountLockEvent.getLoginAttemptInfo().toString());
		log.info("Email: [{}] , Name: [{}] account is will be lock.", user.getEmail(), user.getName());
		user.setAccountNonLocked(false);
		userRepository.save(user);

		loginAttemptRepository.delete(accountLockEvent.getLoginAttemptInfo());
		log.info("Login attempt data delete.");
	}
}
