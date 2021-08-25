package com.virnect.data.dao.group;

import java.util.List;

import com.virnect.data.domain.group.FavoriteGroup;

public interface CustomFavoriteGroupRepository {
	long findByWorkspaceIdAndUserIdGroupCount(String workspaceId, String userId);

	List<FavoriteGroup> findByWorkspaceIdAndUserIdAndIncludeOneself(String workspaceId, String userId, boolean includeOneself);

	FavoriteGroup findByWorkspaceIdAndUserIdAndGroupId(String workspaceId, String userId, String groupId);

	FavoriteGroup findByWorkspaceIdAndUserIdAndGroupIdAndIncludeOneself(String workspaceId, String groupId, String userId, boolean includeOneself);
}
