package com.virnect.data.redis.application;

import java.util.List;

import com.virnect.data.redis.domain.AccessStatus;
import com.virnect.data.redis.domain.AccessType;

public interface AccessStatusService {

	AccessStatus saveAccessStatus(String id, AccessType accessType, String uuid);

	boolean deleteAccessStatus(String uuid);

	AccessStatus getAccessStatus(String uuid);

	List<AccessStatus> getAccessStatusAll();

}
