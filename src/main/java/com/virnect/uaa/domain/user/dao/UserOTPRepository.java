package com.virnect.uaa.domain.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.user.domain.UserOTP;

public interface UserOTPRepository extends JpaRepository<UserOTP, Long> {
	void deleteAllByEmail(String email);
}
