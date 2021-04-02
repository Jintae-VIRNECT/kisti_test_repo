package com.virnect.data.dao.roomhistory;

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
import org.springframework.util.StringUtils;

import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.data.domain.member.MemberType;
import com.virnect.data.domain.roomhistory.RoomHistory;
import com.virnect.data.domain.session.SessionType;

@Repository
public class CustomRoomHistoryRepositoryImpl extends QuerydslRepositorySupport implements CustomRoomHistoryRepository {

	private final JPAQueryFactory query;
	private final long EXPIRATION_DATE = 30;

	public CustomRoomHistoryRepositoryImpl(JPAQueryFactory query) {
		super(RoomHistory.class);
		this.query = query;
	}

	@Override
	public List<RoomHistory> findRoomHistoryInWorkspaceIdWithDateOrSpecificUserId(
		LocalDateTime startDate, LocalDateTime endDate, String workspaceId, String userId
	) {
		return query
			.selectFrom(roomHistory)
			.innerJoin(roomHistory.memberHistories, memberHistory).fetchJoin()
			.innerJoin(roomHistory.sessionPropertyHistory, sessionPropertyHistory).fetchJoin()
			.where(
				roomHistory.workspaceId.eq(workspaceId),
				includeMemberHistoryUser(userId),
				betweenDate(startDate, endDate)
			)
			.distinct()
			.fetch();
	}

	/**
	 * 협업 검색 다이나믹 쿼리
	 * @param workspaceId - 조회 대상 워크스페이스
	 * @param sessionId - 조회 대상 세션
	 * @return - 기간 검색 조건 쿼리
	 */
	@Override
	public Optional<RoomHistory> findRoomHistoryByWorkspaceIdAndSessionId(
		String workspaceId,
		String sessionId
	) {
		return Optional.ofNullable(
			query
			.selectFrom(roomHistory)
			.innerJoin(roomHistory.memberHistories, memberHistory).fetchJoin()
			.innerJoin(roomHistory.sessionPropertyHistory, sessionPropertyHistory).fetchJoin()
			.where(
				roomHistory.workspaceId.eq(workspaceId),
				roomHistory.sessionId.eq(sessionId)
			)
			.distinct()
			.fetchOne());
	}

	@Override
	public Optional<RoomHistory> findBySessionId(String sessionId) {
		return Optional.ofNullable(
			query
			.selectFrom(roomHistory)
			.innerJoin(roomHistory.memberHistories, memberHistory).fetchJoin()
			.innerJoin(roomHistory.sessionPropertyHistory, sessionPropertyHistory).fetchJoin()
			.where(
				roomHistory.sessionId.eq(sessionId)
			)
			.distinct()
			.fetchOne());
	}

	@Override
	public boolean existsByWorkspaceIdAndSessionId(String workspaceId, String sessionId) {
		return Optional.ofNullable(query
			.selectFrom(roomHistory)
			.innerJoin(roomHistory.memberHistories, memberHistory).fetchJoin()
			.innerJoin(roomHistory.sessionPropertyHistory, sessionPropertyHistory).fetchJoin()
			.where(
				roomHistory.sessionId.eq(sessionId)
			)
			.distinct()
			.fetchOne()).isPresent();
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

	/**
	 * 사용자 정보 조회 다이나믹 쿼리
	 * @param userId - 조회될 사용자 정보 식별자
	 * @return - 해당 사용자가 참여한 roomHistory 검색 조건 쿼리
	 */
	private BooleanExpression includeMemberHistoryUser(String userId){
		if(StringUtils.isEmpty(userId)){
			return null;
		}
		List<Long> roomHistoryIdList =
			query
			.selectFrom(memberHistory)
			.select(memberHistory.roomHistory.id)
			.where(memberHistory.uuid.eq(userId))
			.fetch();

		return roomHistory.id.in(roomHistoryIdList);
	}

	/**
	 * 검색 기준 협업 히스토리 조회
	 * @param workspaceId 해당 워크스페이스
	 * @param userId 사용자 식별 id
	 * @param userIds 참여 멤버 리스트
 	 * @param search 검색어
	 * @param pageable 페이징 정보
	 * @return - 해당 사용자가 참여한 roomHistory 검색 조건 쿼리
	 */
	@Override
	public Page<RoomHistory> findRoomBySearch(
		String workspaceId,
		String userId,
		List<String> userIds,
		String search,
		Pageable pageable
	) {
		JPQLQuery<RoomHistory> queryResult = query
			.selectFrom(roomHistory)
			.leftJoin(roomHistory.memberHistories, memberHistory).fetchJoin()
			.innerJoin(roomHistory.sessionPropertyHistory, sessionPropertyHistory).fetchJoin()
			.where(
				roomHistory.workspaceId.eq(workspaceId),
				(
					(roomHistory.memberHistories.any().uuid.eq(userId)
						.and(roomHistory.memberHistories.any().historyDeleted.isFalse()))
					.or((roomHistory.memberHistories.any().uuid.in(userIds))
						.and(roomHistory.memberHistories.any().historyDeleted.isFalse()))
				),
				/*.and(roomHistory.memberHistories.any().uuid.in(userId)),
				roomHistory.memberHistories.any().historyDeleted.isFalse(),
				memberHistory.historyDeleted.isFalse(),*/
				roomHistory.isNotNull(),
				includeTitleSearch(search)
			).distinct();
		long totalCount = queryResult.fetchCount();
		List<RoomHistory> result = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, queryResult).fetch();
		return new PageImpl<>(result, pageable, totalCount);
	}

	@Override
	public Page<RoomHistory> findRoomByWorkspaceIdAndUserIdCurrent(
		String workspaceId,
		String userId,
		boolean paging,
		Pageable pageable
	) {
		JPQLQuery<RoomHistory> queryResult = query
			.selectFrom(roomHistory)
			.leftJoin(roomHistory.memberHistories, memberHistory).fetchJoin()
			.innerJoin(roomHistory.sessionPropertyHistory, sessionPropertyHistory).fetchJoin()
			.where(
				roomHistory.workspaceId.eq(workspaceId),
				(
					roomHistory.memberHistories.any().uuid.eq(userId)
					//.and(memberHistory.historyDeleted.isFalse())
				),
				roomHistory.isNotNull()
			)
			.distinct();
		long totalCount = queryResult.fetchCount();
		List<RoomHistory> results;

		if (paging) {
			results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, queryResult).fetch();
		} else {
			results = Objects.requireNonNull(queryResult.fetch());
		}

		return new PageImpl<>(results, pageable, totalCount);
	}

	@Override
	public Page<RoomHistory> findMyRoomHistorySpecificUserId(
		String workspaceId, String userId, boolean paging, Pageable pageable
	) {
		JPQLQuery<RoomHistory> queryResult = query.selectFrom(roomHistory)
			.leftJoin(roomHistory.memberHistories, memberHistory).fetchJoin()
			.innerJoin(roomHistory.sessionPropertyHistory, sessionPropertyHistory).fetchJoin()
			.where(
				roomHistory.workspaceId.eq(workspaceId),
				roomHistory.id.in(includeNotEvicted(workspaceId, userId)),
				roomHistory.isNotNull()
				.or(roomHistory.sessionPropertyHistory.sessionType.eq(SessionType.OPEN))
			).distinct();
		long totalCount = queryResult.fetchCount();
		List<RoomHistory> results;
		if (paging) {
			results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, queryResult).fetch();
		} else {
			results = Objects.requireNonNull(queryResult.fetch());
		}
		return new PageImpl<>(results, pageable, totalCount);
	}

	@Override
	public Page<RoomHistory> findMyRoomHistorySpecificUserIdBySearch(
		String workspaceId, String userId, List<String> userIds, String search, Pageable pageable
	) {
		JPQLQuery<RoomHistory> queryResult = query.selectFrom(roomHistory)
			.leftJoin(roomHistory.memberHistories, memberHistory).fetchJoin()
			.innerJoin(roomHistory.sessionPropertyHistory, sessionPropertyHistory).fetchJoin()
			.where(
				roomHistory.workspaceId.eq(workspaceId),
				roomHistory.id.in(includeUserIdOrUserIds(workspaceId, userId, userIds)),
				roomHistory.isNotNull(),
				includeTitleSearch(search)
			).distinct();
		long totalCount = queryResult.fetchCount();
		List<RoomHistory> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, queryResult).fetch();
		return new PageImpl<>(results, pageable, totalCount);
	}

	/**
	 * 협업 기록 제목 검색 동적 쿼리
	 * @param search - 검색 키워드
	 * @return - 해당 사용자가 참여한 roomHistory 검색 조건 쿼리
	 */
	private BooleanExpression includeTitleSearch(String search){
		if (search == null || search.isEmpty()) {
			return null;
		}
		return roomHistory.title.contains(search);
	}

	/**
	 * 강퇴된 사용자 제외 서브 쿼리
	 * @param userId - 조회될 사용자 정보 식별자
	 * @return - 해당 사용자가 참여한 roomHistory 검색 조건 쿼리
	 */
	private SubQueryExpression<Long> includeNotEvicted(String workspaceId, String userId) {
		return JPAExpressions.select(memberHistory.roomHistory.id)
			.from(memberHistory)
			.where(
				memberHistory.workspaceId.eq(workspaceId),
				roomHistory.id.eq(memberHistory.roomHistory.id),
				memberHistory.uuid.eq(userId),
				memberHistory.historyDeleted.isFalse(),
				memberHistory.memberType.ne(MemberType.SECESSION));
	}

	/**
	 * 사용자 히스토리 검색 서브 쿼리
	 * @param userId - 조회될 사용자 정보 식별자
	 * @return - 해당 사용자가 참여한 roomHistory 검색 조건 쿼리
	 */
	private SubQueryExpression<Long> includeUserIdOrUserIds(String workspaceId, String userId, List<String> userIds) {
		SubQueryExpression<Long> subQueryExpression;
		if (userIds.size() > 0) {
			subQueryExpression = JPAExpressions.select(memberHistory.roomHistory.id)
				.from(memberHistory)
				.where(
					memberHistory.workspaceId.eq(workspaceId),
					roomHistory.id.eq(memberHistory.roomHistory.id),
					memberHistory.uuid.eq(userId).and(memberHistory.historyDeleted.isFalse())
						.or(memberHistory.uuid.eq(userId).and(memberHistory.uuid.in(userIds)).and(memberHistory.historyDeleted.isFalse()))
				);
		} else {
			subQueryExpression = JPAExpressions.select(memberHistory.roomHistory.id)
				.from(memberHistory)
				.where(
					memberHistory.workspaceId.eq(workspaceId),
					roomHistory.id.eq(memberHistory.roomHistory.id).and(memberHistory.historyDeleted.isFalse()),
					memberHistory.uuid.eq(userId)
				);
		}
		return subQueryExpression;
	}

}
