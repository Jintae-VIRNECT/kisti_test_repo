package com.virnect.data.dao.member;

import static com.virnect.data.domain.member.QMember.member;
import static com.virnect.data.domain.member.QRemoteGroup.*;
import static com.virnect.data.domain.member.QRemoteGroupMember.*;
import static com.virnect.data.domain.room.QRoom.room;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.data.domain.member.RemoteGroup;
import org.springframework.util.StringUtils;

public class CustomRemoteGroupRepositoryImpl extends QuerydslRepositorySupport implements CustomRemoteGroupRepository {

	private final JPAQueryFactory query;

	public CustomRemoteGroupRepositoryImpl(JPAQueryFactory query) {
		super(RemoteGroup.class);
		this.query = query;
	}

	@Override
	public List<RemoteGroup> findByWorkspaceIdAndUserId(
		String workspaceId,
		String userId
	) {
		return query
			.selectFrom(remoteGroup)
			.innerJoin(remoteGroup.groupMembers, remoteGroupMember).fetchJoin()
			.where(
				remoteGroup.workspaceId.eq(workspaceId),
				remoteGroup.uuid.eq(userId)
				//includeUserId(userId)
			)
			.orderBy(remoteGroup.groupName.asc())
			.distinct()
			.fetch();
	}

	@Override
	public long findByWorkspaceIdAndUserIdGroupCount(
		String workspaceId,
		String userId
	) {
		return query
			.selectFrom(remoteGroup)
			.where(
				remoteGroup.workspaceId.eq(workspaceId),
				remoteGroup.uuid.eq(userId)
			)
			.distinct()
			.fetchCount();
	}

	@Override
	public RemoteGroup findByWorkspaceIdAndGroupId(
		String workspaceId,
		String groupId
	) {
		return query
			.selectFrom(remoteGroup)
			.where(
				remoteGroup.workspaceId.eq(workspaceId),
				remoteGroup.groupId.eq(groupId)
			)
			.distinct()
			.fetchOne();
	}

	@Override
	public RemoteGroup findByWorkspaceIdAndGroupIdAndUserId(String workspaceId, String groupId, String userId) {
		return query
			.selectFrom(remoteGroup)
			.where(
				remoteGroup.workspaceId.eq(workspaceId),
				remoteGroup.groupId.eq(groupId),
				remoteGroup.uuid.eq(userId)
			)
			.distinct()
			.fetchOne();
	}

	private BooleanExpression includeUserId(String userId) {
		if (StringUtils.isEmpty(userId)) {
			return null;
		}
		List<Long> userRoomIdList = query.selectFrom(remoteGroupMember)
				.select(remoteGroupMember.remoteGroup.id)
				.where(remoteGroupMember.uuid.eq(userId)
				)
				.fetch();
		return remoteGroup.id.in(userRoomIdList);
	}

}
