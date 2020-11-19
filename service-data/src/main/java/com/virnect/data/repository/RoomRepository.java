package com.virnect.data.repository;

import com.virnect.data.dao.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findBySessionId(final String sessionId);

    Optional<Room> findRoomByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    //Optional<Room> findRoomByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    List<Room> findByWorkspaceId(final String workspaceId);

    List<Room> findByWorkspaceIdAndTitleIsContaining(final String workspaceId, final String title);

    //Page<Room> findByTitleIsContaining(final String title, Pageable pageable);

    Page<Room> findByWorkspaceIdAndTitleIsContaining(final String workspaceId, final String title, Pageable pageable);

    //List<Room> findByTitleIsContaining(final String title);

    //List<Room> findByNameIsContainingOrRoomTitleIsContaining(final String title);

}
