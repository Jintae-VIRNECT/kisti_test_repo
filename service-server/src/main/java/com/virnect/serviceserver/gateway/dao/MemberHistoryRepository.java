package com.virnect.serviceserver.gateway.dao;

import com.virnect.serviceserver.gateway.domain.Member;
import com.virnect.serviceserver.gateway.domain.MemberHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberHistoryRepository extends JpaRepository<MemberHistory, Long> {
    Optional<MemberHistory> findByUuid(final String uuid);

    Optional<MemberHistory> findBySessionId(final String sessionId);

    List<MemberHistory> findAllBySessionId(final String sessionId);

    List<MemberHistory> findAllByUuid(final String userId);

    void deleteAllByUuid(final String userId);
}
