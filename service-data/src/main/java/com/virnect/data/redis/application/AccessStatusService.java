package com.virnect.data.redis.application;

import java.util.List;

import com.virnect.data.redis.domain.AccessStatus;
import com.virnect.data.redis.domain.AccessType;

public interface AccessStatusService {

	AccessStatus saveAccessStatus(String uuid, AccessType accessType);

	boolean deleteAccessStatus(String uuid);

	AccessStatus getAccessStatus(String uuid);

	List<AccessStatus> getAccessStatusAll();

}
