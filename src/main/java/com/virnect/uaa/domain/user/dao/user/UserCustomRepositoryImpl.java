package com.virnect.uaa.domain.user.dao.user;

import static com.virnect.uaa.domain.user.domain.QPermission.*;
import static com.virnect.uaa.domain.user.domain.QUser.*;
import static com.virnect.uaa.domain.user.domain.QUserPermission.*;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.uaa.domain.user.domain.User;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.10
 */
public class UserCustomRepositoryImpl extends QuerydslRepositorySupport implements UserCustomRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public UserCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		super(User.class);
		this.jpaQueryFactory = jpaQueryFactory;
	}

	@Override
	public List<User> findUserByNameAndRecoveryEmailOrInternationalNumberAndMobile(
		String name, String recoveryEmail, String mobile
	) {
		return jpaQueryFactory.selectFrom(user)
			.where(
				equalName(name), equalRecoveryEmail(recoveryEmail), equalInternationalNumber(mobile),
				equalMobile(mobile)
			)
			.fetch();
	}

	@Override
	public Optional<User> findLoginUserInformationByUserEmail(String email) {
		return Optional.ofNullable(
			jpaQueryFactory.selectFrom(user)
				.innerJoin(user.userPermissionList, userPermission).fetchJoin()
				.innerJoin(userPermission.permission, permission1).fetchJoin()
				.where(user.email.eq(email))
				.distinct().fetchOne());
	}

	private BooleanExpression equalName(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return user.name.eq(name);
	}

	private BooleanExpression equalRecoveryEmail(String email) {
		if (StringUtils.isEmpty(email)) {
			return null;
		}
		return user.recoveryEmail.eq(email);
	}

	private BooleanExpression equalInternationalNumber(String mobile) {
		if (StringUtils.isEmpty(mobile) || mobile.matches("")) {
			return null;
		}
		String internationalNumber = mobile.split("-")[0];
		return user.internationalNumber.eq(internationalNumber);
	}

	private BooleanExpression equalMobile(String mobile) {
		if (StringUtils.isEmpty(mobile)) {
			return null;
		}
		String mobileNumber = mobile.split("-")[1];
		return user.mobile.eq(mobileNumber);
	}
}
