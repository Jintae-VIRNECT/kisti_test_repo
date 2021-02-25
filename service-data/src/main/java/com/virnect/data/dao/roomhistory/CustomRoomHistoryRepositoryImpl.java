package com.virnect.data.dao.roomhistory;

import static com.virnect.data.domain.member.QMemberHistory.*;
import static com.virnect.data.domain.roomhistory.QRoomHistory.*;
import static com.virnect.data.domain.session.QSessionPropertyHistory.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.data.domain.roomhistory.RoomHistory;

@RequiredArgsConstructor
public class CustomRoomHistoryRepositoryImpl implements CustomRoomHistoryRepository {
	private final JPAQueryFactory query;

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

	@Override
	public RoomHistory findRoomHistoryByWorkspaceIdAndSessionId(
		String workspaceId,
		String sessionId
	) {
		return query
			.selectFrom(roomHistory)
			.innerJoin(roomHistory.memberHistories, memberHistory).fetchJoin()
			.innerJoin(roomHistory.sessionPropertyHistory, sessionPropertyHistory).fetchJoin()
			.where(
				roomHistory.workspaceId.eq(workspaceId),
				roomHistory.sessionId.eq(sessionId)
			)
			.distinct()
			.fetchOne();
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
		List<Long> roomHistoryIdList = query.selectFrom(memberHistory)
			.select(memberHistory.roomHistory.id)
			.where(memberHistory.uuid.eq(userId))
			.fetch();

		return roomHistory.id.in(roomHistoryIdList);
	}
}
