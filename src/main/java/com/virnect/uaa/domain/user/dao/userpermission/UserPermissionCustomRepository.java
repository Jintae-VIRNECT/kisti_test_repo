package com.virnect.uaa.domain.user.dao.userpermission;

import com.virnect.uaa.domain.user.domain.User;

public interface UserPermissionCustomRepository {
	long deleteAllUserPermissionByUser(User user);
}
