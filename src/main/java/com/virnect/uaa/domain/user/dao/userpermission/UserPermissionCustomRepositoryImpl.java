package com.virnect.uaa.domain.user.dao.userpermission;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.uaa.domain.user.domain.QUserPermission;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.domain.UserPermission;

public class UserPermissionCustomRepositoryImpl extends QuerydslRepositorySupport
	implements UserPermissionCustomRepository {

	public UserPermissionCustomRepositoryImpl() {
		super(UserPermission.class);
	}

	@Override
	public long deleteAllUserPermissionByUser(User user) {
		QUserPermission qUserPermission = QUserPermission.userPermission;
		return delete(qUserPermission).where(qUserPermission.user.eq(user)).execute();
	}
}
