package com.virnect.uaa.domain.auth.account.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.uaa.domain.auth.account.domain.UserOTP;

public interface UserOTPRepository extends JpaRepository<UserOTP, Long> {
	UserOTP findByEmail(String email);

	boolean existsByEmail(String email);

	void deleteAllByEmail(String email);
}
