package com.virnect.uaa.domain.user.dao.useraccesslog;

import static com.virnect.uaa.domain.user.domain.QUserAccessLog.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.uaa.domain.user.domain.User;

public class UserAccessLogCustomRepositoryImpl implements UserAccessLogCustomRepository {
	private final JPAQueryFactory query;

	public UserAccessLogCustomRepositoryImpl(JPAQueryFactory query) {
		this.query = query;
	}

	@Override
	public long deleteAllUserAccessLogByUser(User user) {
		return query.delete(userAccessLog).where(userAccessLog.user.eq(user)).execute();
	}

	@Override
	public long deleteAllByUserIn(List<User> users) {
		return query
			.delete(userAccessLog)
			.where(userAccessLog.user.in(users))
			.execute();
	}
}
