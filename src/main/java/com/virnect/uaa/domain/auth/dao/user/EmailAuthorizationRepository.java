package com.virnect.uaa.domain.auth.dao.user;

import org.springframework.data.repository.CrudRepository;

import com.virnect.uaa.domain.auth.domain.user.EmailAuth;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description Redis Email Authorization Repository
 * @since 2020.03.17
 */
public interface EmailAuthorizationRepository extends CrudRepository<EmailAuth, String> {
	boolean existsByEmail(String email);
}
