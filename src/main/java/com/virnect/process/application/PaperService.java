package com.virnect.process.application;

import com.virnect.process.application.user.UserRestService;
import com.virnect.process.application.workspace.WorkspaceRestService;
import com.virnect.process.dao.ItemRepository;
import com.virnect.process.dao.ReportRepository;
import com.virnect.process.domain.Process;
import com.virnect.process.domain.*;
import com.virnect.process.dto.response.ItemResponse;
import com.virnect.process.dto.response.ReportInfoResponse;
import com.virnect.process.dto.response.ReportsResponse;
import com.virnect.process.dto.rest.response.user.UserInfoListResponse;
import com.virnect.process.dto.rest.response.user.UserInfoResponse;
import com.virnect.process.dto.rest.response.workspace.MemberInfoDTO;
import com.virnect.process.dto.rest.response.workspace.MemberListResponse;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.common.PageMetadataResponse;
import com.virnect.process.global.error.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-08-27
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaperService {
    private final ReportRepository reportRepository;
    private final UserRestService userRestService;
    private final ItemRepository itemRepository;
    private final WorkspaceRestService workspaceRestService;

    /**
     * 페이지 상세 조회
     *
     * @param reportId
     * @return
     */
    public ApiResponse<ReportInfoResponse> getReportInfo(Long reportId) {
        // 페이지 단건 조회 (리포트 -> 페이지로 명칭변경)
        Report report = this.reportRepository.findById(reportId).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_REPORT));
        Job job = Optional.of(report).map(Report::getJob).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_JOB));
        SubProcess subProcess = Optional.ofNullable(job).map(Job::getSubProcess).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SUBPROCESS));
        Process process = Optional.ofNullable(subProcess).map(SubProcess::getProcess).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
        ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(subProcess.getWorkerUUID());

        ReportInfoResponse reportInfoResponse = ReportInfoResponse.builder()
                .taskId(process.getId())
                .taskName(process.getName())
                .subTaskId(subProcess.getId())
                .subTaskName(subProcess.getName())
                .stepId(job.getId())
                .stepName(job.getName())
                .paperId(report.getId())
                .reportedDate(subProcess.getReportedDate())
                .workerUUID(subProcess.getWorkerUUID())
                .workerName(userInfoResponse.getData().getNickname())
                .workerProfile(userInfoResponse.getData().getProfile())
                .paperActions(report.getItemList().stream().map(item -> {
                    return ItemResponse.builder()
                            .id(item.getId())
                            .title(item.getTitle())
                            .type(item.getType())
                            .answer(item.getAnswer())
                            .priority(item.getPriority())
                            .photoFilePath(item.getPath())
                            .build();
                }).collect(Collectors.toList()))
                .build();
        return new ApiResponse<>(reportInfoResponse);
    }

    /**
     * 페이퍼 목록 조회
     *
     * @param userUUID
     * @param workspaceUUID
     * @param processId
     * @param subProcessId
     * @param search
     * @param reported
     * @param pageable
     * @return
     */
    public ApiResponse<ReportsResponse> getReports(String myUUID, String workspaceUUID, Long processId, Long subProcessId, String search, Boolean reported, Pageable pageable, Long stepId) {

        List<String> userUUIDList = new ArrayList<>();

        if (Objects.nonNull(search) && Objects.nonNull(workspaceUUID)) {
            // 사용자 검색 (이메일 및 닉네임으로 검색)
            List<UserInfoResponse> userInfos = getUserInfo(search, workspaceUUID);
            userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
        }
        Page<Report> reportPage = this.reportRepository.getPages(myUUID, workspaceUUID, processId, subProcessId, search, userUUIDList, reported, pageable, stepId);

        List<ReportInfoResponse> reportInfoResponseList = reportPage.stream().map(report -> {
            List<Item> items = Optional.ofNullable(this.itemRepository.findByReport(report)).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_REPORT_ITEM));
            // 리포트의 아이템
            List<ItemResponse> itemResponseList = items.stream().map(item -> {
                return ItemResponse.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .type(item.getType())
                        .answer(Optional.of(item).map(Item::getAnswer).orElseGet(() -> ""))
                        .priority(item.getPriority())
                        .photoFilePath(Optional.of(item).map(Item::getPath).orElseGet(() -> ""))
                        .build();
            }).collect(Collectors.toList());
            Job job = Optional.of(report).map(Report::getJob).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_JOB));
            SubProcess subProcess = Optional.ofNullable(job).map(Job::getSubProcess).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SUBPROCESS));
            Process process = Optional.ofNullable(subProcess).map(SubProcess::getProcess).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
            ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(subProcess.getWorkerUUID());
            return ReportInfoResponse.builder()
                    .taskId(process.getId())
                    .subTaskId(subProcess.getId())
                    .stepId(job.getId())
                    .paperId(report.getId())
                    .reportedDate(report.getJob().getSubProcess().getReportedDate())
                    .taskName(process.getName())
                    .subTaskName(subProcess.getName())
                    .stepName(job.getName())
                    .workerUUID(userInfoResponse.getData().getUuid())
                    .workerName(userInfoResponse.getData().getNickname())
                    .workerProfile(userInfoResponse.getData().getProfile())
                    .paperActions(itemResponseList)
                    .build();
        }).collect(Collectors.toList());

        PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
                .currentPage(pageable.getPageNumber())
                .currentSize(pageable.getPageSize())
                .totalPage(reportPage.getTotalPages())
                .totalElements(reportPage.getTotalElements())
                .build();
        return new ApiResponse<>(new ReportsResponse(reportInfoResponseList, pageMetadataResponse));
    }

    /**
     * 워크스페이스 내 사용자 검색(닉네임, 이메일)
     *
     * @param search
     * @param workspaceId
     * @return
     */
    private List<UserInfoResponse> getUserInfo(String search, String workspaceId) {

        ApiResponse<MemberListResponse> userList = workspaceRestService.getSimpleWorkspaceUserList(workspaceId);
        List<String> userUUIDs = new ArrayList<>();

        for (MemberInfoDTO dto : userList.getData().getMemberInfoList()) {
            userUUIDs.add(dto.getUuid());
        }

        ApiResponse<UserInfoListResponse> userInfoListResult = this.userRestService.getUserInfoSearchNickName(search, userUUIDs);

        List<UserInfoResponse> userInfoResponses = new ArrayList<>();

        if (userInfoListResult != null) {
            UserInfoListResponse userInfoList = userInfoListResult.getData();
            userInfoResponses = userInfoList.getUserInfoList();
            log.info("GET USER INFO BY SEARCH KEYWORD: [{}]", userInfoList.getUserInfoList());
        } else {
            log.info("GET USER INFO BY SEARCH is null");
        }

        return userInfoResponses;
    }

}
