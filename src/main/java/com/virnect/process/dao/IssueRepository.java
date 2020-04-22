package com.virnect.process.dao;

import com.virnect.process.domain.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-02-14
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public interface IssueRepository extends JpaRepository<Issue, Long> {

    // JPQL
    @Query(value = "select I from Issue I where I.id = :issueId")
    Optional<Issue> getIssue(Long issueId);

//    Optional<Issue> findById(Long issueId);

    @Query(value = "select count(i.issue_id) from issue i join job j on j.job_id = i.job_id join sub_process s on s.sub_process_id = j.sub_process_id and s.sub_process_id = :subProcessId", nativeQuery = true)
    Long countIssuesInSubProcess(@Param("subProcessId") Long subProcessId);

    @Query(value = "select i.issue_id as issue_id, i.content as content, i.photo_file_path as photo_file_path, i.worker_uuid as worker_uuid, i.created_at as created_at, i.updated_at as updated_at, i.job_id as job_id from issue i join job j on j.job_id = i.job_id join sub_process s on s.sub_process_id = j.sub_process_id join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) where 1=1 and i.worker_uuid in (:userUUIDList)"
            , countQuery = "select count(*) from issue i join job j on j.job_id = i.job_id join sub_process s on s.sub_process_id = j.sub_process_id join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) where 1=1 and i.worker_uuid in (:userUUIDList)"
            , nativeQuery = true)
    Page<Issue> getIssuesInSearchUserName(@Param("workspaceUUID") String workspaceUUID, @Param("userUUIDList") List<String> userUUIDList, Pageable pageable);

    @Query(value = "select * from issue where 1=1 and job_id is null and i.worker_uuid in (:userUUIDList)"
            , countQuery = "select count(*) from issue where 1=1 and job_id is null and i.worker_uuid in (:userUUIDList)"
            , nativeQuery = true)
    Page<Issue> getIssuesOutSearchUserName(@Param("userUUIDList") List<String> userUUIDList, Pageable pageable);

    @Query(value = "select * from (select i.issue_id as issue_id, i.content as content, i.photo_file_path as photo_file_path, i.worker_uuid as worker_uuid, i.created_at as created_at, i.updated_at as updated_at, i.job_id as job_id from issue i join job j on j.job_id = i.job_id join sub_process s on s.sub_process_id = j.sub_process_id join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) where 1=1 and i.worker_uuid in (:userUUIDList) union all select issue_id, content, photo_file_path, worker_uuid, created_at, updated_at, job_id from issue where 1=1 and job_id is null and worker_uuid in (:userUUIDList)) as a"
            , countQuery = "select count(*) from (select i.updated_at as updated_at from issue i join job j on j.job_id = i.job_id join sub_process s on s.sub_process_id = j.sub_process_id join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) where 1=1 and i.worker_uuid in (:userUUIDList) union all select updated_at from issue where 1=1 and job_id is null and worker_uuid in (:userUUIDList)) as a"
            , nativeQuery = true)
    Page<Issue> getIssuesAllSearchUserName(@Param("workspaceUUID") String workspaceUUID, @Param("userUUIDList") List<String> userUUIDList, Pageable pageable);

    @Query(value = "select * from issue i join job j on j.job_id = i.job_id join sub_process s on s.sub_process_id = j.sub_process_id join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID))"
            , countQuery = "select count(*) from issue i join job j on j.job_id = i.job_id join sub_process s on s.sub_process_id = j.sub_process_id join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID))"
            , nativeQuery = true)
    Page<Issue> getIssuesIn(@Param("workspaceUUID") String workspaceUUID, Pageable pageable);

    @Query(value = "select * from issue where 1=1 and job_id is null"
            , countQuery = "select count(*) from issue where 1=1 and job_id is null"
            , nativeQuery = true)
    Page<Issue> getIssuesOut(Pageable pageable);

    @Query(value = "select * from (select i.issue_id as issue_id, i.content as content, i.photo_file_path as photo_file_path, i.worker_uuid as worker_uuid, i.created_at as created_at, i.updated_at as updated_at, i.job_id as job_id from issue i join job j on j.job_id = i.job_id join sub_process s on s.sub_process_id = j.sub_process_id join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) union all select issue_id, content, photo_file_path, worker_uuid, created_at, updated_at, job_id from issue where 1=1 and job_id is null) as a"
            , countQuery = "select count(*) from (select i.updated_at as updated_at from issue i join job j on j.job_id = i.job_id join sub_process s on s.sub_process_id = j.sub_process_id join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) union all select updated_at from issue where 1=1 and job_id is null) as a"
            , nativeQuery = true)
    Page<Issue> getIssuesAll(@Param("workspaceUUID") String workspaceUUID, Pageable pageable);

    @Query(value = "select * from issue i join job j on j.job_id = i.job_id join sub_process s on s.sub_process_id = j.sub_process_id join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) and ((:title is null) or (:title is not null and p.name like %:title%))"
            , countQuery = "select count(*) from issue i join job j on j.job_id = i.job_id join sub_process s on s.sub_process_id = j.sub_process_id join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID)) and ((:title is null) or (:title is not null and p.name like %:title%))"
            , nativeQuery = true)
    Page<Issue> getIssuesInSearchProcessTitle(@Param("workspaceUUID") String workspaceUUID, @Param("title") String title, Pageable pageable);

    @Query(value = "select * from issue i join job j on j.job_id = i.job_id join sub_process s on s.sub_process_id = j.sub_process_id and ((:title is null) or (:title is not null and s.name like %:title%)) join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID))"
            , countQuery = "select count(*) from issue i join job j on j.job_id = i.job_id join sub_process s on s.sub_process_id = j.sub_process_id and ((:title is null) or (:title is not null and s.name like %:title%)) join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID))"
            , nativeQuery = true)
    Page<Issue> getIssuesInSearchSubProcessTitle(@Param("workspaceUUID") String workspaceUUID, @Param("title") String title, Pageable pageable);

    @Query(value = "select * from issue i join job j on j.job_id = i.job_id and ((:title is null) or (:title is not null and j.name like %:title%)) join sub_process s on s.sub_process_id = j.sub_process_id join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID))"
            , countQuery = "select count(*) from issue i join job j on j.job_id = i.job_id and ((:title is null) or (:title is not null and j.name like %:title%)) join sub_process s on s.sub_process_id = j.sub_process_id join process p on p.process_id = s.process_id and ((:workspaceUUID is null) or (:workspaceUUID is not null and p.workspace_uuid like :workspaceUUID))"
            , nativeQuery = true)
    Page<Issue> getIssuesInSearchJobTitle(@Param("workspaceUUID") String workspaceUUID, @Param("title") String title, Pageable pageable);

    List<Issue> findByWorkerUUIDIsIn(List<String> workerUUIDList);

    List<Issue> findByWorkerUUIDIsInAndJobIsNull(List<String> workerUUIDList);
}
