package com.virnect.process.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.process.application.user.UserRestService;
import com.virnect.process.application.workspace.WorkspaceRestService;
import com.virnect.process.dao.IssueRepository;
import com.virnect.process.domain.Issue;
import com.virnect.process.domain.Job;
import com.virnect.process.domain.Process;
import com.virnect.process.domain.SubProcess;
import com.virnect.process.dto.request.TroubleMemoUploadRequest;
import com.virnect.process.dto.response.IssueInfoResponse;
import com.virnect.process.dto.response.IssuesResponse;
import com.virnect.process.dto.response.TroubleMemoUploadResponse;
import com.virnect.process.dto.rest.response.user.UserInfoListResponse;
import com.virnect.process.dto.rest.response.user.UserInfoResponse;
import com.virnect.process.dto.rest.response.workspace.MemberInfoDTO;
import com.virnect.process.dto.rest.response.workspace.MemberListResponse;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.common.PageMetadataResponse;
import com.virnect.process.global.error.ErrorCode;
import com.virnect.process.infra.file.FileUploadService;

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
public class IssueService {
	private final IssueRepository issueRepository;
	private final WorkspaceRestService workspaceRestService;
	private final UserRestService userRestService;
	private final FileUploadService fileUploadService;
	private static final LocalDateTime DEFATUL_LOCAL_DATE_TIME = LocalDateTime.parse("1500-01-01T00:00:00");

	/**
	 * 작업의 이슈 목록
	 *
	 * @param userUUID
	 * @param workspaceUUID
	 * @param search
	 * @param stepId
	 * @param pageable
	 * @return
	 */
	public ApiResponse<IssuesResponse> getIssuesIn(
		String userUUID, String workspaceUUID, String search, Long stepId, Pageable pageable
	) {

		List<String> userUUIDList = new ArrayList<>();

		if (Objects.nonNull(search) && Objects.nonNull(workspaceUUID)) {
			// 검색어로 워크스페이스 내 사용자 닉네임 및 이메일 검색
			List<UserInfoResponse> userInfos = getUserInfo(search, workspaceUUID);
			userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
		}

		Page<Issue> issuePage = this.issueRepository.getIssuesIn(
			userUUID, workspaceUUID, search, stepId, userUUIDList, pageable);

		return getIssuesResponseApiResponse(pageable, issuePage);
	}

	/**
	 * 워크스페이스의 이슈 목록 (troubleMemo)
	 *
	 * @param myUUID
	 * @param workspaceUUID
	 * @param search
	 * @param pageable
	 * @return
	 */
	public ApiResponse<IssuesResponse> getIssuesOut(
		String myUUID, String workspaceUUID, String search, Pageable pageable
	) {

		ApiResponse<MemberListResponse> memberListResponseApiResponse = workspaceRestService.getSimpleWorkspaceUserList(
			workspaceUUID);
		List<String> userUUIDList = memberListResponseApiResponse.getData()
			.getMemberInfoList()
			.stream()
			.map(MemberInfoDTO::getUuid)
			.collect(
				Collectors.toList());

		if (StringUtils.hasText(search)) {
			// 검색어로 워크스페이스 내 사용자의 이메일, 닉네임 검색
			ApiResponse<UserInfoListResponse> userInfoListResult = this.userRestService.getUserInfoSearchNickName(
				search, userUUIDList);

			if (!userInfoListResult.getData().getUserInfoList().isEmpty()) {
				userUUIDList = userInfoListResult.getData()
					.getUserInfoList()
					.stream()
					.map(UserInfoResponse::getUuid)
					.collect(
						Collectors.toList());
				search = null;//사용자의 이메일, 닉네임으로 검색되었을 때는 트러블 메모 내용명으로 검색하지 않는다.
			}
		}
		log.debug("{}", userUUIDList);

		Page<Issue> issuePage = this.issueRepository.getIssuesOut(myUUID, search, userUUIDList, pageable);

		return getIssuesResponseApiResponse(pageable, issuePage);
	}

	/**
	 * 이슈 상세 조회
	 *
	 * @param issueId
	 * @return
	 */
	public ApiResponse<IssueInfoResponse> getIssueInfo(Long issueId) {
		// 이슈 단건 조회
		Issue issue = this.issueRepository.findById(issueId)
			.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_ISSUE));

		Job job = issue.getJob();
		IssueInfoResponse issueInfoResponse = null;

		if (Objects.isNull(job)) {
			// global issue
			log.debug("JOB is NULL");
			ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(
				issue.getWorkerUUID());
			issueInfoResponse = IssueInfoResponse.builder()
				.issueId(issue.getId())
				.reportedDate(Optional.of(issue).map(Issue::getUpdatedDate).orElseGet(() -> DEFATUL_LOCAL_DATE_TIME))
				.photoFilePath(Optional.of(issue).map(Issue::getPath).orElseGet(() -> ""))
				.workerUUID(issue.getWorkerUUID())
				.workerName(userInfoResponse.getData().getNickname())
				.workerProfile(userInfoResponse.getData().getProfile())
				.caption(Optional.of(issue).map(Issue::getContent).orElseGet(() -> ""))
				.build();
		} else {
			// job issue
			log.debug("JOB is NOT!! NULL");
			SubProcess subProcess = Optional.ofNullable(job)
				.map(Job::getSubProcess)
				.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SUBPROCESS));
			Process process = Optional.ofNullable(subProcess)
				.map(SubProcess::getProcess)
				.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
			ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(
				subProcess.getWorkerUUID());
			issueInfoResponse = IssueInfoResponse.builder()
				.taskId(process.getId())
				.taskName(process.getName())
				.subTaskId(subProcess.getId())
				.subTaskName(subProcess.getName())
				.stepId(job.getId())
				.stepName(job.getName())
				.issueId(issue.getId())
				.reportedDate(subProcess.getReportedDate())
				.workerUUID(subProcess.getWorkerUUID())
				.workerName(userInfoResponse.getData().getNickname())
				.workerProfile(userInfoResponse.getData().getProfile())
				.photoFilePath(issue.getPath())
				.caption(issue.getContent())
				.build();
		}
		return new ApiResponse<>(issueInfoResponse);
	}

	/**
	 * 트러블 메모 업로드
	 *
	 * @param request
	 * @return
	 */
	public ApiResponse<TroubleMemoUploadResponse> uploadTroubleMemo(TroubleMemoUploadRequest request) {
		Issue issue = Issue.builder()
			.content(request.getCaption())
			.workerUUID(request.getWorkerUUID())
			.build();

		// Base64로 받은 이미지 처리
		if (!StringUtils.isEmpty(request.getPhotoFile())) {
			issue.setPath(getFileUploadUrl(request.getPhotoFile()));
		}

		this.issueRepository.save(issue);

		return new ApiResponse<>(new TroubleMemoUploadResponse(true, LocalDateTime.now()));
	}

	public ApiResponse<IssuesResponse> getIssuesAll(String workspaceUUID, Pageable pageable) {
		Page<Issue> issuePage = this.issueRepository.getIssuesAll(workspaceUUID, pageable);

		return getIssuesResponseApiResponse(pageable, issuePage);
	}

	private ApiResponse<IssuesResponse> getIssuesResponseApiResponse(Pageable pageable, Page<Issue> issuePage) {
		List<IssueInfoResponse> issueInfoResponseList = issuePage.stream().map(issue -> {
			Job job = Optional.of(issue).map(Issue::getJob).orElseGet(() -> null);
			SubProcess subProcess = Optional.of(issue).map(Issue::getJob).map(Job::getSubProcess).orElseGet(() -> null);
			Process process = Optional.of(issue)
				.map(Issue::getJob)
				.map(Job::getSubProcess)
				.map(SubProcess::getProcess)
				.orElseGet(() -> null);
			ApiResponse<UserInfoResponse> userInfoResponse =
				this.userRestService.getUserInfoByUserUUID(Optional.of(issue).map(Issue::getWorkerUUID).orElseGet(() ->
					Optional.ofNullable(subProcess).map(SubProcess::getWorkerUUID).orElseGet(() -> null)));
			UserInfoResponse userInfo = userInfoResponse.getData();
			// 이슈가 글로벌 이슈인 경우 process, subProcess, job 이 null 이므로 그에 맞는 null 체크
			return IssueInfoResponse.builder()
				.issueId(issue.getId())
				.reportedDate(Objects.isNull(subProcess) ? issue.getUpdatedDate() : subProcess.getReportedDate())
				.photoFilePath(Optional.of(issue).map(Issue::getPath).orElseGet(() -> ""))
				.caption(Optional.of(issue).map(Issue::getContent).orElseGet(() -> ""))
				.taskId(Objects.isNull(process) ? 0 : process.getId())
				.taskName(Objects.isNull(process) ? null : process.getName())
				.subTaskId(Objects.isNull(subProcess) ? 0 : subProcess.getId())
				.subTaskName(Objects.isNull(subProcess) ? null : subProcess.getName())
				.stepId(Objects.isNull(job) ? 0 : job.getId())
				.stepName(Objects.isNull(job) ? null : job.getName())
				.workerUUID(userInfo.getUuid())
				.workerName(userInfo.getNickname())
				.workerProfile(userInfo.getProfile())
				.build();
		}).collect(Collectors.toList());

		PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.totalPage(issuePage.getTotalPages())
			.totalElements(issuePage.getTotalElements())
			.build();
		return new ApiResponse<>(new IssuesResponse(issueInfoResponseList, pageMetadataResponse));
	}

	/**
	 * 워크스페이스 내 사용자 검색(닉네임, 이메일)
	 *
	 * @param search
	 * @zparam workspaceId
	 * @return
	 */
	private List<UserInfoResponse> getUserInfo(String search, String workspaceId) {

		ApiResponse<MemberListResponse> userList = workspaceRestService.getSimpleWorkspaceUserList(workspaceId);
		List<String> userUUIDs = new ArrayList<>();

		for (MemberInfoDTO dto : userList.getData().getMemberInfoList()) {
			userUUIDs.add(dto.getUuid());
		}

		ApiResponse<UserInfoListResponse> userInfoListResult = this.userRestService.getUserInfoSearchNickName(
			search, userUUIDs);

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

	/**
	 * base64로 인코딩된 이미지 파일 업로드
	 *
	 * @param base64EncodedImage - upload file
	 * @return - file path
	 */
	private String getFileUploadUrl(String base64EncodedImage) {
		return Optional.of(fileUploadService.base64ImageUpload(base64EncodedImage))
			.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_PROCESS_WORK_RESULT_SYNC));
	}
}
