package com.virnect.process.dao;

import com.virnect.process.domain.Target;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author hangkee.min (henry)
 * @project PF-ProcessManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.08
 */
public interface TargetRepository extends JpaRepository<Target, Long> {

}
