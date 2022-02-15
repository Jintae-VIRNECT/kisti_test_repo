package com.virnect.content.dao.contentliveshare;

import static com.virnect.content.domain.QLiveShareUser.*;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.content.domain.ActiveOrInactive;
import com.virnect.content.domain.LiveShareUser;

public class LiveShareUserCustomRepositoryImpl extends QuerydslRepositorySupport
	implements LiveShareUserCustomRepository {

	public LiveShareUserCustomRepositoryImpl(
	) {
		super(LiveShareUser.class);
	}

	@Override
	public List<LiveShareUser> getActiveUserListByRoomId(Long roomId) {
		return
			from(liveShareUser).select(liveShareUser)
				.where(liveShareUser.status.eq(ActiveOrInactive.ACTIVE), liveShareUser.roomId.eq(roomId))
				.orderBy(liveShareUser.createdDate.asc())
				.fetch();
	}

	@Override
	public long countActiveUserByRoomId(Long roomId) {
		return
			from(liveShareUser).select(liveShareUser)
				.where(liveShareUser.status.eq(ActiveOrInactive.ACTIVE), liveShareUser.roomId.eq(roomId))
				.fetchCount();
	}
}