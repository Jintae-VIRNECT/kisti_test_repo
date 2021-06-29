package com.virnect.process.dao.dailytotal;

import java.util.List;

import com.virnect.process.domain.DailyTotal;
import com.virnect.process.domain.DailyTotalWorkspace;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.06.08
 */
public interface DailyTotalCustomRepository {

	List<DailyTotal> getDailyTotalRateAtMonth(String month);

	List<DailyTotalWorkspace> getDailyTotalRateAtMonthWithWorkspace(String workspaceUUID, String month);

	long deleteAllDailyTotalByDailyTotalList(List<DailyTotal> dailyTotalList);
}
