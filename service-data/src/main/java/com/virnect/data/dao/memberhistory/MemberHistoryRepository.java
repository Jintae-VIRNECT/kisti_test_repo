package com.virnect.data.dao.memberhistory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.data.domain.member.MemberHistory;

@Repository
public interface MemberHistoryRepository extends JpaRepository<MemberHistory, Long>, CustomMemberHistoryRepository {
    Optional<MemberHistory> findByUuid(final String uuid);

    Optional<MemberHistory> findMemberHistoryByWorkspaceIdAndUuid(final String workspaceId, final String uuid);

    //Optional<MemberHistory> findByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    //Optional<MemberHistory> findByWorkspaceIdAndSessionIdAndUuid(final String workspaceId, final String sessionId, final String uuid);

    Optional<MemberHistory> findBySessionId(final String sessionId);

    //List<MemberHistory> findAllBySessionId(final String sessionId);

    List<MemberHistory> findMemberHistoriesByWorkspaceIdAndUuid(final String workspaceId, final String userId);

    //List<MemberHistory> findByWorkspaceIdAndUuid(final String workspaceId, final String userId);

    //List<MemberHistory> findAllByUuid(final String userId);

    Page<MemberHistory> findByWorkspaceIdAndUuidAndRoomHistoryIsNotNull(final String workspaceId, final String userId, Pageable pageable);

    //Page<MemberHistory> findByWorkspaceIdAndUuidAndRoomHistoryIsNotNullAndHistoryDeletedFalse(final String workspaceId, final String userId, Pageable pageable);

    void deleteAllByUuid(final String userId);

    //List<MemberHistory> findByWorkspaceIdAndRoomHistoryIsNotNullAndRoomHistory_ActiveDateBetween(String workspaceId, LocalDateTime start, LocalDateTime end);

    //List<MemberHistory> findByWorkspaceId(final String workspaceId);

}
