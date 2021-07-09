package com.virnect.process.dao.dailytotal;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.JPQLQuery;

import lombok.extern.slf4j.Slf4j;

import com.virnect.process.domain.DailyTotal;
import com.virnect.process.domain.DailyTotalWorkspace;
import com.virnect.process.domain.QDailyTotal;
import com.virnect.process.domain.QDailyTotalWorkspace;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.06.08
 */
@Slf4j
public class DailyTotalCustomRepositoryImpl extends QuerydslRepositorySupport implements DailyTotalCustomRepository {
	public DailyTotalCustomRepositoryImpl() {
		super(DailyTotal.class);
	}

	@Override
	public List<DailyTotal> getDailyTotalRateAtMonth(String month) {
		QDailyTotal qDailyTotal = QDailyTotal.dailyTotal;

		YearMonth dd = YearMonth.parse(month);

		LocalDateTime lda = dd.atDay(1).atStartOfDay();
		LocalDateTime lde = dd.atEndOfMonth().atTime(23, 59, 59);

		LocalDateTime zdt = lda.minusHours(9);
		LocalDateTime zdt2 = lde.minusHours(9);

		JPQLQuery<DailyTotal> query = from(qDailyTotal)
			.where(qDailyTotal.updatedDate.between(zdt, zdt2));

		List<DailyTotal> dailyTotalList = query.fetch();

		return dailyTotalList;
	}

	@Override
	public List<DailyTotalWorkspace> getDailyTotalRateAtMonthWithWorkspace(String workspaceUUID, String month) {
		QDailyTotalWorkspace qDailyTotalWorkspace = QDailyTotalWorkspace.dailyTotalWorkspace;

		YearMonth dd = YearMonth.parse(month);

		LocalDateTime lda = dd.atDay(1).atStartOfDay();
		LocalDateTime lde = dd.atEndOfMonth().atTime(23, 59, 59);

		LocalDateTime zdt = lda.minusHours(9);
		LocalDateTime zdt2 = lde.minusHours(9);

		JPQLQuery<DailyTotalWorkspace> query = from(qDailyTotalWorkspace)
			.where(qDailyTotalWorkspace.updatedDate.between(zdt, zdt2))
			.where(qDailyTotalWorkspace.workspaceUUID.eq(workspaceUUID));

		List<DailyTotalWorkspace> dailyTotalWorkspaceList = query.fetch();

		return dailyTotalWorkspaceList;
	}

	@Override
	public long deleteAllDailyTotalByDailyTotalList(List<DailyTotal> dailyTotalList) {
		QDailyTotal qDailyTotal = QDailyTotal.dailyTotal;
		return delete(qDailyTotal).where(qDailyTotal.in(dailyTotalList)).execute();
	}
	
}
