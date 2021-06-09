package com.virnect.data.dao.member;

import com.virnect.data.domain.member.Member;

public interface CustomMemberRepository {

	Member findRoomLeaderBySessionId(String workspaceId, String sessionId);

	Member findBySessionIdAndUuid(String sessionId, String uuid);

}
