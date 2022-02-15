package com.virnect.content.dao.contentliveshare;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.virnect.content.domain.LiveShareRoom;
public interface LiveShareRoomCustomRepository {
	LiveShareRoom getActiveRoomByContentUUID(String contentUUID);
	Optional<LiveShareRoom> getActiveRoomById(Long roomId);
}
