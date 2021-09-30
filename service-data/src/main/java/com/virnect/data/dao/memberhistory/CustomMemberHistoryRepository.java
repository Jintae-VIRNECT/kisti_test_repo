package com.virnect.data.dao.memberhistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.virnect.data.domain.member.MemberHistory;

public interface CustomMemberHistoryRepository {

	List<MemberHistory> findByWorkspaceIdAndUuid(final String workspaceId, final String userId);

	Optional<MemberHistory> findByWorkspaceIdAndSessionIdAndUuid(final String workspaceId, final String sessionId, final String uuid);

	List<MemberHistory> findAllByUuid(final String userId);

	List<MemberHistory> findByWorkspaceIdAndRoomHistoryIsNotNullAndRoomHistory_ActiveDateBetween(String workspaceId, LocalDateTime startDate, LocalDateTime endDate);

}
