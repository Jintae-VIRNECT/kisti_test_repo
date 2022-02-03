package com.virnect.content.dao.contentliveshare;

import static com.virnect.content.domain.QLiveShareRoom.*;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.content.domain.ActiveOrInactive;
import com.virnect.content.domain.LiveShareRoom;

public class LiveShareRoomCustomRepositoryImpl extends QuerydslRepositorySupport
	implements LiveShareRoomCustomRepository {

	public LiveShareRoomCustomRepositoryImpl(
	) {
		super(LiveShareRoom.class);
	}

	@Override
	public LiveShareRoom getActiveRoomByContentUUID(String contentUUID) {
		return from(liveShareRoom).select(liveShareRoom)
			.where(liveShareRoom.contentUUID.eq(contentUUID), liveShareRoom.status.eq(ActiveOrInactive.ACTIVE))
			.fetchOne();
	}
}
