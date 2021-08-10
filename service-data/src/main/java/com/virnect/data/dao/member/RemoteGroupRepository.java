package com.virnect.data.dao.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.data.domain.member.RemoteGroup;

@Repository
public interface RemoteGroupRepository extends JpaRepository<RemoteGroup, Long>, CustomRemoteGroupRepository {

}
