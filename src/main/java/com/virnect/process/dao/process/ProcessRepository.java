package com.virnect.process.dao.process;

import com.virnect.process.domain.Process;
import com.virnect.process.domain.State;
import com.virnect.process.dto.response.ProcessesStatisticsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProcessRepository extends JpaRepository<Process, Long>, ProcessCustomRepository {
    Optional<Process> findById(String id);

//    Long deleteById(Long processId);

    List<Process> findByContentUUID(String contentUUID);

    @Query(value = "SELECT\n" +
            "(SELECT '0') AS total_rate,\n" +
            "(SELECT count(NAME) FROM process WHERE conditions =            \"(SELECT count(NAME) FROM process WHERE conditions = 'WAIT') AS category_wait,\\n\" +\n 'UNPROGRESSNIG' OR conditions = 'PROGRESSING' OR conditions = 'COMPLETED' OR conditions = 'INCOMPLETED') AS category_started,\n" +
            "(SELECT count(NAME) FROM process WHERE conditions = 'FAILED' OR conditions = 'SUCCESS' OR conditions = 'FAULT') AS category_ended,\n" +
            "(SELECT count(NAME) FROM process WHERE conditions = 'WAIT') AS WAIT,\n" +
            "(SELECT count(NAME) FROM process WHERE conditions = 'UNPROGRESSNIG') AS unprogressnig,\n" +
            "(SELECT count(NAME) FROM process WHERE conditions = 'PROGRESSING') AS progressing,\n" +
            "(SELECT count(NAME) FROM process WHERE conditions = 'COMPLETED') AS completed,\n" +
            "(SELECT count(NAME) FROM process WHERE conditions = 'INCOMPLETED') AS incompleted,\n" +
            "(SELECT count(NAME) FROM process WHERE conditions = 'FAILED') AS failed,\n" +
            "(SELECT count(NAME) FROM process WHERE conditions = 'SUCCESS') AS success,\n" +
            "(SELECT count(NAME) FROM process WHERE conditions = 'FAULT') AS fault\n"
            , nativeQuery = true)
    ProcessesStatisticsResponse getStatistics();

    @Query(value = "SELECT count(NAME) FROM process WHERE conditions = 'UNPROGRESSING' OR conditions = 'PROGRESSING' OR conditions = 'COMPLETED' OR conditions = 'INCOMPLETED'", nativeQuery = true)
    int getCountStarted();

    @Query(value = "SELECT count(NAME) FROM process WHERE conditions = 'FAILED' OR conditions = 'SUCCESS' OR conditions = 'FAULT'", nativeQuery = true)
    int getCountEnded();

    @Query(value = "SELECT count(NAME) FROM process WHERE conditions = :conditions", nativeQuery = true)
    int getCountConditions(@Param("conditions") String conditions);

    @Query(value = "select * from process p join Target t on t.process_id = p.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) where t.data = :targetData and not (state <=> 'CLOSED') and not (state <=> 'DELETED')"
            , countQuery = "select count(*) from process p join Target t on t.process_id = p.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) where t.data = :targetData and not (state <=> 'CLOSED') and not (state <=> 'DELETED')"
            , nativeQuery = true)
    Optional<Process> getProcessUnClosed(String workspaceUUID, String targetData);

    @Query(value = "select * from process where 1=1 and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID))"
            , countQuery = "select count(*) from process where 1=1 and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID))"
            , nativeQuery = true)
    List<Process> getProcesses(String workspaceUUID);

    @Query(value = "SELECT p.process_id, p.name, p.position, p.state, p.start_date, p.end_date, p.reported_date, p.content_uuid, p.content_manager_uuid, p.workspace_uuid, p.created_at, p.updated_at from process p join sub_process s on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) WHERE 1=1 and (((:title is null) or (:title is not null and p.name like %:title%)) or s.worker_uuid in (:userUUIDList)) group by p.process_id #sort"
            , nativeQuery = true)
    List<Process> getProcessListSearchUser(String workspaceUUID, @Param("title") String title, @Param("userUUIDList") List<String> userUUIDList, Sort sort);

    @Query(value = "SELECT p.process_id, p.name, p.position, p.state, p.start_date, p.end_date, p.reported_date, p.content_uuid, p.content_manager_uuid, p.workspace_uuid, t.data, p.created_at, p.updated_at from process p join target t on t.process_id = p.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) join sub_process s on p.process_id = s.process_id WHERE 1=1 and (((:title is null) or (:title is not null and p.name like %:title%)) or s.worker_uuid in (:userUUIDList)) group by p.process_id, t.data"
            , countQuery = "select count(*) from process p join target t on t.process_id = p.process_id join sub_process s on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) WHERE 1=1 and (((:title is null) or (:title is not null and p.name like %:title%)) or s.worker_uuid in (:userUUIDList)) group by p.process_id, t.data"
            , nativeQuery = true)
    Page<Process> getProcessPageSearchUser(String workspaceUUID, @Param("title") String title, @Param("userUUIDList") List<String> userUUIDList, Pageable pageable);

    @Query(value = "select * from (SELECT p.process_id, p.name, p.position, p.state, p.start_date, p.end_date, p.reported_date, p.created_at, p.updated_at FROM process p JOIN sub_process s ON p.process_id = s.process_id AND STRCMP(p.state, 'CLOSED') = 0 or STRCMP(p.state, 'DELETED') = 0 WHERE 1=1 and (((:title is null) or (:title is not null and p.name like %:title%)) or s.worker_uuid in (:userUUIDList)) group by p.process_id) a ORDER BY :orderStr :directionName, updated_at DESC", nativeQuery = true)
    List<Process> getProcessClosedList(@Param("title") String title, @Param("userUUIDList") List<String> userUUIDList, String orderStr, String directionName);

    @Query(value = "select * from (SELECT p.process_id, p.name, p.position, p.state, p.start_date, p.end_date, p.reported_date, p.created_at, p.updated_at FROM process p JOIN sub_process s ON p.process_id = s.process_id AND STRCMP(p.state, 'CLOSED') != 0 AND STRCMP(p.state, 'DELETED') != 0 WHERE 1=1 and (((:title is null) or (:title is not null and p.name like %:title%)) or s.worker_uuid in (:userUUIDList)) group by p.process_id) a ORDER BY :orderStr :directionName, updated_at DESC", nativeQuery = true)
    List<Process> getProcessList(@Param("title") String title, @Param("userUUIDList") List<String> userUUIDList, String orderStr, String directionName);

    @Query(value = "select count(*) from process p left join sub_process s on p.process_id = s.process_id", nativeQuery = true)
    int getSubProcessesTotal(Long id);

    @Query(value = "select * from process where process_id = :processId", nativeQuery = true)
    Optional<Process> getProcessInfo(@Param("processId") Long processId);

    @Query(value = "select count(i.issue_id) from process p join sub_process s on s.process_id = p.process_id and s.process_id = :processId join job j on j.sub_process_id = s.sub_process_id join issue i on i.job_id = j.job_id where i.job_id is not null", nativeQuery = true)
    int getCountIssuesInProcess(@Param("processId") Long processId);
}
