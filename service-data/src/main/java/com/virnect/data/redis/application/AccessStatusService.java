package com.virnect.data.redis.application;

import com.virnect.data.redis.domain.AccessStatus;
import com.virnect.data.redis.domain.AccessType;

public interface AccessStatusService {

	void saveAccessStatus(String workspaceId, String uuid, AccessType accessType);

	AccessStatus getAccessStatus(String workspaceId, String uuid);

	String makeAccessStatusId(String workspaceId, String uuid);

}
