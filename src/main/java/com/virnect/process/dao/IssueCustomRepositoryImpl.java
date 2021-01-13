package com.virnect.process.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.thymeleaf.util.StringUtils;

import com.querydsl.jpa.JPQLQuery;

import com.virnect.process.domain.Issue;
import com.virnect.process.domain.QIssue;
import com.virnect.process.domain.QJob;
import com.virnect.process.domain.QProcess;
import com.virnect.process.domain.QSubProcess;
import com.virnect.process.global.common.PageRequest;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.05.21
 */
public class IssueCustomRepositoryImpl extends QuerydslRepositorySupport implements IssueCustomRepository {
	public IssueCustomRepositoryImpl() {
		super(Issue.class);
	}

	@Override
	public Long countIssuesInSubProcess(Long subProgressId) {
		QIssue qIssue = QIssue.issue;
		QJob qJob = QJob.job;
		QSubProcess qSubProcess = QSubProcess.subProcess;

		JPQLQuery<Issue> query = from(qIssue)
			.join(qIssue.job, qJob)
			.join(qJob.subProcess, qSubProcess)
			.where(qSubProcess.id.eq(subProgressId));

		Long result = query.fetchCount();

		return result;
	}

	// 더 보기
	@Override
	public Page<Issue> getTroubleMemo(String workerUUID, Pageable pageable) {
		QIssue qIssue = QIssue.issue;

		JPQLQuery<Issue> query = from(qIssue);

		if (Objects.nonNull(workerUUID)) {
			query.where(qIssue.workerUUID.eq(workerUUID));
		}
		query.where(qIssue.job.id.isNull());

		List<Issue> issueList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl<>(issueList, pageable, query.fetchCount());
	}

	// 더 보기
	@Override
	public Page<Issue> getTroubleMemoSearchUserName(String userUUID, List<String> userUUIDList, Pageable pageable) {
		JPQLQuery<Issue> query = defaultQuery(userUUID, null);

		List<Issue> issueList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl<>(issueList, pageable, query.fetchCount());
	}

	@Override
	public Page<Issue> getIssuesIn(
		String myUUID, String workspaceUUID, String search, Long stepId, List<String> userUUIDList, Pageable pageable
	) {
		JPQLQuery<Issue> query = defaultQuery(myUUID, workspaceUUID);

		// 문제의 소지가 될 수 있는 쿼리.
		if (Objects.nonNull(search)) {
			query.where(QJob.job.name.contains(search));
			query.where(QIssue.issue.content.contains(search));
			query.where(QSubProcess.subProcess.name.contains(search));
			query.where(QProcess.process.name.contains(search));
		}

		if (Objects.nonNull(stepId)) {
			query.where(QJob.job.id.eq(stepId));
		}

		if (!userUUIDList.isEmpty()) {
			query.where(QIssue.issue.workerUUID.in(userUUIDList));
		}
		pageable = pageSortNameCheck(pageable);
		List<Issue> issueList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl<>(issueList, pageable, query.fetchCount());
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
			if (order.getProperty().equals("caption")) {
				pageRequest.setSort(StringUtils.concat("content", ",", order.getDirection()));
				return pageRequest.of();
			}
		}
		return pageable;
	}

	@Override
	public Page<Issue> getIssuesInSearchProcessTitle(
		String userUUID, String workspaceUUID, String title, Pageable pageable
	) {
		JPQLQuery<Issue> query = defaultQuery(userUUID, workspaceUUID);

		query.where(QProcess.process.name.contains(title));

		List<Issue> issueList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl<>(issueList, pageable, query.fetchCount());
	}

	@Override
	public Page<Issue> getIssuesInSearchSubProcessTitle(
		String userUUID, String workspaceUUID, String title, Pageable pageable
	) {
		JPQLQuery<Issue> query = defaultQuery(userUUID, workspaceUUID);

		query.where(QSubProcess.subProcess.name.contains(title));

		List<Issue> issueList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl<>(issueList, pageable, query.fetchCount());
	}

	@Override
	public Page<Issue> getIssuesInSearchJobTitle(
		String userUUID, String workspaceUUID, String title, Pageable pageable
	) {
		JPQLQuery<Issue> query = defaultQuery(userUUID, workspaceUUID);

		query.where(QJob.job.name.contains(title));

		List<Issue> issueList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl<>(issueList, pageable, query.fetchCount());
	}

	@Override
	public Page<Issue> getIssuesOut(String myUUID, String search, List<String> workspaceUserList, Pageable pageable) {
		QIssue qIssue = QIssue.issue;

		JPQLQuery<Issue> query = from(qIssue);

		query.where(qIssue.job.isNull());

		if (Objects.nonNull(search)) {
			query.where(qIssue.content.contains(search));
		}

		if (Objects.nonNull(workspaceUserList)) {
			query.where(qIssue.workerUUID.in(workspaceUserList));
		}

		if (Objects.nonNull(myUUID)) {
			query.where(qIssue.workerUUID.eq(myUUID));
		}

		List<Issue> issueList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl<>(issueList, pageable, query.fetchCount());
	}

	public JPQLQuery<Issue> defaultQuery(String userUUID, String workspaceUUID) {
		QIssue qIssue = QIssue.issue;
		QJob qJob = QJob.job;
		QProcess qProcess = QProcess.process;
		QSubProcess qSubProcess = QSubProcess.subProcess;

		JPQLQuery<Issue> query = from(qIssue);

		query.join(qIssue.job, qJob);
		query.join(qJob.subProcess, qSubProcess);
		query.join(qSubProcess.process, qProcess);

		if (Objects.nonNull(userUUID)) {
			query.where(qIssue.workerUUID.eq(userUUID));
		}

		if (Objects.nonNull(workspaceUUID)) {
			query.where(qProcess.workspaceUUID.eq(workspaceUUID));
		}

		return query;
	}
}
