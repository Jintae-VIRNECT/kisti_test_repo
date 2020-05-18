package com.virnect.process.dao.process;

import com.querydsl.jpa.JPQLQuery;
import com.virnect.process.domain.*;
import com.virnect.process.domain.Process;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ProcessCustomRepositoryImpl extends QuerydslRepositorySupport implements ProcessCustomRepository {
    public ProcessCustomRepositoryImpl() {
        super(Process.class);
    }

    @Override
    public Process findByTargetDataAndState(String targetData, State state) {
        QProcess qProcess = QProcess.process;
        QTarget qTarget = QTarget.target;
        JPQLQuery<Process> query = from(qProcess).where(qProcess.state.eq(state));
        query = query.join(qProcess.targetList, qTarget).where(qTarget.data.eq(targetData));
        return query.fetchOne();
    }

    @Override
    public List<Process> getProcessListSearchUser(String workspaceUUID, String title, List<String> userUUIDList) {
        QProcess qProcess = QProcess.process;
        QSubProcess qSubProcess = QSubProcess.subProcess;
        QTarget qTarget = QTarget.target;

        JPQLQuery<Process> query = from(qProcess);

        query = query.join(qProcess.subProcessList, qSubProcess);

        query = query.join(qProcess.targetList, qTarget);

        if (workspaceUUID != null)
        {
            query = query.where(qProcess.workspaceUUID.contains(workspaceUUID));
        }

        if (title != null)
        {
            query = query.where(qProcess.name.contains(title));
        }

        query.groupBy(qProcess.id);

        final List<Process> processList = query.fetch();

        return processList;
    }

    @Override
    public Page<Process> getProcessPageSearchUser(String workspaceUUID, String title, Pageable pageable){
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ProcessCustomRepository in");
        QProcess qProcess = QProcess.process;
        QSubProcess qSubProcess = QSubProcess.subProcess;
        QTarget qTarget = QTarget.target;

        JPQLQuery<Process> query = from(qProcess);

        query = query.join(qProcess.subProcessList, qSubProcess);

        query = query.join(qProcess.targetList, qTarget);

        log.debug(">>>>>>>>>>>> query : {} ", query);

        if (workspaceUUID != null)
        {
            query = query.where(qProcess.workspaceUUID.contains(workspaceUUID));
        }

//        if (userUUIDList != null)
//        {
//            query = query.where(qSubProcess.workerUUID.in(userUUIDList));
//        }

        if (title != null)
        {
            query = query.where(qProcess.name.contains(title));
        }

        query.groupBy(qProcess.id);

        log.debug(">>>>>>>>>>>> query : {} ", query);

        // 중복 제거
        final List<Process> processList = getQuerydsl().applyPagination(pageable, query).fetch(); //.stream().distinct().collect(Collectors.toList());

        log.debug(">>>>>>>>>>>> fetchResults : {}", query.fetchResults());
        return new PageImpl<>(processList, pageable, query.fetchCount());

        //return query.fetch();
    }

    @Override
    public int getCountIssuesInProcess(Long processId){
        QProcess qProcess = QProcess.process;
        QSubProcess qSubProcess = QSubProcess.subProcess;
        QJob qJob = QJob.job;
        QIssue qIssue = QIssue.issue;


        JPQLQuery<Process> query = from(qProcess);

        query.join(qProcess.subProcessList, qSubProcess);

        query.join(qSubProcess.jobList, qJob);

        query.join(qJob.issueList, qIssue);

        query.where(qProcess.id.eq(processId));

        return (int)query.fetchCount();
    }

    @Override
    public Optional<Process> getProcessUnClosed(String workspaceUUID, String targetData) {
        QProcess qProcess = QProcess.process;
        QTarget qTarget = QTarget.target;

        JPQLQuery<Process> query = from(qProcess);

        query.join(qProcess.targetList, qTarget);

        query.where(qProcess.workspaceUUID.eq(workspaceUUID), qProcess.state.eq(State.CREATED), qProcess.state.eq(State.UPDATED), qTarget.data.eq(targetData));

        Process result = query.fetchOne();

        return Optional.ofNullable(result);
    }
}
