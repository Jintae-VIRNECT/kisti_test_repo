package com.virnect.data.dao.room;

import static com.virnect.data.domain.member.QMember.*;
import static com.virnect.data.domain.room.QRoom.*;
import static com.virnect.data.domain.session.QSessionProperty.*;

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

import com.virnect.data.domain.member.MemberStatus;
import com.virnect.data.domain.room.Room;
import com.virnect.data.domain.room.RoomStatus;
import com.virnect.data.domain.session.SessionType;

@Repository
public class CustomRoomRepositoryImpl extends QuerydslRepositorySupport implements CustomRoomRepository {
	private final JPAQueryFactory query;

	public CustomRoomRepositoryImpl(JPAQueryFactory query) {
		super(Room.class);
		this.query = query;
	}

	/**
	 * 진행 중인 협업 다이나믹 쿼리
	 * @param workspaceId - 조회될 대상 워크스페이스 식별자
	 * @param userId - 참여한 유저 식별자
	 * @param startDate - 시작일자 및 시간
	 * @param endDate - 종료일자 및 시간
	 * @return - 해당 사용자가 참여한 room 검색 조건 쿼리
	 */
	@Override
	public List<Room> findRoomHistoryInWorkspaceWithDateOrSpecificUserId(
		LocalDateTime startDate, LocalDateTime endDate,
		String workspaceId, String userId
	) {
		return query.selectFrom(room)
			.innerJoin(room.members, member).fetchJoin()
			.innerJoin(room.sessionProperty, sessionProperty).fetchJoin()
			.where(
				room.workspaceId.eq(workspaceId),
				includeMemberUser(userId),
				room.roomStatus.eq(RoomStatus.ACTIVE),
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
		return room.activeDate.between(startDate, endDate.plusDays(1).minusSeconds(1));
	}

	/**
	 * 사용자 정보 조회 다이나믹 쿼리
	 * @param userId - 조회될 사용자 정보 식별자
	 * @return - 해당 사용자가 참여한 room 검색 조건 쿼리
	 */
	private BooleanExpression includeMemberUser(String userId) {
		if (StringUtils.isEmpty(userId)) {
			return null;
		}
		List<Long> userRoomIdList = query.selectFrom(member)
			.select(member.room.id)
			.where(member.uuid.eq(userId))
			.fetch();

		return room.id.in(userRoomIdList);
	}

	/**
	 * 진행 중인 협업 다이나믹 쿼리
	 * @param sessionId - 조회될 대상 세션 식별자
	 * @return - 해당 사용자가 참여한 room 검색 조건 쿼리
	 */
	public Optional<Room> findBySessionId(String sessionId) {
		return Optional.ofNullable(
			query.selectFrom(room)
				.innerJoin(room.members, member).fetchJoin()
				.innerJoin(room.sessionProperty, sessionProperty).fetchJoin()
				.where(
					room.sessionId.eq(sessionId)
				)
				.distinct()
				.fetchOne());
	}

	/**
	 * 진행 중인 협업 다이나믹 쿼리
	 *
	 * @param sessionId - 조회될 대상 세션 식별자
	 * @return - 해당 사용자가 참여한 room 검색 조건 쿼리
	 */
	@Override
	public Optional<Room> findRoomByWorkspaceIdAndSessionIdForWrite(
		String workspaceId, String sessionId
	) {
      return Optional.ofNullable(
			query.selectFrom(room)
			.leftJoin(room.members, member).fetchJoin()
			.innerJoin(room.sessionProperty, sessionProperty).fetchJoin()
			.where(
				room.workspaceId.eq(workspaceId),
				room.sessionId.eq(sessionId)
			)
			.distinct()
			.fetchOne());
	}

	/**
	 * 진행 중인 협업 다이나믹 쿼리
	 *
	 * @param sessionId - 조회될 대상 세션 식별자
	 * @return - 해당 사용자가 참여한 room 검색 조건 쿼리
	 */
	@Override
	public Optional<Room> findRoomByWorkspaceIdAndSessionIdNotInEvictedMember(
		String workspaceId, String sessionId
	) {
		return Optional.ofNullable(
			query.selectFrom(room)
				.leftJoin(room.members, member).fetchJoin()
				.innerJoin(room.sessionProperty, sessionProperty).fetchJoin()
				.where(
					room.workspaceId.eq(workspaceId),
					room.sessionId.eq(sessionId),
					member.memberStatus.ne(MemberStatus.EVICTED)
				)
				.distinct()
				.fetchOne());
	}

	@Override
	public Page<Room> findMyRoomSpecificUserId(
		String workspaceId,
		String userId,
		boolean paging,
		Pageable pageable
	) {
		JPQLQuery<Room> queryResult = query.selectFrom(room)
			.leftJoin(room.members, member).fetchJoin()
			.innerJoin(room.sessionProperty, sessionProperty).fetchJoin()
			.where(
				room.workspaceId.eq(workspaceId),
				room.id.in(includeNotEvicted(workspaceId, userId)).or(room.sessionProperty.sessionType.eq(SessionType.OPEN)),
				room.roomStatus.eq(RoomStatus.ACTIVE)
			)
			.orderBy(room.createdDate.desc())
			.distinct();
		long totalCount = queryResult.fetchCount();
		List<Room> results;
		if (paging) {
			results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, queryResult).fetch();
		} else {
			results = Objects.requireNonNull(queryResult.fetch());
		}
		return new PageImpl<>(results, pageable, totalCount);
	}

	@Override
	public Page<Room> findMyRoomSpecificUserIdBySearch(
		String workspaceId,
		String userId,
		List<String> userIds,
		String search,
		Pageable pageable
	) {
		JPQLQuery<Room> queryResult = query
			.selectFrom(room)
			.leftJoin(room.members, member).fetchJoin()
			.innerJoin(room.sessionProperty, sessionProperty).fetchJoin()
			.where(
				room.workspaceId.eq(workspaceId),
				room.roomStatus.eq(RoomStatus.ACTIVE),
				room.id.in(includeSearch(workspaceId, userId, userIds, search))
				.or(
					room.sessionProperty.sessionType.eq(SessionType.OPEN)
						.and(room.title.contains(search).and(room.workspaceId.eq(workspaceId)))
				)
			).distinct();
		long totalCount = queryResult.fetchCount();
		List<Room> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, queryResult).fetch();
		return new PageImpl<>(results, pageable, totalCount);
	}

	@Override
	public Optional<Room> findOpenRoomByGuest(String workspaceId, String sessionId) {
		return Optional.ofNullable(
			query.selectFrom(room)
				.leftJoin(room.members, member).fetchJoin()
				.innerJoin(room.sessionProperty, sessionProperty).fetchJoin()
				.where(
					room.workspaceId.eq(workspaceId),
					room.sessionId.eq(sessionId),
					room.sessionProperty.sessionType.eq(SessionType.OPEN)
				)
				.distinct()
				.fetchOne());
	}

	/**
	 * 강퇴된 사용자 제외 서브 쿼리
	 * @param userId - 조회될 사용자 정보 식별자
	 * @return - 해당 사용자가 참여한 roomHistory 검색 조건 쿼리
	 */
	private SubQueryExpression includeNotEvicted(String workspaceId, String userId) {
		return JPAExpressions.select(member.room.id)
			.from(member)
			.where(
				member.workspaceId.eq(workspaceId),
				room.id.eq(member.room.id),
				member.uuid.eq(userId).and(member.memberStatus.ne(MemberStatus.EVICTED))
			);
	}

	/**
	 * 사용자 히스토리 검색 서브 쿼리
	 * @param workspaceId - 해당 워크스페이스
	 * @return - 해당 사용자가 참여한 roomHistory 검색 조건 쿼리
	 */
	private SubQueryExpression<Long> includeSearch(String workspaceId, String userId, List<String> userIds, String search) {

		SubQueryExpression<Long> includeUserIds = JPAExpressions
			.select(member.room.id)
			.from(member)
			.where(
				member.uuid.in(userIds)
				.or(
					member.uuid.in(userIds)
						.and(member.room.title.contains(search))
				)
			);

		SubQueryExpression<Long> includeUserId = JPAExpressions
			.select(member.room.id)
			.from(member)
			.where(
				member.uuid.eq(userId)
					.and(member.room.title.contains(search))
			);

		SubQueryExpression<Long> subQueryExpression;
		if (userIds.size() > 0) {
			subQueryExpression = JPAExpressions.select(member.room.id)
				.from(member)
				.where(
					member.workspaceId.eq(workspaceId),
					member.uuid.eq(userId),
					member.room.id.in(includeUserIds),
					member.memberStatus.ne(MemberStatus.EVICTED)
				);
		} else {
			subQueryExpression = JPAExpressions.select(member.room.id)
				.from(member)
				.where(
					member.workspaceId.eq(workspaceId),
					member.uuid.eq(userId),
					member.room.id.in(includeUserId),
					member.memberStatus.ne(MemberStatus.EVICTED)
				);
		}
		return subQueryExpression;
	}
}
