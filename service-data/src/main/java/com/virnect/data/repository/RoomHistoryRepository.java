package com.virnect.data.repository;

import com.virnect.data.dao.RoomHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomHistoryRepository extends JpaRepository<RoomHistory, Long> {
    Optional<RoomHistory> findBySessionId(final String sessionId);

    Optional<RoomHistory> findRoomHistoryByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);
    //RoomHistory findByTitleIsContaining(final String title);

    Page<RoomHistory> findByTitleIsContaining(final String title, Pageable pageable);

    List<RoomHistory> findByTitleIsContaining(final String title);
}
