package com.virnect.data.dao.member;

import static com.virnect.data.domain.member.QMember.*;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.data.domain.member.Member;
import com.virnect.data.domain.member.MemberHistory;
import com.virnect.data.domain.member.MemberType;

public class CustomMemberRepositoryImpl  extends QuerydslRepositorySupport implements CustomMemberRepository {

	private final JPAQueryFactory query;
	private final long EXPIRATION_DATE = 30;

	public CustomMemberRepositoryImpl(JPAQueryFactory query) {
		super(MemberHistory.class);
		this.query = query;
	}

	@Override
	public Member findRoomLeaderBySessionId(String workspaceId, String sessionId) {
		return query
			.selectFrom(member)
			.where(
				member.workspaceId.eq(workspaceId),
				member.sessionId.eq(sessionId),
				member.memberType.eq(MemberType.LEADER)
			)
			.distinct()
			.fetchOne();
	}
}