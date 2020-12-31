package com.virnect.data.repository;

import com.virnect.data.dao.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    Optional<Room> findBySessionId(final String sessionId);

    Optional<Room> findRoomByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Room> findRoomByWorkspaceIdAndSessionIdForWrite(final String workspaceId, final String sessionId);

    //Optional<Room> findRoomByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    @Query("select r from Room r where r.workspaceId = ?1 and r.roomStatus = 0")
    Page<Room> findRoomsByWorkspaceId(final String workspaceId, Pageable pageable);

    @Query("select r from Room r where r.workspaceId = ?1 and r.roomStatus = 0")
    Page<Room> findRoomByWorkspaceId(final String workspaceId, Pageable pageable);

    List<Room> findByWorkspaceId(final String workspaceId);

    List<Room> findByWorkspaceIdAndTitleIsContaining(final String workspaceId, final String title);

    //Page<Room> findByTitleIsContaining(final String title, Pageable pageable);

    Page<Room> findByWorkspaceIdAndTitleIsContaining(final String workspaceId, final String title, Pageable pageable);

    @Modifying
    @Query("delete from Room r where r.workspaceId = ?1 and r.sessionId = ?2")
    void deleteByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    //List<Room> findByTitleIsContaining(final String title);

    //List<Room> findByNameIsContainingOrRoomTitleIsContaining(final String title);

}
