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
 * @since 2020.05.21
 */
public class IssueCustomRepositoryImpl extends QuerydslRepositorySupport implements IssueCustomRepository {
    public IssueCustomRepositoryImpl() { super(Issue.class); }

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
    public Page<Issue> getIssuesIn(String userUUID, String workspaceUUID, List<String> userUUIDList, Pageable pageable) {
        JPQLQuery<Issue> query = defaultQuery(userUUID, workspaceUUID);

        if (Objects.nonNull(userUUIDList)) {
            query.where(QIssue.issue.workerUUID.in(userUUIDList));
        }

        List<Issue> issueList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(issueList, pageable, query.fetchCount());
    }

    @Override
    public Page<Issue> getIssuesInSearchProcessTitle(String userUUID, String workspaceUUID, String title, Pageable pageable) {
        JPQLQuery<Issue> query = defaultQuery(userUUID, workspaceUUID);

        query.where(QProcess.process.name.contains(title));

        List<Issue> issueList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(issueList, pageable, query.fetchCount());
    }

    @Override
    public Page<Issue> getIssuesInSearchSubProcessTitle(String userUUID, String workspaceUUID, String title, Pageable pageable) {
        JPQLQuery<Issue> query = defaultQuery(userUUID, workspaceUUID);

        query.where(QSubProcess.subProcess.name.contains(title));

        List<Issue> issueList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(issueList, pageable, query.fetchCount());
    }

    @Override
    public Page<Issue> getIssuesInSearchJobTitle(String userUUID, String workspaceUUID, String title, Pageable pageable) {
        JPQLQuery<Issue> query = defaultQuery(userUUID, workspaceUUID);

        query.where(QJob.job.job.name.contains(title));

        List<Issue> issueList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(issueList, pageable, query.fetchCount());
    }

    @Override
    public Page<Issue> getIssuesOut(String userUUID, List<String> workspaceUserList, Pageable pageable) {
        QIssue qIssue = QIssue.issue;

        JPQLQuery<Issue> query = from(qIssue);

        query.where(qIssue.job.isNull());

        if (Objects.nonNull(workspaceUserList)){
            query.where(qIssue.workerUUID.in(workspaceUserList));
        }

        if (Objects.nonNull(userUUID)) {
            query.where(qIssue.workerUUID.eq(userUUID));
        }

        List<Issue> issueList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(issueList, pageable, query.fetchCount());
    }

    public JPQLQuery<Issue> defaultQuery(String userUUID, String workspaceUUID){
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
