package com.virnect.data.redis.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.virnect.data.redis.domain.NonmemberAuth;

@Repository
public interface NonmemberRepository extends CrudRepository<NonmemberAuth, String> {
}
