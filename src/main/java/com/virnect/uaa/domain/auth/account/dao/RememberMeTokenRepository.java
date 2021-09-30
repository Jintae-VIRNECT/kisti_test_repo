package com.virnect.uaa.domain.auth.account.dao;

import org.springframework.data.repository.CrudRepository;

import com.virnect.uaa.domain.auth.account.domain.RememberMeToken;

/**
 * Project: PF-Auth
 * DATE: 2021-03-04
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface RememberMeTokenRepository extends CrudRepository<RememberMeToken, String> {
}
