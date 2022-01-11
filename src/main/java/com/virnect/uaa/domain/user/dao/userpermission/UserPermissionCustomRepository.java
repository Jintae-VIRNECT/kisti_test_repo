package com.virnect.uaa.domain.user.dao.userpermission;

import java.util.List;

import com.virnect.uaa.domain.user.domain.User;

public interface UserPermissionCustomRepository {
	long deleteAllUserPermissionByUser(User user);
	long deleteAllByUserIn(List<User> users);
}
