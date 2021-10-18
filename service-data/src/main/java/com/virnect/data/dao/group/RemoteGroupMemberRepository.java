package com.virnect.data.dao.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.data.domain.group.RemoteGroupMember;

@Repository
public interface RemoteGroupMemberRepository extends JpaRepository<RemoteGroupMember, Long>, CustomRemoteGroupMemberRepository {
}
