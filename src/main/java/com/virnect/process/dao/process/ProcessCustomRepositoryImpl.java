package com.virnect.process.dao.process;

import com.virnect.process.domain.Process;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class ProcessCustomRepositoryImpl extends QuerydslRepositorySupport implements ProcessCustomRepository {
    public ProcessCustomRepositoryImpl() {
        super(Process.class);
    }
}
