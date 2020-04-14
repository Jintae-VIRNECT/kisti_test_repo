package com.virnect.process.dao;

import com.virnect.process.domain.SmartTool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-02-20
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public interface SmartToolRepository extends JpaRepository<SmartTool, Long> {
    Page<SmartTool> findSmartToolsByJob(Long processId, Long subProcessId, Pageable pageable);

    @Query(value = "select * from sub_process s join process p on s.process_id = p.process_id and ((:reportedFilter is null or :reportedFilter is not true) or (:reportedFilter is not null and :reportedFilter is true and s.reported_date is not null)) join job j on j.sub_process_id = s.sub_process_id and ((:subProcessId is null) or (:subProcessId is not null and j.sub_process_id = :subProcessId and s.sub_process_id = :subProcessId)) join smart_tool st on st.job_id = j.job_id where 1=1 and ((:search is null) or (:search is not null and st.smart_tool_job_id like %:search%))"
            , countQuery = "select count(*) from sub_process s join process p on s.process_id = p.process_id and ((:reportedFilter is null or :reportedFilter is not true) or (:reportedFilter is not null and :reportedFilter is true and s.reported_date is not null)) join job j on j.sub_process_id = s.sub_process_id and ((:subProcessId is null) or (:subProcessId is not null and j.sub_process_id = :subProcessId and s.sub_process_id = :subProcessId)) join smart_tool st on st.job_id = j.job_id where 1=1 and ((:search is null) or (:search is not null and st.smart_tool_job_id like %:search%))"
            , nativeQuery = true
    )
    Page<SmartTool> getSmartToolJobs(Long subProcessId, @Param("search") String search, Boolean reportedFilter, Pageable pageable);
}
