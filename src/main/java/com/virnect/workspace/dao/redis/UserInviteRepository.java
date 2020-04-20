package com.virnect.workspace.dao.redis;

import com.virnect.workspace.domain.redis.UserInvite;
import org.springframework.data.repository.CrudRepository;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface UserInviteRepository extends CrudRepository<UserInvite, String> {
    UserInvite findByJoinUserIdAndAndCode(String joinUserId, String code);
}
