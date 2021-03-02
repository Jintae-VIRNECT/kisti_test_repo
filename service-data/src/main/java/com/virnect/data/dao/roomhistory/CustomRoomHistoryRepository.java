package com.virnect.data.dao.roomhistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.virnect.data.domain.roomhistory.RoomHistory;

public interface CustomRoomHistoryRepository {

	List<RoomHistory> findRoomHistoryInWorkspaceIdWithDateOrSpecificUserId(LocalDateTime startDate, LocalDateTime endDate, String workspaceId, String userId);

	Optional<RoomHistory> findRoomHistoryByWorkspaceIdAndSessionId(String workspaceId, String sessionId);

	Optional<RoomHistory> findBySessionId(final String sessionId);

	boolean existsByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

}
