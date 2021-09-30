package com.virnect.uaa.domain.auth.account.dao;

import org.springframework.data.repository.CrudRepository;

import com.virnect.uaa.domain.auth.account.domain.EmailAuth;

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
