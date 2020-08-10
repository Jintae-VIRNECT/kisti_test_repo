package com.virnect.api.data;

import com.virnect.api.dao.MemberHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberHistoryRepository extends JpaRepository<MemberHistory, Long> {
    Optional<MemberHistory> findByUuid(final String uuid);

    Optional<MemberHistory> findMemberHistoryByWorkspaceIdAndUuid(final String workspaceId, final String uuid);

    //Optional<MemberHistory> findByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    Optional<MemberHistory> findByWorkspaceIdAndSessionIdAndUuid(final String workspaceId, final String sessionId, final String uuid);

    Optional<MemberHistory> findBySessionId(final String sessionId);

    List<MemberHistory> findAllBySessionId(final String sessionId);

    List<MemberHistory> findMemberHistoriesByWorkspaceIdAndUuid(final String workspaceId, final String userId);

    List<MemberHistory> findByWorkspaceIdAndUuid(final String workspaceId, final String userId);

    List<MemberHistory> findAllByUuid(final String userId);

    Page<MemberHistory> findByWorkspaceIdAndUuidAndRoomHistoryIsNotNull(final String workspaceId, final String userId, Pageable pageable);

    void deleteAllByUuid(final String userId);
}
