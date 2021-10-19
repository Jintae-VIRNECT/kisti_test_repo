package com.virnect.data.dao.group;

import static com.virnect.data.domain.group.QRemoteGroupMember.*;

import java.util.Optional;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.data.domain.group.RemoteGroupMember;

public class CustomRemoteGroupMemberRepositoryImpl extends QuerydslRepositorySupport implements
	CustomRemoteGroupMemberRepository {

	private final JPAQueryFactory query;

	public CustomRemoteGroupMemberRepositoryImpl(JPAQueryFactory query) {
		super(RemoteGroupMember.class);
		this.query = query;
	}

	@Override
	public Optional<RemoteGroupMember> findByRemoteGroupIdAndUuid(Long remoteGroupId, String uuid) {
		return Optional.ofNullable(
			query
				.selectFrom(remoteGroupMember)
				.where(
					remoteGroupMember.remoteGroup.id.eq(remoteGroupId),
					remoteGroupMember.uuid.eq(uuid)
				)
				.fetchFirst());
	}

	@Override
	public void deleteByRemoteGroupIdAndUuid(Long remoteGroupId, String uuid) {
		query.delete(remoteGroupMember).where(
			remoteGroupMember.remoteGroup.id.eq(remoteGroupId),
			remoteGroupMember.uuid.eq(uuid)
		);
	}
}
