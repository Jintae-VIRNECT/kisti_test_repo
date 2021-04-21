package com.virnect.uaa.domain.auth.dao.user;

import org.springframework.data.repository.CrudRepository;

import com.virnect.uaa.domain.auth.domain.user.BlockToken;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description Blocked Token Data Repository
 * @since 2020.03.20
 */
public interface BlockTokenRepository extends CrudRepository<BlockToken, String> {
}
