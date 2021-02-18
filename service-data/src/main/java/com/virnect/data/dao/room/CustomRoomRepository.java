package com.virnect.data.dao.room;

import java.time.LocalDateTime;
import java.util.List;

import com.virnect.data.domain.room.Room;

public interface CustomRoomRepository {
	List<Room> findRoomHistoryInWorkspaceWithDateOrSpecificUserId(LocalDateTime startDate, LocalDateTime endDate, String workspaceId, String userId);

	Room findRoomHistoryByWorkspaceAndSessionId(String workspaceId, String sessionId);
}

 