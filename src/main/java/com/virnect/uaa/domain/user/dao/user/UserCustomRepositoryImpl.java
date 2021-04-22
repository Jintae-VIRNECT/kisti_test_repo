package com.virnect.uaa.domain.user.dao.user;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.uaa.domain.user.domain.QUser;
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
		QUser user = QUser.user;
		return jpaQueryFactory.selectFrom(user)
			.where(
				equalName(name), equalRecoveryEmail(recoveryEmail), equalInternationalNumber(mobile),
				equalMobile(mobile)
			)
			.fetch();
	}

	private BooleanExpression equalName(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return QUser.user.name.eq(name);
	}

	private BooleanExpression equalRecoveryEmail(String email) {
		if (StringUtils.isEmpty(email)) {
			return null;
		}
		return QUser.user.recoveryEmail.eq(email);
	}

	private BooleanExpression equalInternationalNumber(String mobile) {
		if (StringUtils.isEmpty(mobile) || mobile.matches("")) {
			return null;
		}
		String internationalNumber = mobile.split("-")[0];
		return QUser.user.internationalNumber.eq(internationalNumber);
	}

	private BooleanExpression equalMobile(String mobile) {
		if (StringUtils.isEmpty(mobile)) {
			return null;
		}
		String mobileNumber = mobile.split("-")[1];
		return QUser.user.mobile.eq(mobileNumber);
	}
}
