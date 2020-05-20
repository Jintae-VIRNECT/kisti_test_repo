package com.virnect.process.dao.process;

import com.virnect.process.domain.Process;
import com.virnect.process.domain.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProcessCustomRepository {
    /**
     * 상태와 타겟데이터로 작업 찾기
     * @param targetData    타겟 데이터
     * @param state         작업 상태
     * @return
     */
    Process findByTargetDataAndState(String targetData, State state);

    /**
     *
     * @param workspaceUUID 워크스페이스UUID
     * @param title         작업명
     * @param pageable
     * @return
     */
    Page<Process> getProcessPageSearchUser(String workspaceUUID, String title, List<String> userUUIDList, Pageable pageable);

    /**
     * 특정 작업의 Issue 카운트
     * @param processId 작업ID
     * @return
     */
    int getCountIssuesInProcess(Long processId);

    /**
     * 종료되지 않은 작업 조회
     * @param workspaceUUID 워크스페이스UUID
     * @param targetData    타겟데이터
     * @return
     */
    Optional<Process> getProcessUnClosed(String workspaceUUID, String targetData);
}