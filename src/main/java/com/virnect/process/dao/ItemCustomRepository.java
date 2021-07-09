package com.virnect.process.dao;

import java.util.List;

import com.virnect.process.domain.Process;
import com.virnect.process.domain.Report;

/**
 * Project: PF-ProcessManagement
 * DATE: 2021-06-28
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface ItemCustomRepository {
	long deleteAllItemByReportList(List<Report> reportList);

	long deleteAllItemByProcessList(List<Process> processList);
}
