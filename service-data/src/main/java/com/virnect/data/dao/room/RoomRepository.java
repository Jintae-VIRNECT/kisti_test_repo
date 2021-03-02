package com.virnect.data.dao.room;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.virnect.data.domain.room.Room;
import com.virnect.data.domain.room.RoomStatus;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room>, CustomRoomRepository {

    //Optional<Room> findBySessionId(final String sessionId);

    /*@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Room r where r.workspaceId = ?1 and r.sessionId = ?2")
    Optional<Room> findRoomByWorkspaceIdAndSessionIdForWrite(final String workspaceId, final String sessionId);*/

    /*@Query("select r from Room r where r.workspaceId = ?1 and r.roomStatus = 0")
    Page<Room> findRoomsByWorkspaceId(final String workspaceId, Pageable pageable);*/

    /*@Query("select r from Room r where r.workspaceId = ?1 and r.roomStatus = 0")
    Page<Room> findRoomByWorkspaceId(final String workspaceId, Pageable pageable);*/

    //List<Room> findByWorkspaceId(final String workspaceId);

    @Modifying
    @Query("delete from Room r where r.workspaceId = ?1 and r.sessionId = ?2")
    void deleteByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    //Optional<Room> findRoomByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    //Optional<Room> findRoomByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    List<Room> findByWorkspaceIdAndTitleIsContaining(final String workspaceId, final String title);

    //Page<Room> findByTitleIsContaining(final String title, Pageable pageable);

    Page<Room> findByWorkspaceIdAndTitleIsContaining(final String workspaceId, final String title, Pageable pageable);

    List<Room> findByWorkspaceIdAndRoomStatusAndActiveDateBetween(String workspaceId, RoomStatus active, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Room> findByWorkspaceIdAndRoomStatus(String workspaceId, RoomStatus active);

    //Room findRoomHistoryByWorkspaceAndSessionId(String workspaceId, String sessionId);

    //List<Room> findByTitleIsContaining(final String title);

    //List<Room> findByNameIsContainingOrRoomTitleIsContaining(final String title);

}
