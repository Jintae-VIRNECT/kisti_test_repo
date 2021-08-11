package com.virnect.data.dao.memberhistory;

import static com.virnect.data.domain.member.QMemberHistory.*;
import static com.virnect.data.domain.roomhistory.QRoomHistory.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.data.domain.member.MemberHistory;

@Repository
public class CustomMemberHistoryRepositoryImpl extends QuerydslRepositorySupport implements CustomMemberHistoryRepository{

	private final JPAQueryFactory query;

	public CustomMemberHistoryRepositoryImpl(JPAQueryFactory query) {
		super(MemberHistory.class);
		this.query = query;
	}

	@Override
	public List<MemberHistory> findByWorkspaceIdAndUuid(String workspaceId, String userId) {
		return query
			.selectFrom(memberHistory)
			.innerJoin(memberHistory.roomHistory, roomHistory).fetchJoin()
			.where(
				memberHistory.workspaceId.eq(workspaceId),
				memberHistory.uuid.eq(userId)
			)
			.distinct()
			.fetch();
	}

	@Override
	public Optional<MemberHistory> findByWorkspaceIdAndSessionIdAndUuid(
		String workspaceId, String sessionId, String uuid
	) {
		return Optional.ofNullable(
			query.selectFrom(memberHistory)
			.innerJoin(memberHistory.roomHistory, roomHistory).fetchJoin()
			.where(
				memberHistory.workspaceId.eq(workspaceId),
				memberHistory.sessionId.eq(sessionId),
				memberHistory.uuid.eq(uuid)
			)
			.distinct()
			.fetchOne());
	}

	@Override
	public List<MemberHistory> findAllByUuid(String userId) {
		return query
			.selectFrom(memberHistory)
			.innerJoin(memberHistory.roomHistory, roomHistory).fetchJoin()
			.where(
				memberHistory.uuid.eq(userId)
			)
			.distinct()
			.fetch();
	}

	@Override
	public List<MemberHistory> findByWorkspaceIdAndRoomHistoryIsNotNullAndRoomHistory_ActiveDateBetween(
		String workspaceId, LocalDateTime startDate, LocalDateTime endDate
	) {
		return query
			.selectFrom(memberHistory)
			.innerJoin(memberHistory.roomHistory, roomHistory).fetchJoin()
			.where(
				memberHistory.workspaceId.eq(workspaceId),
				memberHistory.roomHistory.isNotNull(),
				betweenDate(startDate, endDate)
			)
			.distinct()
			.fetch();
	}

	/**
	 * 기간 검색 다이나믹 쿼리
	 * @param startDate - 검색 시작 일자
	 * @param endDate - 검색 종료 일자
	 * @return - 기간 검색 조건 쿼리
	 */
	private BooleanExpression betweenDate(LocalDateTime startDate, LocalDateTime endDate) {
		if (startDate == null || endDate == null) {
			return null;
		}
		return roomHistory.unactiveDate.between(startDate, endDate.plusDays(1).minusSeconds(1));
	}

}
