package com.virnect.data.dao.memberhistory;

import static com.virnect.data.domain.member.QMemberHistory.*;
import static com.virnect.data.domain.roomhistory.QRoomHistory.*;
import static com.virnect.data.domain.session.QSessionPropertyHistory.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.data.domain.member.MemberHistory;

@Repository
public class CustomMemberHistoryRepositoryImpl extends QuerydslRepositorySupport implements CustomMemberHistoryRepository{

	private final JPAQueryFactory query;
	private final long EXPIRATION_DATE = 30;

	public CustomMemberHistoryRepositoryImpl(JPAQueryFactory query) {
		super(MemberHistory.class);
		this.query = query;
	}

	/**
	 * 사용자 기록 조회 다이나믹 쿼리
	 * @param workspaceId - 조회될 워크스페이스 식별자
	 * @param userId - 조회될 사용자 정보 식별자
	 * @param pageable - 데이터 페이징 정보
	 * @return - 해당 조건의 사용자 데이터
	 */
	@Override
	public Page<MemberHistory> findByWorkspaceIdAndUuidAndRoomHistoryIsNotNullAndHistoryDeletedFalse(
		String workspaceId,
		String userId,
		boolean paging,
		Pageable pageable
	) {
		/*long offSet = pageable.getOffset();
		int pageSize = pageable.getPageSize();
		if (!paging) {
			offSet = 0;
			pageSize = Integer.MAX_VALUE;
		}
		QueryResults<MemberHistory> queryResult = query
			.selectFrom(memberHistory)
			.innerJoin(memberHistory.roomHistory, roomHistory).fetchJoin()
			.where(
				memberHistory.workspaceId.eq(workspaceId),
				memberHistory.uuid.eq(userId),
				memberHistory.roomHistory.isNotNull(),
				memberHistory.historyDeleted.isFalse()
			)
			.offset(offSet)
			.limit(pageSize)
			.orderBy(memberHistory.createdDate.desc())
			.orderBy()
			.distinct().fetchResults();
		return new PageImpl<>(queryResult.getResults(), pageable, queryResult.getTotal());*/

		JPQLQuery<MemberHistory> queryResult = query
			.selectFrom(memberHistory)
			.innerJoin(memberHistory.roomHistory, roomHistory).fetchJoin()
			.where(
				memberHistory.workspaceId.eq(workspaceId),
				memberHistory.uuid.eq(userId),
				memberHistory.roomHistory.isNotNull(),
				memberHistory.historyDeleted.isFalse(),
				memberHistory.roomHistory.createdDate.between(LocalDateTime.now().minusDays(EXPIRATION_DATE), LocalDateTime.now())
			)
			.orderBy(memberHistory.createdDate.desc())
			.distinct();
		long totalCount = queryResult.fetchCount();
		List<MemberHistory> results;
		if (paging) {
			results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, queryResult).fetch();
		} else {
			results = Objects.requireNonNull(queryResult.fetch());
		}
		return new PageImpl<>(results, pageable, totalCount);
	}

	/**
	 * 사용자 기록 조회 다이나믹 쿼리
	 * @param sessionId - 조회될 세션 식별자
	 * @return - 해당 조건의 사용자 데이터
	 */
	@Override
	public List<MemberHistory> findAllBySessionId(String sessionId) {
		return query
			.selectFrom(memberHistory)
			.innerJoin(memberHistory.roomHistory, roomHistory).fetchJoin()
			.where(
				memberHistory.sessionId.eq(sessionId)
			)
			.distinct()
			.fetch();
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

	@Override
	public List<MemberHistory> findByWorkspaceId(String workspaceId) {
		return query
			.selectFrom(memberHistory)
			.innerJoin(memberHistory.roomHistory, roomHistory).fetchJoin()
			.where(
				memberHistory.workspaceId.eq(workspaceId)
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
