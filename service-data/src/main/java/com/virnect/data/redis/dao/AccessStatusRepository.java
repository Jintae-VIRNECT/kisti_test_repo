package com.virnect.data.redis.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.virnect.data.redis.domain.AccessStatus;

@Repository
public interface AccessStatusRepository extends CrudRepository<AccessStatus, String> {
}
