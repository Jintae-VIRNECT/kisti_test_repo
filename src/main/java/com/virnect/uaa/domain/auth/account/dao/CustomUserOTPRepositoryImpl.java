package com.virnect.uaa.domain.auth.account.dao;

import static com.virnect.uaa.domain.auth.account.domain.QUserOTP.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class CustomUserOTPRepositoryImpl implements CustomUserOTPRepository {
	private final JPAQueryFactory query;

	public CustomUserOTPRepositoryImpl(JPAQueryFactory query) {
		this.query = query;
	}

	@Override
	public long deleteAllByEmailIn(List<String> emailList) {
		return query.delete(userOTP).where(userOTP.email.in(emailList)).execute();
	}
}
