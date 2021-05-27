package com.virnect.data.redis.application;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.infra.utils.LogMessage;
import com.virnect.data.redis.dao.NonmemberRepository;
import com.virnect.data.redis.domain.NonmemberAuth;

@Slf4j
@Service
@RequiredArgsConstructor
public class NonmemberServiceImpl implements NonmemberService {

	private static final String TAG = NonmemberServiceImpl.class.getSimpleName();
	private static final String AUTH_CODE = "non_";
	private final NonmemberRepository nonmemberRepository;

	@Override
	public NonmemberAuth saveNonmemberAuth(String sessionId) {
		LogMessage.formedInfo(
			TAG,
			"[REDIS:POST] "
				+ "sessionId:" + sessionId,
			"saveNonmemberAuth"
		);
		NonmemberAuth savedData = null;
		try {
			NonmemberAuth findData = getNonmemberAuth(sessionId);
			if (!ObjectUtils.isEmpty(findData)) {
				log.info("This auth code data is already saved : " + findData.toString());
			} else {
				savedData = NonmemberAuth.builder()
					.sessionId(sessionId)
					.authCode(AUTH_CODE + RandomStringUtils.randomAlphabetic(1).toUpperCase())
					.build();
				nonmemberRepository.save(savedData);
			}
		} catch (Exception e) {
			log.info("[REDIS:POST] Error exception message : " + e.getMessage());
		}
		return savedData;
	}

	@Override
	public NonmemberAuth getNonmemberAuth(String sessionId) {
		LogMessage.formedInfo(
			TAG,
			"[REDIS:GET] "
				+ "sessionId:" + sessionId,
			"getNonmemberAuth"
		);
		NonmemberAuth targetData = null;
		try {
			targetData = nonmemberRepository.findById(sessionId).orElse(null);
			if (ObjectUtils.isEmpty(targetData)) {
				log.info("This auth code data is null");
			}
		} catch (Exception e) {
			log.info("[REDIS:GET] Error exception message : " + e.getMessage());
		}
		return targetData;
	}

	@Override
	public boolean deleteNonmemberAuth(String sessionId) {
		LogMessage.formedInfo(
			TAG,
			"[REDIS:DELETE] "
				+ "sessionId:" + sessionId,
			"deleteNonmemberAuth"
		);
		boolean deleteResult = false;
		try {
			NonmemberAuth targetData = nonmemberRepository.findById(sessionId).orElse(null);
			if (ObjectUtils.isEmpty(targetData)) {
				log.info("This auth code data is null");
			} else {
				nonmemberRepository.deleteById(sessionId);
				deleteResult = true;
			}
		} catch (Exception e) {
			log.info("[REDIS:GET] Error exception message : " + e.getMessage());
		}
		return deleteResult;
	}
}
