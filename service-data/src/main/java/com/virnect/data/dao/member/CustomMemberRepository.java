package com.virnect.data.dao.member;

import java.util.List;

import com.virnect.data.domain.member.Member;

public interface CustomMemberRepository {

	Member findRoomLeaderBySessionId(String workspaceId, String sessionId);

	Member findBySessionIdAndUuid(String sessionId, String uuid);

	List<Member> findByWorkspaceIdAndUuid(String workspaceId, String uuid);

}
