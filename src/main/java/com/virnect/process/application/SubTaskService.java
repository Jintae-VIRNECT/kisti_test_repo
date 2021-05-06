package com.virnect.process.application;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.process.application.content.ContentRestService;
import com.virnect.process.application.user.UserRestService;
import com.virnect.process.application.workspace.WorkspaceRestService;
import com.virnect.process.dao.IssueRepository;
import com.virnect.process.dao.SubProcessRepository;
import com.virnect.process.dao.process.ProcessRepository;
import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.Job;
import com.virnect.process.domain.Process;
import com.virnect.process.domain.State;
import com.virnect.process.domain.SubProcess;
import com.virnect.process.domain.YesOrNo;
import com.virnect.process.dto.request.EditSubProcessRequest;
import com.virnect.process.dto.response.EditSubProcessResponse;
import com.virnect.process.dto.response.MyWorkListResponse;
import com.virnect.process.dto.response.MyWorksResponse;
import com.virnect.process.dto.response.SubProcessInfoResponse;
import com.virnect.process.dto.response.SubProcessListResponse;
import com.virnect.process.dto.response.SubProcessOfTargetResponse;
import com.virnect.process.dto.response.SubProcessReportedResponse;
import com.virnect.process.dto.response.SubProcessesOfTargetResponse;
import com.virnect.process.dto.response.SubProcessesResponse;
import com.virnect.process.dto.rest.response.user.UserInfoListResponse;
import com.virnect.process.dto.rest.response.user.UserInfoResponse;
import com.virnect.process.dto.rest.response.workspace.MemberInfoDTO;
import com.virnect.process.dto.rest.response.workspace.MemberListResponse;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.common.PageMetadataResponse;
import com.virnect.process.global.common.ResponseMessage;
import com.virnect.process.global.error.ErrorCode;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-08-27
 * AUTHOR: jkleee (Jukyoung Lee)
 *  * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubTaskService {
	private final ProcessRepository processRepository;
	private final SubProcessRepository subProcessRepository;
	private final UserRestService userRestService;
	private final IssueRepository issueRepository;
	private final ContentRestService contentRestService;
	private final WorkspaceRestService workspaceRestService;

	/**
	 * 작업 내 하위작업 목록
	 *
	 * @param processId
	 * @param workspaceUUID
	 * @param search
	 * @param userUUID
	 * @param filter
	 * @param pageable
	 * @return
	 */
	public ApiResponse<SubProcessListResponse> getSubProcessList(
		Long processId, String workspaceUUID, String search, String userUUID, List<Conditions> filter, Pageable pageable
	) {
		// 작업 정보 단건 조회
		Process process = this.processRepository.findById(processId)
			.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));

		String processName = process.getName();
		State processState = process.getState();
		String contentUUID = process.getContentUUID();

		List<String> userUUIDList = new ArrayList<>();

		if (Objects.nonNull(search) && Objects.nonNull(workspaceUUID)) {
			// 사용자 닉네임, 이메일을 검색
			List<UserInfoResponse> userInfos = getUserInfo(search, workspaceUUID);
			userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
		}

		Page<SubProcess> subProcessPage = null;

		// 하위 작업 조회
		subProcessPage = this.subProcessRepository.getSubProcessPage(
			workspaceUUID, processId, search, userUUIDList, pageable);

		if (filter != null && filter.size() > 0 && !filter.contains(Conditions.ALL)) {
			subProcessPage = filterConditionsSubProcessPage(subProcessPage, filter, pageable);
		}

		List<EditSubProcessResponse> editSubProcessResponseList = subProcessPage.stream().map(subProcess -> {
			ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(
				subProcess.getWorkerUUID());
			return EditSubProcessResponse.builder()
				.subTaskId(subProcess.getId())
				.priority(subProcess.getPriority())
				.subTaskName(subProcess.getName())
				.stepTotal(subProcess.getJobList().size())
				.conditions(Optional.of(subProcess).map(SubProcess::getConditions).orElseGet(() -> Conditions.WAIT))
				.startDate(Optional.of(subProcess)
					.map(SubProcess::getStartDate)
					.orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
				.endDate(Optional.of(subProcess)
					.map(SubProcess::getEndDate)
					.orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
				.progressRate(Optional.of(subProcess).map(SubProcess::getProgressRate).orElseGet(() -> 0))
				.reportedDate(Optional.of(subProcess)
					.map(SubProcess::getReportedDate)
					.orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
				.isRecent(Optional.of(subProcess).map(SubProcess::getIsRecent).orElseGet(() -> YesOrNo.NO))
				.workerUUID(userInfoResponse.getData().getUuid())
				.workerName(userInfoResponse.getData().getNickname())
				.workerProfile(userInfoResponse.getData().getProfile())
				.issuesTotal(this.issueRepository.countIssuesInSubProcess(subProcess.getId()))
				.doneCount((int)subProcess.getJobList()
					.stream()
					.filter(
						job -> job.getConditions() == Conditions.COMPLETED || job.getConditions() == Conditions.SUCCESS)
					.count())
				.build();
		}).collect(Collectors.toList());

		PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.totalPage(subProcessPage.getTotalPages())
			.totalElements(subProcessPage.getTotalElements())
			.build();
		return new ApiResponse<>(
			new SubProcessListResponse(processId, processName, processState, contentUUID, editSubProcessResponseList,
				pageMetadataResponse
			));
	}

	/**
	 * 하위 작업의 컨디션을 필터링
	 *
	 * @param subProcessList
	 * @param filter
	 * @param pageable
	 * @return
	 */
	private Page<SubProcess> filterConditionsSubProcessPage(
		Page<SubProcess> subProcessList, List<Conditions> filter, Pageable pageable
	) {
		List<SubProcess> subProcesses = new ArrayList<>();
		for (SubProcess subProcess : subProcessList) {
			// 상태가 일치하는 공정만 필터링
			if (filter.contains(subProcess.getConditions())) {
				subProcesses.add(subProcess);
			}
		}

		return new PageImpl<>(subProcesses, pageable, subProcesses.size());
	}

	/**
	 * 워크스페이스 전체의 하위 작업 목록 조회
	 *
	 * @param workspaceUUID
	 * @param processId
	 * @param search
	 * @param pageable
	 * @return
	 */
	public ApiResponse<SubProcessesResponse> getSubProcesses(
		String workspaceUUID, Long processId, String search, Pageable pageable, List<Conditions> filter
	) {
		// 워크스페이스 전체의 세부공정목록조회
		// 검색어로 사용자 목록 조회
		//List<UserInfoResponse> userInfos = getUserInfoSearch(search);
		List<String> userUUIDList = new ArrayList<>();

		if (Objects.nonNull(search) && Objects.nonNull(workspaceUUID)) {
			List<UserInfoResponse> userInfos = getUserInfo(search, workspaceUUID);
			userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
		}

		Page<SubProcess> subProcessPage = this.subProcessRepository.getFilteredSubProcessPage(
			workspaceUUID, processId, search, userUUIDList, pageable, filter);
/*
		if (filter != null && !filter.isEmpty() && !filter.contains(Conditions.ALL)) {
			List<SubProcessReportedResponse> editSubProcessResponseList = subProcessPage.stream()
				.filter(subProcess -> filter.contains(subProcess.getConditions()))
				.map(subProcess -> {
					ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(
						subProcess.getWorkerUUID());
					return SubProcessReportedResponse.builder()
						.taskId(subProcess.getProcess().getId())
						.taskName(subProcess.getProcess().getName())
						.subTaskId(subProcess.getId())
						.subTaskName(subProcess.getName())
						.conditions(subProcess.getConditions())
						.reportedDate(Optional.of(subProcess)
							.map(SubProcess::getReportedDate)
							.orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
						.workerUUID(userInfoResponse.getData().getUuid())
						.workerName(userInfoResponse.getData().getNickname())
						.workerProfile(userInfoResponse.getData().getProfile())
						.build();
				}).collect(Collectors.toList());

			int totalElements = (int)subProcessPage.stream()
				.filter(subProcess -> subProcess.getConditions().equals(filter))
				.count();
			int totalPage = totalElements / pageable.getPageSize();
			if (editSubProcessResponseList.size() % pageable.getPageSize() > 0) {
				totalPage = totalPage + 1;
			}

			PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
				.currentPage(pageable.getPageNumber())
				.currentSize(pageable.getPageSize())
				.totalPage(totalPage)
				.totalElements(totalElements)
				.build();
			return new ApiResponse<>(new SubProcessesResponse(editSubProcessResponseList, pageMetadataResponse));
		}*/

		List<SubProcessReportedResponse> editSubProcessResponseList = subProcessPage.stream().map(subProcess -> {
			ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(
				subProcess.getWorkerUUID());
			return SubProcessReportedResponse.builder()
				.taskId(subProcess.getProcess().getId())
				.taskName(subProcess.getProcess().getName())
				.subTaskId(subProcess.getId())
				.subTaskName(subProcess.getName())
				.conditions(subProcess.getConditions())
				.reportedDate(Optional.of(subProcess)
					.map(SubProcess::getReportedDate)
					.orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
				.workerUUID(userInfoResponse.getData().getUuid())
				.workerName(userInfoResponse.getData().getNickname())
				.workerProfile(userInfoResponse.getData().getProfile())
				.build();
		}).collect(Collectors.toList());
		PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.totalPage(subProcessPage.getTotalPages())
			.totalElements(subProcessPage.getTotalElements())
			.build();
		return new ApiResponse<>(new SubProcessesResponse(editSubProcessResponseList, pageMetadataResponse));
	}

	/**
	 * 하위 작업 상세조회
	 *
	 * @param subProcessId
	 * @return
	 */
	public ApiResponse<SubProcessInfoResponse> getSubProcess(Long subProcessId) {
		// 하위 작업 단건 조회
		SubProcess subProcess = this.subProcessRepository.findById(subProcessId)
			.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER));

		ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(
			subProcess.getWorkerUUID());

		return new ApiResponse<>(SubProcessInfoResponse.builder()
			.taskId(subProcess.getProcess().getId())
			.taskName(subProcess.getProcess().getName())
			.subTaskId(subProcess.getId())
			.subTaskName(subProcess.getName())
			.priority(subProcess.getPriority())
			.stepTotal(subProcess.getJobList().size())
			.conditions(subProcess.getConditions())
			.startDate(subProcess.getStartDate())
			.endDate(subProcess.getEndDate())
			.progressRate(subProcess.getProgressRate())
			.reportedDate(subProcess.getReportedDate())
			.isRecent(subProcess.getIsRecent())
			.workerUUID(subProcess.getWorkerUUID())
			.workerName(userInfoResponse.getData().getNickname())
			.workerProfile(userInfoResponse.getData().getProfile())
			.issuesTotal(this.issueRepository.countIssuesInSubProcess(subProcess.getId()))
			.doneCount((int)subProcess.getJobList()
				.stream()
				.filter(job -> job.getConditions() == Conditions.COMPLETED || job.getConditions() == Conditions.SUCCESS)
				.count())
			.build());
	}

	/**
	 * 내 작업 조회
	 *
	 * @param workspaceUUID
	 * @param workerUUID
	 * @param processId
	 * @param search
	 * @param pageable
	 * @return
	 * @Description 내 작업을 조회. 종료된 작업은 제외한다.
	 */
	@Transactional
	public ApiResponse<MyWorkListResponse> getMyWorks(
		String workspaceUUID, String workerUUID, Long processId, String search, Pageable pageable, String targetType
	) {
		Page<SubProcess> subProcessPage = this.subProcessRepository.getMyWorksInProcess(
			workspaceUUID, workerUUID, processId, search, pageable, targetType);
		//        Page<SubProcess> subProcessPage = this.subProcessRepository.findByWorkerUUID(workerUUID, pageable);

		List<MyWorksResponse> myWorksResponseList = subProcessPage.stream().map(subProcess -> {
			// 신규작업확인 처리
			if (workerUUID.equals(subProcess.getWorkerUUID())) {
				if (subProcess.getIsRecent() == YesOrNo.YES) {
					subProcess.setIsRecent(YesOrNo.NO);
					this.subProcessRepository.save(subProcess);
				}
			}

			Process process = Optional.of(subProcess)
				.map(SubProcess::getProcess)
				.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SUBPROCESS));
			ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(
				subProcess.getWorkerUUID());
			// 공정이 정상적으로 생성되지 않고, 임의로 데이터를 넣으면 process_id가 null일 수 있음.
			return MyWorksResponse.builder()
				.taskId(process.getId())
				.taskName(process.getName())
				.contentUUID(process.getContentUUID())
				.downloadPath(this.contentRestService.getContentInfo(process.getContentUUID()).getData().getPath())
				.subTaskId(subProcess.getId())
				.priority(subProcess.getPriority())
				.subTaskName(subProcess.getName())
				.stepTotal(Optional.of(subProcess).map(SubProcess::getJobList).map(List<Job>::size).orElseGet(() -> 0))
				.conditions(Optional.of(subProcess).map(SubProcess::getConditions).orElseGet(() -> Conditions.WAIT))
				.startDate(subProcess.getStartDate())
				.endDate(subProcess.getEndDate())
				.progressRate(Optional.of(subProcess).map(SubProcess::getProgressRate).orElseGet(() -> 0))
				.isRecent(Optional.of(subProcess).map(SubProcess::getIsRecent).orElseGet(() -> YesOrNo.NO))
				.workerUUID(subProcess.getWorkerUUID())
				.workerName(userInfoResponse.getData().getNickname())
				.workerProfile(userInfoResponse.getData().getProfile())
				.reportedDate(Optional.of(subProcess)
					.map(SubProcess::getReportedDate)
					.orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
				.doneCount((int)subProcess.getJobList()
					.stream()
					.filter(
						job -> job.getConditions() == Conditions.COMPLETED || job.getConditions() == Conditions.SUCCESS)
					.count())
				.state(process.getState())
				.issueTotal(this.issueRepository.countIssuesInSubProcess(subProcess.getId()))
				.build();
		}).collect(Collectors.toList());

		PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.totalPage(subProcessPage.getTotalPages())
			.totalElements(subProcessPage.getTotalElements())
			.build();
		return new ApiResponse<>(new MyWorkListResponse(myWorksResponseList, pageMetadataResponse));
	}

	/**
	 * 타겟 데이터로 작업 조회
	 *
	 * @param workspaceUUID
	 * @param targetData
	 * @param pageable
	 * @return
	 */
	public ApiResponse<SubProcessesOfTargetResponse> getSubProcessesOfTarget(
		String workspaceUUID, String targetData, Pageable pageable
	) {

		// URLDecode된 데이터를 다시 encoding
		String encodedData = checkParameterEncoded(targetData);

		Process process = this.processRepository.getProcessUnClosed(workspaceUUID, encodedData)
			.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS_OF_TARGET));
		//        Page<SubProcess> subProcessPage = this.subProcessRepository.selectSubProcesses(null, process.getId(), null, null, pageable);
		Page<SubProcess> subProcessPage = this.subProcessRepository.getSubProcessPage(
			null, process.getId(), null, null, pageable);
		SubProcessesOfTargetResponse subProcessesOfTargetResponse = SubProcessesOfTargetResponse.builder()
			.taskId(process.getId())
			.taskName(process.getName())
			.contentUUID(process.getContentUUID())
			.downloadPath(this.contentRestService.getContentInfo(process.getContentUUID()).getData().getPath())
			.subTasks(subProcessPage.getContent().stream().map(subProcess -> {
				ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(
					subProcess.getWorkerUUID());
				return SubProcessOfTargetResponse.builder()
					.subTaskId(subProcess.getId())
					.subTaskName(subProcess.getName())
					.priority(subProcess.getPriority())
					.stepTotal(subProcess.getJobList().size())
					.startDate(subProcess.getStartDate())
					.endDate(subProcess.getEndDate())
					.reportedDate(subProcess.getReportedDate())
					.conditions(subProcess.getConditions())
					.progressRate(subProcess.getProgressRate())
					.isRecent(subProcess.getIsRecent())
					.workerUUID(subProcess.getWorkerUUID())
					.workerName(userInfoResponse.getData().getNickname())
					.workerProfile(userInfoResponse.getData().getProfile())
					.build();
			}).collect(Collectors.toList()))
			.pageMeta(PageMetadataResponse.builder()
				.currentPage(pageable.getPageNumber())
				.currentSize(pageable.getPageSize())
				.totalPage(subProcessPage.getTotalPages())
				.totalElements(subProcessPage.getTotalElements())
				.build())
			.build();
		return new ApiResponse<>(subProcessesOfTargetResponse);
	}

	/**
	 * 하위 작업 수정
	 *
	 * @param subProcessId
	 * @param subProcessRequest
	 * @return
	 */
	public ResponseMessage updateSubProcess(Long subProcessId, EditSubProcessRequest subProcessRequest) {
		// 하위 작업 단건 조회
		SubProcess subProcess = this.subProcessRepository.findById(subProcessId)
			.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SUBPROCESS));
		// 데이터 저장
		// 작업자 신규할당여부 확인 후 isRecent flag 설정
		if (subProcess.getWorkerUUID() == null || !subProcess.getWorkerUUID()
			.equals(subProcessRequest.getWorkerUUID())) {
			subProcess.setIsRecent(YesOrNo.YES);
		}
		subProcess.setWorkerUUID(
			Optional.of(subProcessRequest).map(EditSubProcessRequest::getWorkerUUID).orElseGet(() -> ""));
		// 공정의 날짜 범위 체크는 클라이언트에서 함.
		subProcess.setStartDate(Optional.of(subProcessRequest)
			.map(EditSubProcessRequest::getStartDate)
			.orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")));
		subProcess.setEndDate(Optional.of(subProcessRequest)
			.map(EditSubProcessRequest::getEndDate)
			.orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")));
		this.subProcessRepository.save(subProcess);
		return new ResponseMessage().addParam("result", true);
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
	 * get방식에서 URLEncode된 값의 URLEncoding이 풀려서 오는 케이스를 체크.
	 *
	 * @param targetData
	 * @return
	 */
	protected String checkParameterEncoded(String targetData) {
		String encodedData = null;

		// 컨텐츠 -> 작업으로 복제하여 작업에서 생성된 타겟데이터
		if (targetData.contains("-")) {
			encodedData = targetData;
		}
		// 컨텐츠 -> 작업 전환시에는 타겟데이터가 인코딩 된 상태
		else {
			// 컨텐츠의 타겟데이터는 이미 원본 값이 URLEncoding된 값인데,
			// 실제 서버에서는 servlet container에서 decode하여 URLDecoding된 데이터가 들어오게 된다.
			log.info(">>>>>>>>>>>>>>>>>>> targetData : {}", targetData);

			// 이 와중에 query 파라미터로 받을 경우 '+'가 '공백'으로 리턴된다.
			// PathVariable로 받지 않는 이유는 decoding된 값에 '/'가 들어가는 경우가 있기 때문.
			if (targetData.contains(" ")) {
				// 임시방편으로 공백은 '+'로 치환한다. 더 좋은 방법이 있다면 수정하면 좋을 듯.
				targetData = targetData.replace(" ", "+");
			}
			log.info(">>>>>>>>>>>>>>>>>>> targetData : {}", targetData);

			try {
				// Database에 저장된 targetData는 URLEncoding된 값이므로 인코딩 해줌.
				encodedData = URLEncoder.encode(targetData, StandardCharsets.UTF_8.name());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return encodedData;
	}
}
