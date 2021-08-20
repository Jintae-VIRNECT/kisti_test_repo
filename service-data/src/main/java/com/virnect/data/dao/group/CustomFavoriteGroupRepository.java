package com.virnect.data.dao.group;

import java.util.List;

import com.virnect.data.domain.group.FavoriteGroup;

public interface CustomFavoriteGroupRepository {
	long findByWorkspaceIdAndUserIdGroupCount(String workspaceId, String userId);

	List<FavoriteGroup> findByWorkspaceIdAndUserId(String workspaceId, String userId);

	FavoriteGroup findByWorkspaceIdAndGroupId(String workspaceId, String groupId);

	FavoriteGroup findByWorkspaceIdAndUserIdAndGroupId(String workspaceId, String userId, String groupId);
}
