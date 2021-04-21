package com.virnect.uaa.domain.user.dao;

import org.springframework.data.repository.CrudRepository;

import com.virnect.uaa.domain.user.domain.PasswordInitAuthCode;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.13
 */

public interface UserPasswordAuthCodeRepository extends CrudRepository<PasswordInitAuthCode, String> {
}
