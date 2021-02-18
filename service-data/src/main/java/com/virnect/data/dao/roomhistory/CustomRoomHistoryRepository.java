package com.virnect.data.dao.roomhistory;

import java.time.LocalDateTime;
import java.util.List;

import com.virnect.data.domain.roomhistory.RoomHistory;

public interface CustomRoomHistoryRepository {
	List<RoomHistory> findRoomHistoryInWorksapceWithDateOrSpecificUserId(LocalDateTime startDate, LocalDateTime endDate, String workspaceId, String userId);

	RoomHistory findRoomHistoryByWorksapceAndSessionId(String workspaceId, String sessionId);

}
