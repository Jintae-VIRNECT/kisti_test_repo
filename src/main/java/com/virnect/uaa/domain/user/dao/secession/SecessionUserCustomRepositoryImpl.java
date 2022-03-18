package com.virnect.uaa.domain.user.dao.secession;

import static com.virnect.uaa.domain.user.domain.QSecessionUser.*;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.uaa.domain.user.domain.SecessionUser;

public class SecessionUserCustomRepositoryImpl implements SecessionUserCustomRepository {
	private final JPAQueryFactory query;

	public SecessionUserCustomRepositoryImpl(JPAQueryFactory query) {
		this.query = query;
	}

	@Override
	public List<SecessionUser> findSecessionUserInformationByUUIDListWithSearchQuery(
		List<String> uuidList,
		String search
	) {
		return query.selectFrom(secessionUser)
			.where(
				secessionUser.userUUID.in(uuidList),
				secessionUser.nickName.contains(search)
			)
			.fetch();
	}
}
