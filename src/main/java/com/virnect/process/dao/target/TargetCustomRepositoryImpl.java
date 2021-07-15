package com.virnect.process.dao.target;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.process.domain.Process;
import com.virnect.process.domain.QTarget;
import com.virnect.process.domain.Target;

/**
 * Project: PF-ProcessManagement
 * DATE: 2021-06-29
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class TargetCustomRepositoryImpl extends QuerydslRepositorySupport implements TargetCustomRepository {
	/**
	 * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
	 *
	 * @param domainClass must not be {@literal null}.
	 */
	public TargetCustomRepositoryImpl() {
		super(Target.class);
	}

	@Override
	public long deleteAllTargetByProcessList(List<Process> processList) {
		QTarget qTarget = QTarget.target;
		return delete(qTarget).where(qTarget.process.in(processList)).execute();
	}
}
