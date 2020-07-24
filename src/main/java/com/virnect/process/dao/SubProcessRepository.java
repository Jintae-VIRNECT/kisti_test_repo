package com.virnect.process.dao;

import com.virnect.process.domain.Process;
import com.virnect.process.domain.SubProcess;
import com.virnect.process.domain.YesOrNo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SubProcessRepository extends JpaRepository<SubProcess, Long>, SubProcessCustomRepository {
    boolean existsByIsRecentAndWorkerUUID(YesOrNo yesOrNo, String workerUUID);

    Optional<SubProcess> findById(Long subProcessId);

    // TODO : Done 상태에 대한 정의가 미비한 부분 수정필요.
    @Query(value = "select count(*) from process p left join sub_process s on p.process_id = s.process_id"/* and s.status = 'done'*/, nativeQuery = true)
    int getDoneCount(Long id);

//    // TODO : jobTotal, status, progressRate 추가 필요
//    @Query(value = "select * from sub_process s join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) where 1=1 and ((:processId is null) or (:processId is not null and s.process_id = :processId)) and (((:search is null) or (:search is not null and s.name like %:search%)) or s.worker_uuid in (:userUUIDList)) #sort"
//            , nativeQuery = true)
//    List<SubProcess> selectSubProcessList(String workspaceUUID, Long processId, @Param("search") String search, @Param("userUUIDList") List<String> userUUIDList, Sort sort);
//
//    // TODO : jobTotal, status, progressRate 추가 필요
//    @Query(value = "select * from sub_process s join process p on p.process_id = s.process_id and p.process_id = :processId and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) where 1=1 and (((:search is null) or (:search is not null and s.name like %:search%)) or s.worker_uuid in (:userUUIDList))"
//            , countQuery = "select count(*) from sub_process s join process p on p.process_id = s.process_id and p.process_id = :processId and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) where 1=1 and (((:search is null) or (:search is not null and s.name like %:search%)) or s.worker_uuid in (:userUUIDList))"
//            , nativeQuery = true)
//    Page<SubProcess> selectSubProcesses(String workspaceUUID, Long processId, @Param("search") String search, @Param("userUUIDList") List<String> userUUIDList, Pageable pageable);

//    @Query(value = "select * from sub_process s join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) and strcmp(p.state, 'CLOSED') != 0 and strcmp(p.state, 'DELETED') != 0 where s.worker_uuid = :worker and ((:processId is null) or (:processId is not null and s.process_id = :processId)) and ((:search is null) or (:search is not null and s.name like %:search%))"
//            , countQuery = "select count(*) from sub_process s join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) and strcmp(p.state, 'CLOSED') != 0 and strcmp(p.state, 'DELETED') != 0 where s.worker_uuid = :worker and ((:processId is null) or (:processId is not null and s.process_id = :processId)) and ((:search is null) or (:search is not null and s.name like %:search%))"
//            , nativeQuery = true)
//    Page<SubProcess> getMyWorksInProcess(String workspaceUUID, @Param("worker") String workerUUID, Long processId, @Param("search") String search, Pageable pageable);

    // TODO : process id, search 필터 필요
    Page<SubProcess> findByWorkerUUID(String workerUUID, Pageable pageable);

    List<SubProcess> findByProcessAndIdIsIn(Process process, Set<Long> subProcessIdList);

    List<SubProcess> findByWorkerUUID(String workerUUID);
}
