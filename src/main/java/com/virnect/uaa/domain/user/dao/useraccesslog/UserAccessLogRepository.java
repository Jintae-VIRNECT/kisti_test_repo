package com.virnect.uaa.domain.user.dao.useraccesslog;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.domain.UserAccessLog;

public interface UserAccessLogRepository extends JpaRepository<UserAccessLog, Long>, UserAccessLogCustomRepository {
	Page<UserAccessLog> findAllByUser(User user, Pageable pageable);
}
