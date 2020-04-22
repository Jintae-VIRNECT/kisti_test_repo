package com.virnect.process.dao;

import com.querydsl.jpa.JPQLQuery;
import com.virnect.process.domain.QProcess;
import com.virnect.process.domain.QSubProcess;
import com.virnect.process.domain.SubProcess;
import com.virnect.process.domain.YesOrNo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class SubProcessCustomRepositoryImpl extends QuerydslRepositorySupport implements SubProcessCustomRepository {

    public SubProcessCustomRepositoryImpl() {
        super(SubProcess.class);
    }

    @Override
    public Page<SubProcess> getMyWorks(String workerUUID, /*long processId, String search, */Pageable pageable) {
        QSubProcess qSubProcess = QSubProcess.subProcess;
        JPQLQuery<SubProcess> query = from(qSubProcess);
        query.where(/*qSubProcess.id.eq(processId).and(qSubProcess.name.contains(search)).and*/(qSubProcess.workerUUID.eq(workerUUID)));
        long totalCount = query.fetchCount();
        List<SubProcess> results = getQuerydsl().applyPagination(pageable, query).fetch();
        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public boolean existsByIsRecentAndWorkerUUIDAndWorkspaceUUID(YesOrNo newWork, String workspaceUUID, String workerUUID) {
        QProcess qProcess = QProcess.process;
        QSubProcess qSubProcess = QSubProcess.subProcess;
        JPQLQuery<SubProcess> query = from(qSubProcess).where(qSubProcess.isRecent.eq(newWork));

        if (workerUUID != null) {
            query = query.where(qSubProcess.workerUUID.eq(workerUUID));
        }

        if (workspaceUUID != null) {
            query = query.join(qProcess).where(qProcess.workspaceUUID.eq(workspaceUUID));
        }

        long totalCount = query.fetchCount();

        return totalCount > 0;
    }
}
