package com.virnect.process.dao.target;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.process.domain.Process;
import com.virnect.process.domain.Target;

/**
 * @author hangkee.min (henry)
 * @project PF-ProcessManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.08
 */
public interface TargetRepository extends JpaRepository<Target, Long>, TargetCustomRepository {

	List<Target> findByProcessIn(List<Process> processList);
}
