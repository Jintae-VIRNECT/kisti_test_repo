package com.virnect.data.dao.group;

import java.util.List;

import com.virnect.data.domain.group.RemoteGroup;

public interface CustomRemoteGroupRepository {

	List<RemoteGroup> findByWorkspaceId(String workspaceId);

	List<RemoteGroup> findByWorkspaceIdAndUserIdArray(String workspaceId, List<String> userIds);

	long findByWorkspaceIdGroupCount(String workspaceId);

	RemoteGroup findByWorkspaceIdAndGroupId(String workspaceId, String groupId);

	RemoteGroup findByWorkspaceIdAndGroupIdAndUserId(String workspaceId, String groupId, String userId);

}
