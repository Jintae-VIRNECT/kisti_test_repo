package com.virnect.data.dao.group;

import java.util.Optional;

import com.virnect.data.domain.group.RemoteGroupMember;

public interface CustomRemoteGroupMemberRepository {

	Optional<RemoteGroupMember> findByRemoteGroupIdAndUuid(Long remoteGroupId, String uuid);

	void deleteByRemoteGroupIdAndUuid(Long remoteGroupId, String uuid);

}
