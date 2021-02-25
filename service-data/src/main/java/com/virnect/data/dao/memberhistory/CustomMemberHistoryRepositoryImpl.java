package com.virnect.data.dao.memberhistory;

import static com.virnect.data.domain.member.QMemberHistory.*;
import static com.virnect.data.domain.roomhistory.QRoomHistory.*;
import static com.virnect.data.domain.session.QSessionPropertyHistory.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.data.domain.member.MemberHistory;

@RequiredArgsConstructor
public class CustomMemberHistoryRepositoryImpl implements CustomMemberHistoryRepository{

	private final JPAQueryFactory query;

	/**
	 * 사용자 기록 조회 다이나믹 쿼리
	 * @param workspaceId - 조회될 워크스페이스 식별자
	 * @param userId - 조회될 사용자 정보 식별자
	 * @param pageable - 데이터 페이징 정보
	 * @return - 해당 조건의 사용자 데이터
	 */
	@Override
	public Page<MemberHistory> findByWorkspaceIdAndUuidAndRoomHistoryIsNotNullAndHistoryDeletedFalse(
		String workspaceId, String userId, Pageable pageable
	) {
		QueryResults<MemberHistory> result = query
			.selectFrom(memberHistory)
			.innerJoin(roomHistory.memberHistories, memberHistory).fetchJoin()
			.where(
				memberHistory.workspaceId.eq(workspaceId),
				memberHistory.uuid.eq(userId)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();
		return new PageImpl<>(result.getResults(), pageable, result.getTotal());
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
			.innerJoin(roomHistory.memberHistories, memberHistory).fetchJoin()
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
			.innerJoin(roomHistory.memberHistories, memberHistory).fetchJoin()
			.where(
				memberHistory.workspaceId.eq(workspaceId),
				memberHistory.uuid.eq(userId)
			)
			.distinct()
			.fetch();
	}

	@Override
	public MemberHistory findByWorkspaceIdAndSessionIdAndUuid(
		String workspaceId, String sessionId, String uuid
	) {
		return query
			.selectFrom(memberHistory)
			.innerJoin(roomHistory.memberHistories, memberHistory).fetchJoin()
			.where(
				memberHistory.workspaceId.eq(workspaceId),
				memberHistory.sessionId.eq(sessionId),
				memberHistory.uuid.eq(uuid)
			)
			.distinct()
			.fetchOne();
	}
}
