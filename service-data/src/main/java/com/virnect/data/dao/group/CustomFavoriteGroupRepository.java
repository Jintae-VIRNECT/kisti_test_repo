package com.virnect.data.dao.group;

import java.util.List;
import java.util.Optional;

import com.virnect.data.domain.group.FavoriteGroup;

public interface CustomFavoriteGroupRepository {
	long findByWorkspaceIdAndUserIdGroupCount(String workspaceId, String userId);

	List<FavoriteGroup> findByWorkspaceIdAndUserIdAndIncludeOneself(String workspaceId, String userId, boolean includeOneself);

	Optional<FavoriteGroup> findByWorkspaceIdAndUserIdAndGroupId(String workspaceId, String userId, String groupId);

	Optional<FavoriteGroup> findByWorkspaceIdAndUserIdAndGroupIdAndIncludeOneself(String workspaceId, String groupId, String userId, boolean includeOneself);
}
