package com.virnect.uaa.domain.auth.dao.user;

import java.util.Optional;

import com.virnect.uaa.domain.user.domain.User;

public interface CustomUserRepository {
	Optional<User> findLoginUserInformationByUserEmail(String email);
}
