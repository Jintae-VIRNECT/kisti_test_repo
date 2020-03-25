package com.virnect.process.dao;

import com.querydsl.jpa.JPQLQuery;
import com.virnect.process.domain.SubProcess;
import com.virnect.process.domain.QSubProcess;
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
}
