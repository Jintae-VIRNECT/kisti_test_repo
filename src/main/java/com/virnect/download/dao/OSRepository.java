package com.virnect.download.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.download.domain.OS;

public interface OSRepository extends JpaRepository<OS, Long> {
	Optional<OS> findByName(String name);
}
