package com.virnect.data.redis.application;

import com.virnect.data.redis.domain.NonmemberAuth;

public interface NonmemberService {

	NonmemberAuth saveNonmemberAuth(String sessionId);

	NonmemberAuth getNonmemberAuth(String sessionId);

	boolean deleteNonmemberAuth(String sessionId);

}
