package com.virnect.content.dao.contentliveshare;

import java.util.List;

import com.virnect.content.domain.LiveShareUser;

public interface LiveShareUserCustomRepository {
	List<LiveShareUser> getActiveUserListByRoomId(Long roomId);
	long countActiveUserByRoomId(Long roomId);
}
