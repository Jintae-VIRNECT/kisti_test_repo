package com.virnect.content.dao.contentliveshare;

import java.util.List;
import java.util.Optional;

import com.virnect.content.domain.LiveShareUser;

public interface LiveShareUserCustomRepository {
	List<LiveShareUser> getActiveUserListByRoomId(Long roomId);

	long countActiveUserByRoomId(Long roomId);

	Optional<LiveShareUser> getActiveUserByRoomIdAndUserUUID(Long roomId, String userUUID);
}
