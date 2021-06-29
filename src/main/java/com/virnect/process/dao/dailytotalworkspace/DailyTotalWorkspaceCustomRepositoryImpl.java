package com.virnect.process.dao.dailytotalworkspace;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.process.domain.DailyTotalWorkspace;
import com.virnect.process.domain.QDailyTotalWorkspace;

/**
 * Project: PF-ProcessManagement
 * DATE: 2021-06-28
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class DailyTotalWorkspaceCustomRepositoryImpl extends QuerydslRepositorySupport
	implements DailyTotalWorkspaceCustomRepository {
	/**
	 * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
	 *
	 * @param domainClass must not be {@literal null}.
	 */
	public DailyTotalWorkspaceCustomRepositoryImpl() {
		super(DailyTotalWorkspace.class);
	}

	@Override
	public long deleteAllDailyTotalWorkspaceByWorkspaceUUID(String workspaceUUID) {
		QDailyTotalWorkspace qDailyTotalWorkspace = QDailyTotalWorkspace.dailyTotalWorkspace;
		return delete(qDailyTotalWorkspace).where(qDailyTotalWorkspace.workspaceUUID.eq(workspaceUUID)).execute();
	}
}
