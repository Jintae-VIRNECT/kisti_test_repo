package com.virnect.data.dao.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.data.domain.room.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, CustomRoomRepository {

}
