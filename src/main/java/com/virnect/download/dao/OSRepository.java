package com.virnect.download.dao;

import com.virnect.download.domain.OS;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OSRepository extends JpaRepository<OS, Long> {
    Optional<OS> findByName(String name);
}
