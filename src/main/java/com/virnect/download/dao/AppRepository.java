package com.virnect.download.dao;

import com.virnect.download.domain.App;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Project: PF-Download
 * DATE: 2020-04-23
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface AppRepository extends JpaRepository<App, Long>, AppRepositoryCustom {
    Optional<App> findByUuid(String uuid);
}
