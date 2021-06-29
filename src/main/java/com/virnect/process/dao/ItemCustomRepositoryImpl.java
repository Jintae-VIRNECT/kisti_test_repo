package com.virnect.process.dao;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.process.domain.Item;
import com.virnect.process.domain.Process;
import com.virnect.process.domain.QItem;
import com.virnect.process.domain.Report;

/**
 * Project: PF-ProcessManagement
 * DATE: 2021-06-28
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class ItemCustomRepositoryImpl extends QuerydslRepositorySupport implements ItemCustomRepository {
	/**
	 * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
	 *
	 * @param domainClass must not be {@literal null}.
	 */
	public ItemCustomRepositoryImpl() {
		super(Item.class);
	}

	@Override
	public long deleteAllItemByReportList(List<Report> reportList) {
		QItem qItem = QItem.item;
		return delete(qItem).where(qItem.report.in(reportList)).execute();
	}

	@Override
	public long deleteAllItemByProcessList(List<Process> processList) {
		QItem qItem = QItem.item;
		return delete(qItem).where(qItem.report.job.subProcess.process.in(processList)).execute();
	}

}
