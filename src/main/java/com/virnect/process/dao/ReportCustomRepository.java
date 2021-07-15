package com.virnect.process.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.process.domain.Job;
import com.virnect.process.domain.Report;
import com.virnect.process.dto.response.HourlyReportCountOfaDayResponse;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.05.20
 */
public interface ReportCustomRepository {
	Page<Report> getPages(
		String myUUID, String workspaceUUID, Long processId, Long subProcessId, String search,
		List<String> userUUIDList, Boolean reported, Pageable pageable, Long stepId
	);

	List<HourlyReportCountOfaDayResponse> selectHourlyReportsTemp(String targetDate);

	long deleteAllReportByJobList(List<Job> jobList);
}
