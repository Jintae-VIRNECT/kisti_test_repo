package com.virnect.process.dao.process;

import com.querydsl.jpa.JPQLQuery;
import com.virnect.process.domain.Process;
import com.virnect.process.domain.QProcess;
import com.virnect.process.domain.QTarget;
import com.virnect.process.domain.State;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Optional;

public class ProcessCustomRepositoryImpl extends QuerydslRepositorySupport implements ProcessCustomRepository {
    public ProcessCustomRepositoryImpl() {
        super(Process.class);
    }

    @Override
    public Optional<Process> findByTargetDataAndState(String targetData, State state) {
        QProcess qProcess = QProcess.process;
        QTarget qTarget = QTarget.target;
        JPQLQuery<Process> query = from(qProcess).where(qProcess.state.eq(state));
        query = query.join(qTarget).where(qTarget.data.eq(targetData));
        Process process = query.fetchOne();
        return Optional.empty();
    }
}
