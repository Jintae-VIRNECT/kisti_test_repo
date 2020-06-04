package com.virnect.process.dao;

import com.querydsl.jpa.JPQLQuery;
import com.virnect.process.domain.Job;
import com.virnect.process.domain.QJob;
import lombok.extern.slf4j.Slf4j;
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
 * @since 2020.05.22
 */
@Slf4j
public class JobCustomRepositoryImpl extends QuerydslRepositorySupport implements JobCustomRepository {
    public JobCustomRepositoryImpl() { super(Job.class); }

    @Override
    public Page<Job> getJobPage(Long subProcessId, String search, Pageable pageable) {
        QJob qJob = QJob.job;

        JPQLQuery<Job> query = from(qJob);

        query.where(qJob.subProcess.id.eq(subProcessId));

        if (Objects.nonNull(search)) {
            query.where(qJob.name.contains(search));
        }

        List<Job> jobList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(jobList, pageable, query.fetchCount());
    }
}
