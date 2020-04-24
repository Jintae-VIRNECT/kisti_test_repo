package com.virnect.process.dao.process;

import com.virnect.process.domain.Process;
import com.virnect.process.domain.State;

public interface ProcessCustomRepository {

    Process findByTargetDataAndState(String targetData, State state);
}
