package com.virnect.process.dao;

import com.virnect.process.domain.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-02-20
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query(value = "select * from job where sub_process_id = :subProcessId and ((:search is null) or (:search is not null and name like %:search%)) #sort"
            , nativeQuery = true)
    List<Job> getJobList(Long subProcessId, @Param("search") String search, Sort sort);

    @Query(value = "select * from job where sub_process_id = :subProcessId and ((:search is null) or (:search is not null and name like %:search%))"
            , countQuery = "select count(*) from job where sub_process_id = :subProcessId and ((:search is null) or (:search is not null and name like %:search%))"
            , nativeQuery = true)
    Page<Job> getJobs(Long subProcessId, @Param("search") String search, Pageable pageable);
}
