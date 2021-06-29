package com.virnect.process.dao;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.JPQLQuery;

import lombok.extern.slf4j.Slf4j;

import com.virnect.process.domain.Job;
import com.virnect.process.domain.QJob;
import com.virnect.process.domain.SubProcess;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.05.22
 */
@Slf4j
public class JobCustomRepositoryImpl extends QuerydslRepositorySupport implements JobCustomRepository {
	public JobCustomRepositoryImpl() {
		super(Job.class);
	}

	@Override
	public Page<Job> getJobPage(String myUUID, Long subProcessId, String search, Pageable pageable) {
		QJob qJob = QJob.job;

		JPQLQuery<Job> query = from(qJob);

		query.where(qJob.subProcess.id.eq(subProcessId));

		if (Objects.nonNull(myUUID)) {
			query.where(qJob.subProcess.workerUUID.eq(myUUID));
		}

		if (Objects.nonNull(search)) {
			query.where(qJob.name.contains(search));
		}

		List<Job> jobList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl<>(jobList, pageable, query.fetchCount());
	}

	@Override
	public long deleteAllJobBySubProcessList(List<SubProcess> subProcessList) {
		QJob qJob = QJob.job;
		return delete(qJob).where(qJob.subProcess.in(subProcessList)).execute();
	}
}
