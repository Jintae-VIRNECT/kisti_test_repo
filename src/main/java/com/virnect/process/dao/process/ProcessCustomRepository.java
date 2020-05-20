package com.virnect.process.dao.process;

import com.virnect.process.domain.Process;
import com.virnect.process.domain.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProcessCustomRepository {

    Process findByTargetDataAndState(String targetData, State state);

    /**
     *
     * @param workspaceUUID 워크스페이스UUID
     * @param title         작업명
     * @param pageable
     * @return
     */
    Page<Process> getProcessPageSearchUser(String workspaceUUID, String title, Pageable pageable);
}
