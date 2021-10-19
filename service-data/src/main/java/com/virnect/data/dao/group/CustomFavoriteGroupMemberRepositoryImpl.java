package com.virnect.data.dao.group;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.data.domain.group.FavoriteGroupMember;

import static com.virnect.data.domain.group.QFavoriteGroupMember.*;

import java.util.Optional;

public class CustomFavoriteGroupMemberRepositoryImpl extends QuerydslRepositorySupport implements CustomFavoriteGroupMemberRepository {

	private final JPAQueryFactory query;

	public CustomFavoriteGroupMemberRepositoryImpl(JPAQueryFactory query) {
		super(FavoriteGroupMember.class);
		this.query = query;
	}

	@Override
	public Optional<FavoriteGroupMember> findByFavoriteGroupIdAndUuid(Long favoriteGroupId, String uuid) {
		return Optional.ofNullable(
			query
				.selectFrom(favoriteGroupMember)
				.where(
					favoriteGroupMember.favoriteGroup.id.eq(favoriteGroupId),
					favoriteGroupMember.uuid.eq(uuid)
				)
				.fetchFirst());
	}

	@Override
	public void deleteByFavoriteGroupIdAndUuid(Long favoriteGroupId, String uuid) {
		query.update(favoriteGroupMember).where(
			favoriteGroupMember.favoriteGroup.id.eq(favoriteGroupId),
			favoriteGroupMember.uuid.eq(uuid)
		);
	}
}
