package com.virnect.content.dao;

import com.virnect.content.domain.Target;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.08
 */
public interface TargetRepository extends JpaRepository<Target, Long> {
    Optional<Target> findByData(String targetData);

    Optional<Target> findById(Long id);

    Optional<Target> findbyContentId(Long contentId);

    void deleteByContentId(long contentId);
}
