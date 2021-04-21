package com.virnect.uaa.domain.auth.dao.user;

import org.springframework.data.repository.CrudRepository;

import com.virnect.uaa.domain.auth.domain.user.LoginAttempt;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description
 * @since 2020.05.08
 */
public interface LoginAttemptRepository extends CrudRepository<LoginAttempt, String> {
	LoginAttempt findByEmail(String email);
}
