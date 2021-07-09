package com.virnect.process.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.thymeleaf.util.StringUtils;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.slf4j.Slf4j;

import com.virnect.process.domain.Job;
import com.virnect.process.domain.QJob;
import com.virnect.process.domain.QProcess;
import com.virnect.process.domain.QReport;
import com.virnect.process.domain.QSubProcess;
import com.virnect.process.domain.Report;
import com.virnect.process.domain.YesOrNo;
import com.virnect.process.dto.response.HourlyReportCountOfaDayResponse;
import com.virnect.process.global.common.PageRequest;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.05.20
 */
@Slf4j
public class ReportCustomRepositoryImpl extends QuerydslRepositorySupport implements ReportCustomRepository {
	public ReportCustomRepositoryImpl() {
		super(Report.class);
	}

	@Override
	public Page<Report> getPages(
		String myUUID, String workspaceUUID, Long processId, Long subProcessId, String search,
		List<String> userUUIDList, Boolean reported, Pageable pageable, Long stepId
	) {
		QProcess qProcess = QProcess.process;
		QSubProcess qSubProcess = QSubProcess.subProcess;
		QJob qJob = QJob.job;
		QReport qReport = QReport.report;

		JPQLQuery<Report> query = from(qReport);

		query.join(qReport.job, qJob);
		query.join(qJob.subProcess, qSubProcess);
		query.join(qSubProcess.process, qProcess);

		if (Objects.nonNull(stepId)) {
			query = query.where(qJob.id.eq(stepId));
		}

		if (Objects.nonNull(myUUID)) {
			query = query.where(qSubProcess.workerUUID.eq(myUUID));
		}

		if (Objects.nonNull(workspaceUUID)) {
			query = query.where(qProcess.workspaceUUID.eq(workspaceUUID));
		}

		if (Objects.nonNull(processId)) {
			query = query.where(qProcess.id.eq(processId));
		}

		if (Objects.nonNull(subProcessId)) {
			query = query.where(qSubProcess.id.eq(subProcessId));
		}

		if (!userUUIDList.isEmpty()) {
			query = query.where(qSubProcess.workerUUID.in(userUUIDList));
		} else {
			// 프로필 -> 바로가기 시 처리를 위해
			if (Objects.nonNull(search)) {
				query = query.where(qSubProcess.workerUUID.eq(search));
			}

		}

		if (Objects.nonNull(reported)) {
			if (reported) {
				query = query.where(qJob.isReported.eq(YesOrNo.YES));
			} else {
				query = query.where(qJob.isReported.eq(YesOrNo.NO));
			}
		}

		pageable = pageSortNameCheck(pageable);
		List<Report> paperList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl<>(paperList, pageable, query.fetchCount());
	}

	private Pageable pageSortNameCheck(Pageable pageable) {
		PageRequest pageRequest = new PageRequest();
		pageRequest.setPage(pageable.getPageNumber());
		pageRequest.setSize(pageable.getPageSize());
		Iterator<Sort.Order> iter = pageable.getSort().stream().iterator();
		while (iter.hasNext()) {
			Sort.Order order = iter.next();
			if (order.getProperty().equals("taskName")) {
				pageRequest.setSort(StringUtils.concat("job.subProcess.Process.name", ",", order.getDirection()));
				return pageRequest.of();
			}
			if (order.getProperty().equals("subTaskName")) {
				pageRequest.setSort(StringUtils.concat("job.subProcess.name", ",", order.getDirection()));
				return pageRequest.of();
			}
			if (order.getProperty().equals("stepName")) {
				pageRequest.setSort(StringUtils.concat("job.name", ",", order.getDirection()));
				return pageRequest.of();
			}
		}
		return pageable;
	}

	@Override
	public List<HourlyReportCountOfaDayResponse> selectHourlyReportsTemp(String targetDate) {
		QReport qReport = QReport.report;
		QJob qJob = QJob.job;

		LocalDate date = LocalDate.parse(targetDate);

		LocalDateTime from = date.atStartOfDay();
		LocalDateTime to = date.atTime(23, 59);

		JPQLQuery<HourlyReportCountOfaDayResponse> query = from(qReport)
			.join(qReport.job, qJob)
			.select(Projections.bean(
				HourlyReportCountOfaDayResponse.class,
				qReport.count().intValue().as("reportCount"),
				new CaseBuilder()
					.when(qReport.updatedDate.hour().stringValue().length().eq(1))
					.then(qReport.updatedDate.hour().stringValue().prepend("0"))
					.otherwise(qReport.updatedDate.hour().stringValue()).as("hour")
			))
			.where(qReport.updatedDate.between(from, to))
			.groupBy(new CaseBuilder()
				.when(qReport.updatedDate.hour().stringValue().length().eq(1))
				.then(qReport.updatedDate.hour().stringValue().prepend("0"))
				.otherwise(qReport.updatedDate.hour().stringValue()));

		List<HourlyReportCountOfaDayResponse> resultList = query.fetch();

		return resultList;
	}

	@Override
	public long deleteAllReportByJobList(List<Job> jobList) {
		QReport qReport = QReport.report;
		return delete(qReport).where(qReport.job.in(jobList)).execute();
	}
}

