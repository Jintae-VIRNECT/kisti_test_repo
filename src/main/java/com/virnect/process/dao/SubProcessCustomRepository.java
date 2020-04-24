package com.virnect.process.dao;

import com.virnect.process.domain.SubProcess;
import com.virnect.process.domain.YesOrNo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface SubProcessCustomRepository {
    Page<SubProcess> getMyWorks(String workerUUID, /*long processId, String search, */Pageable pageable);

    boolean existsByIsRecentAndWorkerUUIDAndWorkspaceUUID(YesOrNo newWork, String workspaceUUID, String workerUUID);

    Page<SubProcess> getSubProcesses(String workspaceUUID, Long processId, String search, List<String> userUUIDList, Pageable pageable);
}
