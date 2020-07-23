package com.virnect.serviceserver.gateway.dao;

import com.virnect.serviceserver.gateway.domain.Member;
import com.virnect.serviceserver.gateway.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUuid(final String uuid);

    Optional<Member> findBySessionId(final String sessionId);

    List<Member> findAllBySessionId(final String sessionId);

    List<Member> findByWorkspaceIdAndSessionIdAndRoomNotNull(final String workspaceId, final String sessionId);

    Page<Member> findByWorkspaceIdAndUuidAndRoomNotNull(final String workspaceId, final String uuid, Pageable pageable);

    List<Member> findByWorkspaceIdAndUuidAndRoomNotNull(final String workspaceId, final String uuid);



    @Modifying
    @Query("delete from Member m where m.room.sessionId=:sessionId")
    void deleteBySessionId(@Param("sessionId")final String sessionId);
}
