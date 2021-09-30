package com.virnect.workspace.dao.cache;

import com.virnect.workspace.domain.redis.UserInvite;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface UserInviteRepository extends CrudRepository<UserInvite, String> {

    default Optional<UserInvite> findByWorkspaceIdAndInvitedUserEmail(String workspaceId, String invitedEmail) {
        for (UserInvite userInvite : this.findAll()) {
            if (userInvite != null && userInvite.getWorkspaceId().equals(workspaceId) && userInvite.getInvitedUserEmail().equals(invitedEmail)) {
                return Optional.of(userInvite);
            }
        }
        return Optional.empty();
    }
}
