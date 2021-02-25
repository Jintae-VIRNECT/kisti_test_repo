package com.virnect.data.dao.memberhistory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.data.domain.member.MemberHistory;

public interface CustomMemberHistoryRepository {

	Page<MemberHistory> findByWorkspaceIdAndUuidAndRoomHistoryIsNotNullAndHistoryDeletedFalse(final String workspaceId, final String userId, Pageable pageable);

	List<MemberHistory> findAllBySessionId(final String sessionId);

	List<MemberHistory> findByWorkspaceIdAndUuid(final String workspaceId, final String userId);

	MemberHistory findByWorkspaceIdAndSessionIdAndUuid(final String workspaceId, final String sessionId, final String uuid);

}
