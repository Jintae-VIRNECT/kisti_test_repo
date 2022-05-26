package com.virnect.content.dao.contentliveshare;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.content.domain.LiveShareRoom;

public interface LiveShareRoomRepository extends JpaRepository<LiveShareRoom, Long>,
	LiveShareRoomCustomRepository {
}
