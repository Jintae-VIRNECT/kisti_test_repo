package com.virnect.data.dao.roomhistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.data.domain.roomhistory.RoomHistory;

public interface CustomRoomHistoryRepository {

	List<RoomHistory> findRoomHistoryInWorkspaceIdWithDateOrSpecificUserId(LocalDateTime startDate, LocalDateTime endDate, String workspaceId, String userId);

	Optional<RoomHistory> findRoomHistoryByWorkspaceIdAndSessionId(String workspaceId, String sessionId);

	Optional<RoomHistory> findBySessionId(final String sessionId);

	boolean existsByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

	Page<RoomHistory> findRoomBySearch(String workspaceId, String userId, List<String> userIds, String search, Pageable pageable);

	Page<RoomHistory> findRoomByWorkspaceIdAndUserIdCurrent(final String workspaceId, final String userId, boolean paging, Pageable pageable);

	Page<RoomHistory> findMyRoomHistorySpecificUserId(String workspaceId, String userId, boolean paging, Pageable pageable);
}
