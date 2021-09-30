package com.virnect.uaa.domain.user.dao.userpermission;

import static com.virnect.uaa.domain.user.domain.QUserPermission.*;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.uaa.domain.user.domain.User;

public class UserPermissionCustomRepositoryImpl implements UserPermissionCustomRepository {
	private final JPAQueryFactory query;

	public UserPermissionCustomRepositoryImpl(JPAQueryFactory query) {
		this.query = query;
	}

	@Override
	public long deleteAllUserPermissionByUser(User user) {
		return query.delete(userPermission).where(userPermission.user.eq(user)).execute();
	}
}
