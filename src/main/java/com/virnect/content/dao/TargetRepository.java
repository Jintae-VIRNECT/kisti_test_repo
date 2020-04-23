package com.virnect.content.dao;

import com.virnect.content.domain.Target;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.08
 */
public interface TargetRepository extends JpaRepository<Target, Long> {

}
