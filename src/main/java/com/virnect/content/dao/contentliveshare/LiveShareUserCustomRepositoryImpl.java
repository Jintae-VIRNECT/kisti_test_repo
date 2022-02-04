package com.virnect.content.dao.contentliveshare;

import static com.virnect.content.domain.QLiveShareUser.*;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.content.domain.ActiveOrInactive;
import com.virnect.content.domain.LiveShareUser;
import com.virnect.content.dto.rest.LiveShareUserUpdatePushRequest;
import com.virnect.content.dto.rest.QLiveShareUserUpdatePushRequest;

public class LiveShareUserCustomRepositoryImpl extends QuerydslRepositorySupport
	implements LiveShareUserCustomRepository {

	public LiveShareUserCustomRepositoryImpl(
	) {
		super(LiveShareUser.class);
	}

	@Override
	public List<LiveShareUserUpdatePushRequest> getActiveUserListByRoomId(Long roomId) {
		return from(liveShareUser).select(
				new QLiveShareUserUpdatePushRequest(
					liveShareUser.userNickname,
					liveShareUser.userEmail,
					liveShareUser.userUUID,
					liveShareUser.userRole,
					liveShareUser.createdDate
				))
			.where(liveShareUser.status.eq(ActiveOrInactive.ACTIVE), liveShareUser.roomId.eq(roomId))
			.fetch();
	}
}