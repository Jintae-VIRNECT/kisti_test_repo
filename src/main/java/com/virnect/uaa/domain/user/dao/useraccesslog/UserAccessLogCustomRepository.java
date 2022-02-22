package com.virnect.uaa.domain.user.dao.useraccesslog;

import java.util.List;

import com.virnect.uaa.domain.user.domain.User;

public interface UserAccessLogCustomRepository {
	long deleteAllUserAccessLogByUser(User user);

	long deleteAllByUserIn(List<User> users);
}
