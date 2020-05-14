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
    public Page<Process> getProcessPageSearchUser(String workspaceUUID, String title, List<String> userUUIDList, Pageable pageable){
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

        if (userUUIDList != null)
        {
            query = query.where(qSubProcess.workerUUID.in(userUUIDList));
        }

        if (title != null)
        {
            query = query.where(qProcess.name.contains(title));
        }

        query.groupBy(qProcess.id, qTarget.data);

        log.debug(">>>>>>>>>>>> query : {} ", query);

        // 중복 제거
        final List<Process> processList = getQuerydsl().applyPagination(pageable, query).fetch(); //.stream().distinct().collect(Collectors.toList());

        log.debug(">>>>>>>>>>>> fetchResults : {}", query.fetchResults());
        return new PageImpl<>(processList, pageable, query.fetchCount());

        //return query.fetch();
    }

}
