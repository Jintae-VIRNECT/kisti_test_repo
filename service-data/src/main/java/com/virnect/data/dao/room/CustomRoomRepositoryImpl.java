package com.virnect.data.dao.room;

import static com.virnect.data.domain.member.QMember.*;
import static com.virnect.data.domain.room.QRoom.*;
import static com.virnect.data.domain.session.QSessionProperty.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.data.domain.room.Room;
import com.virnect.data.domain.room.RoomStatus;

@Repository
public class CustomRoomRepositoryImpl extends QuerydslRepositorySupport implements CustomRoomRepository {
	private final JPAQueryFactory query;

	public CustomRoomRepositoryImpl(JPAQueryFactory query) {
		super(Room.class);
		this.query = query;
	}

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

	/*@Override
	public Room findRoomHistoryByWorkspaceAndSessionId(String workspaceId, String sessionId) {
		return query.selectFrom(room)
			.innerJoin(room.members, member).fetchJoin()
			.innerJoin(room.sessionProperty, sessionProperty).fetchJoin()
			.where(
				room.workspaceId.eq(workspaceId),
				room.sessionId.eq(sessionId)
			)
			.distinct()
			.fetchOne();
	}*/

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
	 * 협업 정보 조회 다이나믹 쿼리
	 * @param workspaceId - 조회될 대상 워크스페이스 식별자
	 * @param sessionId - 조회될 대상 세션 식별자
	 * @return - 해당 사용자가 참여한 room 검색 조건 쿼리
	 */
	@Override
	public Optional<Room> findRoomByWorkspaceIdAndSessionId(
		String workspaceId, String sessionId
	) {
		return Optional.ofNullable(
			query.selectFrom(room)
			.innerJoin(room.members, member).fetchJoin()
			.innerJoin(room.sessionProperty, sessionProperty).fetchJoin()
			.where(
				room.workspaceId.eq(workspaceId),
				room.sessionId.eq(sessionId)
			)
			.distinct()
			.fetchOne());
	}

	@Override
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

	@Override
	public Optional<Room> findRoomByWorkspaceIdAndSessionIdForWrite(
		String workspaceId, String sessionId
	) {
		return Optional.ofNullable(
			query.selectFrom(room)
			.innerJoin(room.members, member).fetchJoin()
			.innerJoin(room.sessionProperty, sessionProperty).fetchJoin()
			.where(
				room.workspaceId.eq(workspaceId),
				room.sessionId.eq(sessionId)
			)
			.distinct()
			.fetchOne());
	}

	@Override
	public Page<Room> findRoomByWorkspaceId(
		String workspaceId, Pageable pageable
	) {
		JPQLQuery<Room> queryResult =query.selectFrom(room)
			.innerJoin(room.members, member).fetchJoin()
			.innerJoin(room.sessionProperty, sessionProperty).fetchJoin()
			.where(
				room.workspaceId.eq(workspaceId)
			);
		long totalCount = queryResult.fetchCount();
		List<Room> result = getQuerydsl().applyPagination(pageable, queryResult).fetch();
		return new PageImpl<>(result, pageable, totalCount);
	}

	@Override
	public List<Room> findByWorkspaceId(String workspaceId) {
		return query.selectFrom(room)
			.innerJoin(room.members, member).fetchJoin()
			.innerJoin(room.sessionProperty, sessionProperty).fetchJoin()
			.where(
				room.workspaceId.eq(workspaceId)
			)
			.distinct()
			.fetch();
	}

}
