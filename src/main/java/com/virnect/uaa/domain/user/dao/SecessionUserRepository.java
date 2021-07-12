package com.virnect.uaa.domain.user.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.uaa.domain.user.domain.SecessionUser;

public interface SecessionUserRepository extends JpaRepository<SecessionUser, Long> {
	Optional<SecessionUser> findByUserUUID(String userUUID);
	List<SecessionUser> findByUserUUIDIn(List<String> userUUIDList);
	boolean existsByEmail(String email);
}
