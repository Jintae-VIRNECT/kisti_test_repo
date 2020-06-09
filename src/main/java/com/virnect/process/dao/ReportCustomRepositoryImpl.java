package com.virnect.process.dao;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.virnect.process.domain.*;
import com.virnect.process.dto.response.HourlyReportCountOfaDayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.05.20
 */
@Slf4j
public class ReportCustomRepositoryImpl extends QuerydslRepositorySupport implements ReportCustomRepository{
    public ReportCustomRepositoryImpl() { super(Report.class); }

    @Override
    public Page<Report> getPages(String userUUID, String workspaceUUID, Long processId, Long subProcessId, String workerUUID, Boolean reported, Pageable pageable) {
        QProcess qProcess = QProcess.process;
        QSubProcess qSubProcess = QSubProcess.subProcess;
        QJob qJob = QJob.job;
        QReport qReport = QReport.report;

        JPQLQuery<Report> query = from(qReport);

        query.join(qReport.job, qJob);
        query.join(qJob.subProcess, qSubProcess);
        query.join(qSubProcess.process, qProcess);

        if (Objects.nonNull(userUUID)) {
            query = query.where(qSubProcess.workerUUID.eq(userUUID));
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
        if (Objects.nonNull(workerUUID)) {
            query = query.where(qSubProcess.workerUUID.eq(workerUUID));
        }

        if (Objects.nonNull(reported)) {
            if (reported) {
                query = query.where(qJob.isReported.eq(YesOrNo.YES));
            } else {
                query = query.where(qJob.isReported.eq(YesOrNo.NO));
            }
        }

        List<Report> paperList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(paperList, pageable, query.fetchCount());
    }

    @Override
    public List<HourlyReportCountOfaDayResponse> selectHourlyReportsTemp(String targetDate) {
        QReport qReport = QReport.report;
        QJob qJob = QJob.job;

        LocalDate date = LocalDate.parse(targetDate);

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to   = date.atTime(23, 59);

        JPQLQuery<HourlyReportCountOfaDayResponse> query = from(qReport)
                .join(qReport.job, qJob)
                .select(Projections.bean(HourlyReportCountOfaDayResponse.class,
                        qReport.count().intValue().as("reportCount"),
                        new CaseBuilder()
                        .when(qReport.updatedDate.hour().stringValue().length().eq(1))
                        .then(qReport.updatedDate.hour().stringValue().prepend("0"))
                        .otherwise(qReport.updatedDate.hour().stringValue()).as("hour")))
                .where(qReport.updatedDate.between(from, to))
                .groupBy(new CaseBuilder()
                        .when(qReport.updatedDate.hour().stringValue().length().eq(1))
                        .then(qReport.updatedDate.hour().stringValue().prepend("0"))
                        .otherwise(qReport.updatedDate.hour().stringValue()));

        List<HourlyReportCountOfaDayResponse> resultList = query.fetch();

        return resultList;
    }
}
