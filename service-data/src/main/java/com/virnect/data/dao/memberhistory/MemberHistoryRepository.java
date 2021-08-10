package com.virnect.data.dao.memberhistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.data.domain.member.MemberHistory;

@Repository
public interface MemberHistoryRepository extends JpaRepository<MemberHistory, Long>, CustomMemberHistoryRepository {

}
