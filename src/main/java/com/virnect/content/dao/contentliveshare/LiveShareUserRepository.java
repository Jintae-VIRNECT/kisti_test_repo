package com.virnect.content.dao.contentliveshare;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.content.domain.LiveShareUser;

public interface LiveShareUserRepository extends JpaRepository<LiveShareUser, Long>,
	LiveShareUserCustomRepository {
}
