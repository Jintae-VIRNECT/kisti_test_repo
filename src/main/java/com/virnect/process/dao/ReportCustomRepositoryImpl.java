package com.virnect.process.dao;

import com.querydsl.jpa.JPQLQuery;
import com.virnect.process.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Objects;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.05.20
 */
public class ReportCustomRepositoryImpl extends QuerydslRepositorySupport implements ReportCustomRepository{
    public ReportCustomRepositoryImpl() { super(Report.class); }

    @Override
    public Page<Report> getPages(String workspaceUUID, Long processId, Long subProcessId, String workerUUID, Boolean reported, Pageable pageable) {
        QProcess qProcess = QProcess.process;
        QSubProcess qSubProcess = QSubProcess.subProcess;
        QJob qJob = QJob.job;
        QReport qReport = QReport.report;

        JPQLQuery<Report> query = from(qReport);

        query.join(qReport.job, qJob);
        query.join(qJob.subProcess, qSubProcess);
        query.join(qSubProcess.process, qProcess);

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
}
