package com.virnect.serviceserver.gateway.dao;

import com.virnect.serviceserver.gateway.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findBySessionId(final String sessionId);

    Page<Room> findByTitleIsContaining(final String title, Pageable pageable);

    List<Room> findByTitleIsContaining(final String title);

    //List<Room> findByNameIsContainingOrRoomTitleIsContaining(final String title);

}
