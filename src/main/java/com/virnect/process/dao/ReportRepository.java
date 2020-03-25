package com.virnect.process.dao;

import com.virnect.process.domain.Report;
import com.virnect.process.dto.response.HourlyReportCountOfaDayResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Project: PF-SMIC_CUSTOM
 * DATE: 2020-02-14
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public interface ReportRepository extends JpaRepository<Report, Long> {

    //    @Query(value = "select * from report where report_id = :reportId", nativeQuery = true)
    @Query(value = "select R from Report R where R.id = :reportId")
    Optional<Report> getReport(Long reportId);

//    Optional<Report> findById(Long reportId);

    @Query(value = "select hour, count(1) as reportCount from (" +
            "select(" +
            "case when to_char(r.updated_at, 'HH') = '00' then '00'" +
            " case when to_char(r.updated_at, 'HH') = '01' then '01'" +
            " case when to_char(r.updated_at, 'HH') = '02' then '02'" +
            " case when to_char(r.updated_at, 'HH') = '03' then '03'" +
            " case when to_char(r.updated_at, 'HH') = '04' then '04'" +
            " case when to_char(r.updated_at, 'HH') = '05' then '05'" +
            " case when to_char(r.updated_at, 'HH') = '06' then '06'" +
            " case when to_char(r.updated_at, 'HH') = '07' then '07'" +
            " case when to_char(r.updated_at, 'HH') = '08' then '08'" +
            " case when to_char(r.updated_at, 'HH') = '09' then '09'" +
            " case when to_char(r.updated_at, 'HH') = '10' then '10'" +
            " case when to_char(r.updated_at, 'HH') = '11' then '11'" +
            " case when to_char(r.updated_at, 'HH') = '12' then '12'" +
            " case when to_char(r.updated_at, 'HH') = '13' then '13'" +
            " case when to_char(r.updated_at, 'HH') = '14' then '14'" +
            " case when to_char(r.updated_at, 'HH') = '15' then '15'" +
            " case when to_char(r.updated_at, 'HH') = '16' then '16'" +
            " case when to_char(r.updated_at, 'HH') = '17' then '17'" +
            " case when to_char(r.updated_at, 'HH') = '18' then '18'" +
            " case when to_char(r.updated_at, 'HH') = '19' then '19'" +
            " case when to_char(r.updated_at, 'HH') = '20' then '20'" +
            " case when to_char(r.updated_at, 'HH') = '21' then '21'" +
            " case when to_char(r.updated_at, 'HH') = '22' then '22'" +
            " case when to_char(r.updated_at, 'HH') = '23' then '23'" +
            " else '' end) as hourly" +
            " from (select * from job j left join report r on r.job_id = j.job_id where date(r.updated_at) = date(:targetDate))" +
            ") group by hourly order by hourly",
            nativeQuery = true)
    List<HourlyReportCountOfaDayResponse> selectHourlyReports(String targetDate);

    // 검색어는 어떤 용도로 사용할지 애매함 : 리포트 아이템의 질문명을 검색해야할지...UI상에는 없는 것으로 판단되어 일단 구현하지 않음
    @Query(value = "select * from sub_process s join process p on s.process_id = p.process_id and ((:reported is null or :reported is not true) or (:reported is not null and :reported is true and s.reported_date is not null)) and ((:processId is null) or (:processId is not null and p.process_id = :processId and s.process_id = :processId)) join job j on j.sub_process_id = s.sub_process_id and ((:subProcessId is null) or (:subProcessId is not null and j.sub_process_id = :subProcessId and s.sub_process_id = :subProcessId)) join report r on r.job_id = j.job_id"
            , countQuery = "select count(*) from sub_process s join process p on s.process_id = p.process_id and ((:reported is null or :reported is not true) or (:reported is not null and :reported is true and s.reported_date is not null)) and ((:processId is null) or (:processId is not null and p.process_id = :processId and s.process_id = :processId)) join job j on j.sub_process_id = s.sub_process_id and ((:subProcessId is null) or (:subProcessId is not null and j.sub_process_id = :subProcessId and s.sub_process_id = :subProcessId)) join report r on r.job_id = j.job_id"
            , nativeQuery = true)
    Page<Report> getReports(Long processId, Long subProcessId/*, @Param("search") String search*/, Boolean reported, Pageable pageable);
}
