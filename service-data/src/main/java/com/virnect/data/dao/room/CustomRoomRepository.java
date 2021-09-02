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

	Optional<Room> findBySessionId(final String sessionId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Room> findRoomByWorkspaceIdAndSessionIdForWrite(final String workspaceId, final String sessionId);

	Optional<Room> findRoomByWorkspaceIdAndSessionIdNotInEvictedMember(final String workspaceId, final String sessionId);

	Page<Room> findMyRoomSpecificUserId(final String workspaceId, final String userId, boolean paging, Pageable pageable);

	Page<Room> findMyRoomSpecificUserIdBySearch(String workspaceId, String userId, List<String> userIds, String search, Pageable pageable);

	Optional<Room> findOpenRoomByGuest(String workspaceId, String sessionId);
}

 