package com.virnect.process.dao.dailytotalworkspace;

/**
 * Project: PF-ProcessManagement
 * DATE: 2021-06-28
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface DailyTotalWorkspaceCustomRepository {
	long deleteAllDailyTotalWorkspaceByWorkspaceUUID(String workspaceUUID);
}
