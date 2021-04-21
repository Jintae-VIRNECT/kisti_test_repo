package com.virnect.uaa.domain.user.dao.useraccesslog;

import com.virnect.uaa.domain.user.domain.User;

public interface UserAccessLogCustomRepository {
	long deleteAllUserAccessLogByUser(User user);
}
