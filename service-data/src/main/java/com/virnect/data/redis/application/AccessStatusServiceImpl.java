package com.virnect.data.redis.application;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.user.UserRestService;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.redis.dao.AccessStatusRepository;
import com.virnect.data.redis.domain.AccessStatus;
import com.virnect.data.redis.domain.AccessType;
import com.virnect.data.redis.util.LogMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessStatusServiceImpl implements AccessStatusService {

	private static final String TAG = AccessStatusServiceImpl.class.getSimpleName();

	private final AccessStatusRepository accessStatusRepository;
	private final UserRestService userRestService;

	public AccessStatus saveAccessStatus(
		String id,
		AccessType accessType,
		String uuid
	) {
		LogMessage.formedInfo(
			TAG,
			"[REDIS:POST] "
				+ "uuid:" + id + ","
				+ "accessType:" + accessType,
			"saveAccessStatus"
		);
		AccessStatus savedTarget = null;
		try {
			AccessStatus findData = accessStatusRepository.findById(id).orElse(null);
			if (!ObjectUtils.isEmpty(findData)) {
				if (findData.getAccessType() == accessType) {
					log.info("This access status data is already saved : " + findData.toString());
					return findData;
				}
				AccessType findDataAccessType = findData.getAccessType();
				if (accessType == AccessType.LEAVE) {
					findData.setAccessType(AccessType.LOGIN);
				} else {
					findData.setAccessType(accessType);
				}
				savedTarget = accessStatusRepository.save(findData);
				log.info(
					"updated status : " + savedTarget.getId()
						+ "(" + findDataAccessType.toString() + "->" + savedTarget.getAccessType().toString() + ")");
			} else {
				log.info("findData is null");
				AccessStatus target = new AccessStatus(id, null, accessType);
				if (accessType == AccessType.LEAVE) {
					target.setAccessType(AccessType.LOGIN);
				}
				UserInfoResponse userInfo = userRestService.getUserInfoByUserId(uuid).getData();
				if (!ObjectUtils.isEmpty(userInfo)) {
					target.setEmail(userInfo.getEmail());
				}
				savedTarget = accessStatusRepository.save(target);
				log.info("saved data : " + savedTarget.toString());
			}
		} catch (Exception e) {
			log.info("[REDIS:POST] Error exception message : " + e.getMessage());
		}
		return savedTarget;
	}

	public AccessStatus getAccessStatus(
		String id
	) {
		LogMessage.formedInfo(
			TAG,
			"[REDIS:GET] "
				+ "uuid:" + id,
			"getAccessStatus"
		);
		AccessStatus findData = null;
		try {
			findData = accessStatusRepository.findById(id).orElse(null);
			if (ObjectUtils.isEmpty(findData)) {
				LogMessage.formedInfo("[REDIS:GET] findData is null");
			}
		} catch (Exception e) {
			log.info("[REDIS:GET] Error exception message : " + e.getMessage());
		}
		return findData;
	}

	@Override
	public List<AccessStatus> getAccessStatusAll() {
		return StreamSupport
			.stream(accessStatusRepository.findAll().spliterator(), false)
			.collect(Collectors.toList());
	}

	public boolean deleteAccessStatus(
		String id
	) {
		LogMessage.formedInfo(
			TAG,
			"[REDIS:DELETE] "
				+ "uuid:" + id,
			"deleteAccessStatus"
		);
		boolean result = true;
		try {
			AccessStatus targetData = accessStatusRepository.findById(id).orElse(null);
			if (ObjectUtils.isEmpty(targetData)) {
				LogMessage.formedInfo("[REDIS:DELETE] findData is null");
				result = false;
			}
			accessStatusRepository.delete(Objects.requireNonNull(targetData));
		} catch (Exception e) {
			result = false;
			log.info("[REDIS:DELETE] Error exception message : " + e.getMessage());
		}
		return result;
	}

}
