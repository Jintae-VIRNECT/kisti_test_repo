package com.virnect.data.dao.group;

import static com.virnect.data.domain.group.QRemoteGroup.*;
import static com.virnect.data.domain.group.QRemoteGroupMember.*;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.data.domain.group.RemoteGroup;

public class CustomRemoteGroupRepositoryImpl extends QuerydslRepositorySupport implements CustomRemoteGroupRepository {

	private final JPAQueryFactory query;

	public CustomRemoteGroupRepositoryImpl(JPAQueryFactory query) {
		super(RemoteGroup.class);
		this.query = query;
	}

	@Override
	public List<RemoteGroup> findByWorkspaceId(
		String workspaceId
	) {
		return query
			.selectFrom(remoteGroup)
			.innerJoin(remoteGroup.groupMembers, remoteGroupMember).fetchJoin()
			.where(
				remoteGroup.workspaceId.eq(workspaceId)
			)
			.orderBy(remoteGroup.groupName.asc())
			.distinct()
			.fetch();
	}

	@Override
	public List<RemoteGroup> findByWorkspaceIdAndUserIdArray(String workspaceId, List<String> userIds) {
		return query
			.selectFrom(remoteGroup)
			.innerJoin(remoteGroup.groupMembers, remoteGroupMember).fetchJoin()
			.where(
				remoteGroup.workspaceId.eq(workspaceId),
				includeUserIds(userIds)
			)
			.orderBy(remoteGroup.groupName.asc())
			.distinct()
			.fetch();
	}

	@Override
	public long findByWorkspaceIdGroupCount(
		String workspaceId
	) {
		return query
			.selectFrom(remoteGroup)
			.where(
				remoteGroup.workspaceId.eq(workspaceId)
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
				remoteGroup.groupId.eq(groupId)
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

	private BooleanExpression includeUserIds(List<String> userIds) {
		List<Long> userRoomIdList = query.selectFrom(remoteGroupMember)
			.select(remoteGroupMember.remoteGroup.id)
			.where(remoteGroupMember.uuid.in(userIds))
			.fetch();
		return remoteGroup.id.in(userRoomIdList);
	}

}
