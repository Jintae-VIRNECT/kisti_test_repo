package com.virnect.uaa.domain.auth.account.dao;

import static com.virnect.uaa.domain.user.domain.QPermission.*;
import static com.virnect.uaa.domain.user.domain.QUser.*;
import static com.virnect.uaa.domain.user.domain.QUserPermission.*;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.uaa.domain.user.domain.User;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<User> findLoginUserInformationByUserEmail(String email) {
		return Optional.ofNullable(
			jpaQueryFactory.selectFrom(user)
				.innerJoin(user.userPermissionList, userPermission).fetchJoin()
				.innerJoin(userPermission.permission, permission1).fetchJoin()
				.where(user.email.eq(email))
				.distinct().fetchOne()
		);
	}
}
