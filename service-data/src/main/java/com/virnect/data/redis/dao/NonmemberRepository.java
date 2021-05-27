package com.virnect.data.redis.dao;

import org.springframework.data.repository.CrudRepository;

import com.virnect.data.redis.domain.NonmemberAuth;

public interface NonmemberRepository extends CrudRepository<NonmemberAuth, String> {
}
