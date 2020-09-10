package com.virnect.process.application;

import com.virnect.process.dao.JobRepository;
import com.virnect.process.dao.SubProcessRepository;
import com.virnect.process.dao.process.ProcessRepository;
import com.virnect.process.domain.*;
import com.virnect.process.domain.Process;
import com.virnect.process.dto.response.JobListResponse;
import com.virnect.process.dto.response.JobResponse;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.common.PageMetadataResponse;
import com.virnect.process.global.error.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-08-27
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Service
@RequiredArgsConstructor
public class StepService {
    private final ProcessRepository processRepository;
    private final SubProcessRepository subProcessRepository;
    private final JobRepository jobRepository;
    /**
     * 단계 목록 조회
     *
     * @param userUUID
     * @param subProcessId
     * @param search
     * @param filter
     * @param pageable
     * @return
     */
    public ApiResponse<JobListResponse> getJobs(String myUUID, Long subProcessId, String search, List<Conditions> filter, Pageable pageable) {
        // 하위 작업 단건 조회
        SubProcess subProcess = this.subProcessRepository.findById(subProcessId)
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SUBPROCESS));

        // 작업 단건 조회
        Process process = this.processRepository.findById(subProcess.getProcess().getId())
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));

        Page<Job> jobPage = null;

        jobPage = this.jobRepository.getJobPage(myUUID, subProcessId, search, pageable);

        if (filter != null && filter.size() > 0 && !filter.contains(Conditions.ALL)) {
            jobPage = filterConditionsJobPage(jobPage, filter, pageable);
        }

        List<JobResponse> jobList = jobPage.getContent().stream().map(job -> {
            JobResponse jobResponse = JobResponse.builder()
                    .id(job.getId())
                    .name(job.getName())
                    .priority(job.getPriority())
                    .reportedDate(subProcess.getReportedDate())
                    .progressRate(job.getProgressRate())
                    .conditions(job.getConditions())
                    .build();

            //report도 issue 처럼 job 하위에 여러개 생성할 수 있으므로 report도 리스트로 리턴하는 것으로 수정함.(VECHOSYS-1287)
            List<JobResponse.Paper> jobPaperList = new ArrayList<>();
            if (job.getReportList().size() > 0) {
                for (Report report : job.getReportList()) {
                    JobResponse.Paper jobPaper = JobResponse.Paper.builder()
                            .id(report.getId())
                            .build();
                    jobPaperList.add(jobPaper);
                }

            }
            List<JobResponse.Issue> jobIssueList = new ArrayList<>();

            if (job.getIssueList().size() > 0) {

                for (Issue issue : job.getIssueList()) {
                    JobResponse.Issue jobIssue = JobResponse.Issue.builder()
                            .issueId(issue.getId())
                            .caption(issue.getContent())
                            .photoFilePath(issue.getPath())
                            .workerUUID(issue.getWorkerUUID())
                            .build();

                    jobIssueList.add(jobIssue);
                }
            }
            // null 값이 아닌 [] 값을 리턴하기 위해 밖으로 뺌.
            jobResponse.setIssueList(jobIssueList);
            jobResponse.setPaperList(jobPaperList);
            return jobResponse;
        }).collect(Collectors.toList());

        PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
                .currentPage(pageable.getPageNumber())
                .currentSize(pageable.getPageSize())
                .totalPage(jobPage.getTotalPages())
                .totalElements(jobPage.getTotalElements())
                .build();

        JobListResponse jobListResponse = JobListResponse.builder()
                .taskId(process.getId())
                .taskName(process.getName())
                .subTaskId(subProcessId)
                .subTaskName(subProcess.getName())
                .steps(jobList)
                .pageMeta(pageMetadataResponse)
                .build();
        return new ApiResponse<>(jobListResponse);
    }

    /**
     * 단계 컨디션 필터링
     *
     * @param jobPage
     * @param filter
     * @param pageable
     * @return
     */
    private Page<Job> filterConditionsJobPage(Page<Job> jobPage, List<Conditions> filter, Pageable pageable) {
        List<Job> jobs = new ArrayList<>();
        for (Job job : jobPage) {
            // 상태가 일치하는 단계만 필터링
            if (filter.contains(job.getConditions())) {
                jobs.add(job);
            }
        }

        return new PageImpl<>(jobs, pageable, jobs.size());
    }
}
