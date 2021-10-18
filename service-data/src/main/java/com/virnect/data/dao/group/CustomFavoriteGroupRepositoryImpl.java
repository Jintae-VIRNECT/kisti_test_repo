package com.virnect.data.dao.group;

import static com.virnect.data.domain.group.QFavoriteGroup.*;
import static com.virnect.data.domain.group.QFavoriteGroupMember.*;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.data.domain.group.FavoriteGroup;

public class CustomFavoriteGroupRepositoryImpl extends QuerydslRepositorySupport implements CustomFavoriteGroupRepository {

	private final JPAQueryFactory query;

	public CustomFavoriteGroupRepositoryImpl(JPAQueryFactory query) {
		super(FavoriteGroup.class);
		this.query = query;
	}
	@Override
	public long findByWorkspaceIdAndUserIdGroupCount(String workspaceId, String userId) {
		return query
			.selectFrom(favoriteGroup)
			.where(
				favoriteGroup.workspaceId.eq(workspaceId),
				favoriteGroup.uuid.eq(userId)
			)
			.distinct()
			.fetchCount();
	}

	@Override
	public List<FavoriteGroup> findByWorkspaceIdAndUserIdAndIncludeOneself(
		String workspaceId,
		String userId,
		boolean includeOneself
	) {
		return query
			.selectFrom(favoriteGroup)
			.innerJoin(favoriteGroup.favoriteGroupMembers, favoriteGroupMember).fetchJoin()
			.where(
				favoriteGroup.workspaceId.eq(workspaceId),
				favoriteGroup.uuid.eq(userId),
				favoriteGroupMember.deleted.isFalse(),
				includeOneself(userId, includeOneself)
			)
			.orderBy(favoriteGroup.groupName.asc())
			.distinct()
			.fetch();
	}

	@Override
	public Optional<FavoriteGroup> findByWorkspaceIdAndUserIdAndGroupId(String workspaceId, String userId, String groupId) {
		return Optional.ofNullable(
			query
			.selectFrom(favoriteGroup)
			.innerJoin(favoriteGroup.favoriteGroupMembers, favoriteGroupMember).fetchJoin()
			.where(
				favoriteGroup.workspaceId.eq(workspaceId),
				favoriteGroup.uuid.eq(userId),
				favoriteGroup.groupId.eq(groupId),
				favoriteGroupMember.deleted.isFalse()
			)
			.fetchFirst());
	}

	@Override
	public Optional<FavoriteGroup> findByWorkspaceIdAndUserIdAndGroupIdAndIncludeOneself(
		String workspaceId,
		String groupId,
		String userId,
		boolean includeOneself
	) {
		return Optional.ofNullable(
			query
			.selectFrom(favoriteGroup)
			.innerJoin(favoriteGroup.favoriteGroupMembers, favoriteGroupMember).fetchJoin()
			.where(
				favoriteGroup.workspaceId.eq(workspaceId),
				favoriteGroup.uuid.eq(userId),
				favoriteGroup.groupId.eq(groupId),
				favoriteGroupMember.deleted.isFalse(),
				includeOneself(userId, includeOneself)
			)
			.fetchFirst());
	}

	private BooleanExpression includeUserId(String userId) {
		if (StringUtils.isEmpty(userId)) {
			return null;
		}
		List<Long> includedFavoriteGroupIds = query.selectFrom(favoriteGroupMember)
			.innerJoin(favoriteGroup.favoriteGroupMembers, favoriteGroupMember).fetchJoin()
			.select(favoriteGroupMember.favoriteGroup.id)
			.where(favoriteGroupMember.uuid.eq(userId)
			).fetch();
		return favoriteGroup.id.in(includedFavoriteGroupIds);
	}

	private BooleanExpression includeOneself(String userId, boolean includeOneself) {
		if (includeOneself) {
			return null;
		}
		return favoriteGroupMember.uuid.notIn(userId);
	}

}
