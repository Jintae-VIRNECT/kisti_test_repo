package com.virnect.data.dao.member;

import java.util.List;

import com.virnect.data.domain.member.RemoteGroup;

public interface CustomRemoteGroupRepository {

	List<RemoteGroup> findByWorkspaceIdAndUserId(String workspaceId, String userId);

	long findByWorkspaceIdAndUserIdGroupCount(String workspaceId, String userId);

	RemoteGroup findByWorkspaceIdAndGroupId(String workspaceId, String groupId);

}
