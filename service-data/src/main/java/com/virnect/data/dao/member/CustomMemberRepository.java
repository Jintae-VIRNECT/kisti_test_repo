package com.virnect.data.dao.member;

import java.util.List;
import java.util.Optional;

import com.virnect.data.domain.member.Member;

public interface CustomMemberRepository {

	Optional<Member> findRoomLeaderBySessionId(String workspaceId, String sessionId);

	Optional<Member> findBySessionIdAndUuid(String sessionId, String uuid);

	List<Member> findByWorkspaceIdAndUuid(String workspaceId, String uuid);

	Optional<Member> findGuestMemberByWorkspaceIdAndUuid(String workspaceId, String uuid);

}
