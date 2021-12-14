package com.virnect.data.dao.group;

import java.util.List;
import java.util.Optional;

import com.virnect.data.domain.group.RemoteGroup;

public interface CustomRemoteGroupRepository {

	List<RemoteGroup> findByWorkspaceIdAndUserIdAndIncludeOneself(
		String workspaceId, String userId, boolean includeOneself
	);

	long findByWorkspaceIdAndUserIdArray(String workspaceId, List<String> userIds);

	long findGroupCountOfWorkspaceId(String workspaceId);

	Optional<RemoteGroup> findByWorkspaceIdAndGroupId(String workspaceId, String groupId);

	Optional<RemoteGroup> findByWorkspaceIdAndGroupIdAndUserId(String workspaceId, String groupId, String userId);

	Optional<RemoteGroup> findByWorkspaceIdAndGroupIdAndUserIdAndIncludeOneself(
		String workspaceId, String groupId, String userId, boolean includeOneself
	);

	long findByWorkspaceIdAndGroupName(String workspaceId, String groupName);
}
