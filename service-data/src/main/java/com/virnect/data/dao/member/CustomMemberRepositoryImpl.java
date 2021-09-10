package com.virnect.data.dao.member;

import static com.virnect.data.domain.member.QMember.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.data.domain.member.Member;
import com.virnect.data.domain.member.MemberHistory;
import com.virnect.data.domain.member.MemberType;

public class CustomMemberRepositoryImpl  extends QuerydslRepositorySupport implements CustomMemberRepository {

	private final JPAQueryFactory query;

	public CustomMemberRepositoryImpl(JPAQueryFactory query) {
		super(MemberHistory.class);
		this.query = query;
	}

	@Override
	public Optional<Member> findRoomLeaderBySessionId(String workspaceId, String sessionId) {
		return Optional.ofNullable(
			query
			.selectFrom(member)
			.where(
				member.workspaceId.eq(workspaceId),
				member.sessionId.eq(sessionId),
				member.memberType.eq(MemberType.LEADER)
			)
			.fetchFirst());
	}

	@Override
	public Optional<Member> findBySessionIdAndUuid(String sessionId, String uuid) {
		return Optional.ofNullable(
			query
			.selectFrom(member)
			.where(
				member.sessionId.eq(sessionId),
				member.uuid.eq(uuid)
			)
			.fetchFirst());
	}

	@Override
	public List<Member> findByWorkspaceIdAndUuid(String workspaceId, String uuid) {
		return query
			.selectFrom(member)
			.where(
				member.workspaceId.eq(workspaceId),
				member.uuid.eq(uuid)
			)
			.distinct()
			.fetch();
	}

}