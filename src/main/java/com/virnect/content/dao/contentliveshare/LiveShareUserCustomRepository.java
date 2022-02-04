package com.virnect.content.dao.contentliveshare;

import java.util.List;

import com.virnect.content.dto.rest.LiveShareUserUpdatePushRequest;

public interface LiveShareUserCustomRepository {

	List<LiveShareUserUpdatePushRequest> getActiveUserListByRoomId(Long roomId);
}
