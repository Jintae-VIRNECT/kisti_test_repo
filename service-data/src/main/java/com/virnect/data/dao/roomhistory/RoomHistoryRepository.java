package com.virnect.data.dao.roomhistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.data.domain.roomhistory.RoomHistory;

@Repository
public interface RoomHistoryRepository extends JpaRepository<RoomHistory, Long>, CustomRoomHistoryRepository {

}
