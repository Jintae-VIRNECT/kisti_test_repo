package com.virnect.data.dao.room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;

import com.virnect.data.domain.room.Room;

public interface CustomRoomRepository {
	List<Room> findRoomHistoryInWorkspaceWithDateOrSpecificUserId(LocalDateTime startDate, LocalDateTime endDate, String workspaceId, String userId);

	//Room findRoomHistoryByWorkspaceAndSessionId(String workspaceId, String sessionId);

	//Optional<Room> findRoomByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

	Optional<Room> findBySessionId(final String sessionId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Room> findRoomByWorkspaceIdAndSessionIdForWrite(final String workspaceId, final String sessionId);

	//@Query("select r from Room r where r.workspaceId = ?1 and r.roomStatus = 0")
	Page<Room> findRoomByWorkspaceId(final String workspaceId, Pageable pageable);

	List<Room> findByWorkspaceId(final String workspaceId);

}

 