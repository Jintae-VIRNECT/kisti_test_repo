package com.virnect.process.dao.process;

import com.virnect.process.domain.Process;
import com.virnect.process.domain.State;

import java.util.Optional;

public interface ProcessCustomRepository {

    Optional<Process> findByTargetDataAndState(String targetData, State state);
}
