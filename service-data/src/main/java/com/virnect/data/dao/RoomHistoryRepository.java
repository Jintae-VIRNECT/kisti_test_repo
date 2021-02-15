package com.virnect.data.dao;

import com.virnect.data.domain.room.RoomHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomHistoryRepository extends JpaRepository<RoomHistory, Long>, JpaSpecificationExecutor<RoomHistory> {
    Optional<RoomHistory> findBySessionId(final String sessionId);

    Optional<RoomHistory> findRoomHistoryByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    //Page<RoomHistory> findRoomHistoriesByWorkspaceIdAndMemberHistoriesIsNotNull(final String workspaceId, Pageable pageable);

    @Query("select r from RoomHistory r where r.workspaceId = ?1")
    Page<RoomHistory> findRoomHistoryByWorkspaceId(final String workspaceId, Pageable pageable);

    Page<RoomHistory> findByWorkspaceIdAndTitleIsContaining(final String workspaceId, final String title, Pageable pageable);

    List<RoomHistory> findByTitleIsContaining(final String title);

    boolean existsByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);
}
