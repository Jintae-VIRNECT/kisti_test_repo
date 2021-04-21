package com.virnect.uaa.domain.user.dao.useraccesslog;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.domain.UserAccessLog;

public class UserAccessLogCustomRepositoryImpl extends QuerydslRepositorySupport
	implements UserAccessLogCustomRepository {

	public UserAccessLogCustomRepositoryImpl() {
		super(UserAccessLog.class);
	}

	@Override
	public long deleteAllUserAccessLogByUser(User user) {
		QUserAccessLog qUserAccessLog = QUserAccessLog.userAccessLog;
		return delete(qUserAccessLog).where(qUserAccessLog.user.eq(user)).execute();
	}
}
