package com.virnect.uaa.domain.auth.websocket.dao;

import org.springframework.data.repository.CrudRepository;

import com.virnect.data.redis.domain.AccessStatus;

public interface AccessStatusRepository extends CrudRepository<AccessStatus, String> {
}
