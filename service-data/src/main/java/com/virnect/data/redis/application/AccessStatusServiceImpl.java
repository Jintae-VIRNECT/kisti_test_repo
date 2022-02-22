package com.virnect.data.redis.application;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.account.AccountRestService;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.redis.dao.AccessStatusRepository;
import com.virnect.data.redis.domain.AccessStatus;
import com.virnect.data.redis.domain.AccessType;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessStatusServiceImpl implements AccessStatusService {

	private final AccessStatusRepository accessStatusRepository;
	private final AccountRestService accountRestService;

	public void saveAccessStatus(
		String workspaceId,
		String uuid,
		AccessType accessType
	) {
		String id = makeAccessStatusId(workspaceId, uuid);
		try {
			AccessStatus accessStatus = accessStatusRepository.findById(id).orElse(null);
			if (!ObjectUtils.isEmpty(accessStatus)) {
				if (accessType == AccessType.LEAVE) {
					accessStatus.setAccessType(AccessType.LOGIN);
				} else {
					accessStatus.setAccessType(accessType);
				}
				accessStatusRepository.save(accessStatus);
			} else {
				UserInfoResponse userInfo = accountRestService.getUserInfoByUserId(uuid).getData();
				AccessStatus target = new AccessStatus(id, userInfo.getEmail(), accessType);
				if (accessType == AccessType.LEAVE) {
					target.setAccessType(AccessType.LOGIN);
				}
				accessStatusRepository.save(target);
			}
		} catch (Exception e) {
			log.info("[REDIS:POST] Error exception message : " + e.getMessage());
		}
	}

	public AccessStatus getAccessStatus(
		String workspaceId,
		String uuid
	) {
		try {
			return accessStatusRepository.findById(makeAccessStatusId(workspaceId, uuid)).orElse(null);
		} catch (Exception e) {
			log.info("[REDIS:GET] Error exception message : " + e.getMessage());
			return null;
		}
	}

	@Override
	public String makeAccessStatusId(String workspaceId, String uuid) {
		return workspaceId + "_" + uuid;
	}

}
