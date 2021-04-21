package com.virnect.uaa.domain.user.dao;

import org.springframework.data.repository.CrudRepository;

import com.virnect.uaa.domain.user.domain.EmailAuth;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description Email Authentication Domain Repository
 * @since 2020.03.19
 */

public interface EmailAuthRepository extends CrudRepository<EmailAuth, String> {
}
