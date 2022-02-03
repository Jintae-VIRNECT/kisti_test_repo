package com.virnect.content.dao.contentliveshare;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.content.domain.LiveShareUser;

public class LiveShareUserCustomRepositoryImpl extends QuerydslRepositorySupport
	implements LiveShareUserCustomRepository {

	public LiveShareUserCustomRepositoryImpl(
	) {
		super(LiveShareUser.class);
	}
}