package com.virnect.uaa.domain.auth.dao.user;

import java.util.Optional;

public interface CustomUserRepository {
	Optional<User> findLoginUserInformationByUserEmail(String email);
}
