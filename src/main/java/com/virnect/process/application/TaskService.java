package com.virnect.process.application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.process.application.content.ContentRestService;
import com.virnect.process.application.user.UserRestService;
import com.virnect.process.application.workspace.WorkspaceRestService;
import com.virnect.process.dao.IssueRepository;
import com.virnect.process.dao.ItemRepository;
import com.virnect.process.dao.JobRepository;
import com.virnect.process.dao.ReportRepository;
import com.virnect.process.dao.SubProcessRepository;
import com.virnect.process.dao.dailytotal.DailyTotalRepository;
import com.virnect.process.dao.dailytotalworkspace.DailyTotalWorkspaceRepository;
import com.virnect.process.dao.process.ProcessRepository;
import com.virnect.process.dao.target.TargetRepository;
import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.DailyTotalWorkspace;
import com.virnect.process.domain.Issue;
import com.virnect.process.domain.Item;
import com.virnect.process.domain.Job;
import com.virnect.process.domain.Process;
import com.virnect.process.domain.Report;
import com.virnect.process.domain.Result;
import com.virnect.process.domain.State;
import com.virnect.process.domain.SubProcess;
import com.virnect.process.domain.Target;
import com.virnect.process.domain.TargetType;
import com.virnect.process.domain.YesOrNo;
import com.virnect.process.dto.request.CheckProcessOwnerRequest;
import com.virnect.process.dto.request.EditProcessRequest;
import com.virnect.process.dto.request.ProcessDuplicateRequest;
import com.virnect.process.dto.request.ProcessRegisterRequest;
import com.virnect.process.dto.request.WorkResultSyncRequest;
import com.virnect.process.dto.response.ProcessContentAndTargetResponse;
import com.virnect.process.dto.response.ProcessInfoResponse;
import com.virnect.process.dto.response.ProcessListResponse;
import com.virnect.process.dto.response.ProcessMetadataResponse;
import com.virnect.process.dto.response.ProcessRegisterResponse;
import com.virnect.process.dto.response.ProcessSimpleResponse;
import com.virnect.process.dto.response.ProcessTargetResponse;
import com.virnect.process.dto.response.SubProcessAssignedResponse;
import com.virnect.process.dto.response.TaskSecessionResponse;
import com.virnect.process.dto.response.WorkResultSyncResponse;
import com.virnect.process.dto.response.WorkSyncResponse;
import com.virnect.process.dto.response.WorkspaceUserInfoResponse;
import com.virnect.process.dto.response.WorkspaceUserListResponse;
import com.virnect.process.dto.rest.request.content.ContentDeleteRequest;
import com.virnect.process.dto.rest.response.content.ContentCountResponse;
import com.virnect.process.dto.rest.response.content.ContentDeleteListResponse;
import com.virnect.process.dto.rest.response.content.ContentInfoResponse;
import com.virnect.process.dto.rest.response.content.ContentRestDto;
import com.virnect.process.dto.rest.response.content.ContentTargetResponse;
import com.virnect.process.dto.rest.response.content.ContentUploadResponse;
import com.virnect.process.dto.rest.response.user.UserInfoListResponse;
import com.virnect.process.dto.rest.response.user.UserInfoResponse;
import com.virnect.process.dto.rest.response.workspace.AllMemberInfoResponse;
import com.virnect.process.dto.rest.response.workspace.MemberInfoDTO;
import com.virnect.process.dto.rest.response.workspace.MemberListResponse;
import com.virnect.process.dto.rest.response.workspace.WorkspaceSettingInfoListResponse;
import com.virnect.process.dto.rest.response.workspace.WorkspaceSettingInfoResponse;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.common.PageMetadataResponse;
import com.virnect.process.global.common.ResponseMessage;
import com.virnect.process.global.error.ErrorCode;
import com.virnect.process.global.util.AES256EncryptUtils;
import com.virnect.process.global.util.QRcodeGenerator;
import com.virnect.process.infra.file.FileUploadService;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-23
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Process Service Class
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
	private static final Conditions INIT_CONDITIONS = Conditions.WAIT;
	private static final Integer INIT_PROGRESS_RATE = 0;
	private static final State INIT_STATE = State.CREATED;
	private static final YesOrNo INIT_IS_RECENT = YesOrNo.NO;
	private static final YesOrNo INIT_IS_REPORTED = YesOrNo.NO;
	private static final Result INIT_RESULT = Result.NOK;

	private final TargetRepository targetRepository;
	private final ProcessRepository processRepository;
	private final SubProcessRepository subProcessRepository;
	private final IssueRepository issueRepository;
	private final ReportRepository reportRepository;
	private final ItemRepository itemRepository;
	private final JobRepository jobRepository;
	private final DailyTotalWorkspaceRepository dailyTotalWorkspaceRepository;
	private final DailyTotalRepository dailyTotalRepository;

	private final ContentRestService contentRestService;
	private final UserRestService userRestService;
	private final WorkspaceRestService workspaceRestService;

	private final ModelMapper modelMapper;
	private final FileUploadService fileUploadService;
	private final SubTaskService subTaskService;

	// TODO : 작업저장실패시 컨텐츠 서버도 함께 롤백하는 프로세스 필요. 현재는 만약 예외가 발생할 경우 컨텐츠 서버는 파일 및 컨텐츠가 복제된 채로 그대로 남으므로 저장소가 낭비됨..
	@Transactional
	public ApiResponse<ProcessRegisterResponse> createTheProcess(ProcessRegisterRequest registerNewProcess) {
		/**
		 * 1.     컨텐츠 메타데이터 가져오기
		 * 1-1.   에러처리
		 * 2.     작업 정보 저장
		 *
		 * 3.     복제 (Duplicate) / 전환 (Transform) 분기
		 * 3-1.   복제 (Duplicate)
		 * 3-1-1. 컨텐츠 파일 복제 요청
		 * 3-1-2. 복제된 컨텐츠 식별자 등록
		 * 3-1-3. 컨텐츠의 전환상태 변경
		 * 3-1-4. 작업 저장
		 * 3-1-5. 타겟 등록
		 * 3-1-6. 복제된 컨텐츠로 세부 작업 정보 리스트 생성
		 * 3-1-7. 작업에 하위작업 추가
		 *
		 * 3-2.   변환 (Transform)
		 * 3-2-1. 변환할 컨텐츠 정보 호출
		 * 3-2-1-1. 이미 컨텐츠에서 작업으로 변환된 경우 에러처리
		 * 3-2-1-2. 컨텐츠의 타겟이 없을 경우 에러처리
		 * 3-2-2. 컨텐츠의 타겟 값 호출
		 * 3-2-3. 기존 컨텐츠 식별자 등록
		 * 3-2-4. 컨텐츠의 전환상태 변경
		 * 3-2-5. 작업 저장
		 * 3-2-6. 컨텐츠의 타겟 정보를 작업의 타겟으로 등록
		 * 3-2-7. 기존 컨텐츠로 하위 작업 정보 리스트 생성
		 * 3-2-8. 작업에 하위작업 추가
		 *
		 */
		// 공정 생성 요청 처리
		log.info("CREATE THE PROCESS requestBody ---> {}", registerNewProcess.toString());

		// 1. 컨텐츠 메타데이터 가져오기
		ApiResponse<ContentRestDto> contentApiResponse = this.contentRestService.getContentMetadata(
			registerNewProcess.getContentUUID());

		// 1-1. 에러가 난 경우
		if (contentApiResponse.getCode() != 200) {
			throw new ProcessServiceException(ErrorCode.ERR_PROCESS_REGISTER);
		}

		if (contentApiResponse.getData() == null) {
			throw new ProcessServiceException(ErrorCode.ERR_PROCESS_REGISTER);
		}

		// 워크스페이스 설정 검증 - TASK_REGISTER_ROLE_SETTING
		//todo 등록하는 유저의 식별자를 필수 파라미터로 추가해야 함.

		log.info("CONTENT_METADATA: [{}]", contentApiResponse.getData().getContents().toString());
		log.debug("----------content uuid : {}", contentApiResponse.getData().getContents().getUuid());

		// 2. 공정 정보 저장
		Process newProcess = Process.builder()
			.startDate(registerNewProcess.getStartDate())
			.endDate(registerNewProcess.getEndDate())
			.name(registerNewProcess.getName())
			.position(registerNewProcess.getPosition())
			.conditions(INIT_CONDITIONS)
			.progressRate(INIT_PROGRESS_RATE)
			.state(INIT_STATE)
			.subProcessList(new ArrayList<>())
			.workspaceUUID(registerNewProcess.getWorkspaceUUID())
			.contentUUID(contentApiResponse.getData().getContents().getUuid())
			.contentManagerUUID(contentApiResponse.getData().getContents().getManagerUUID())
			.build();

		// 3. 복제 / 전환 분기
		// 3-1. 복제 - 메뉴얼(컨텐츠)도 보고 작업(보고)도 필요한 경우 = 복제
		if ("duplicate".equals(registerNewProcess.getTargetSetting())) {
			// 3-1-1. 컨텐츠 파일 복제 요청
			ApiResponse<ContentUploadResponse> contentDuplicate = this.contentRestService.contentDuplicate(
				registerNewProcess.getContentUUID()
				, registerNewProcess.getWorkspaceUUID()
				, registerNewProcess.getOwnerUUID());
			try {
				log.info(
					"CREATE THE PROCESS - sourceContentUUID : [{}], createContentUUID : [{}]",
					registerNewProcess.getContentUUID(), contentDuplicate.getData().getContentUUID()
				);

				// 3-1-2. 복제된 컨텐츠 식별자 등록
				newProcess.setContentUUID(contentDuplicate.getData().getContentUUID());
				newProcess.setContentManagerUUID(registerNewProcess.getOwnerUUID());

				// 3-1-3. 컨텐츠의 전환상태 변경
				//컨텐츠 전환 상태 변경을 컨텐츠 복제할 때 같이 한다.
				//this.contentRestService.contentConvertHandler(contentDuplicate.getData().getContentUUID(), YesOrNo.YES);

				// 3-1-4. 작업 저장
				this.processRepository.save(newProcess);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("CONTENT UPLOAD ERROR: {}", e.getMessage());
				//rollbackDuplicateContent(contentDuplicate.getData().getContentUUID(), registerNewProcess.getOwnerUUID());
				rollbackDuplicateContent(
					contentDuplicate.getData().getContentUUID(), registerNewProcess.getWorkspaceUUID());
				throw new ProcessServiceException(ErrorCode.ERR_PROCESS_REGISTER);
			}

			// 3-1-5. 타겟 등록
			addTargetToProcess(newProcess, registerNewProcess.getTargetSize(), registerNewProcess.getTargetType());
			//addTargetToProcess(newProcess, registerNewProcess.getTargetSize(), registerNewProcess.getTargetType());

			ApiResponse<ContentRestDto> duplicatedContent = this.contentRestService.getContentMetadata(
				contentDuplicate.getData().getContentUUID());

			// 3-1-6. 복제된 컨텐츠로 세부 작업 정보 리스트 생성
			log.info("{}", duplicatedContent.getData().getContents().toString());
			ContentRestDto.Content duplicatedMetadata = duplicatedContent.getData().getContents();
			Map<String, ContentRestDto.SceneGroup> sceneGroupMap = new HashMap<>();

			log.debug("Duplicated ConetntMetadata {}", duplicatedMetadata);

			duplicatedMetadata.getSceneGroups()
				.forEach(sceneGroup -> sceneGroupMap.put(sceneGroup.getId(), sceneGroup));

			// 3-1-7. 작업에 하위작업 추가
			addSubProcessOnProcess(registerNewProcess, sceneGroupMap, newProcess);
		}
		// 3-2. 변환. 메뉴얼(컨텐츠)은 필요없고 작업(보고)만 필요한 경우.
		else {
			log.info("CREATE THE PROCESS  - transform sourceContentUUID : [{}]", registerNewProcess.getContentUUID());
			// 3-2-1. 변환할 컨텐츠 정보 호출
			ApiResponse<ContentInfoResponse> contentTransform = this.contentRestService.getContentInfo(
				registerNewProcess.getContentUUID());

			log.debug("line 149 : {}", contentTransform.getData());
			log.debug("line 150 : {}", contentTransform.getData().getTargets());

			ContentInfoResponse contentInfo = contentTransform.getData();

			// 3-2-1-1. 이미 컨텐츠에서 작업으로 변환 된 경우
			if (YesOrNo.YES.equals(contentInfo.getConverted())) {
				throw new ProcessServiceException(ErrorCode.ERR_ALREADY_TRANSFORMED);
			}

			// 3-2-1-2. 컨텐츠의 타겟이 없을 경우
			if (contentInfo.getTargets() == null || contentInfo.getTargets().isEmpty()) {
				throw new ProcessServiceException(ErrorCode.ERR_NO_CONTENT_TARGET);
			}

			// 3-2-2. 컨텐츠의 타겟값을 가져옴
			ContentTargetResponse contentTarget = contentInfo.getTargets().get(0);

			// 3-2-3. 기존 컨텐츠 식별자 등록
			newProcess.setContentUUID(registerNewProcess.getContentUUID());
			newProcess.setContentManagerUUID(registerNewProcess.getOwnerUUID());

			// 3-2-4. 컨텐츠의 전환상태 변경
			this.contentRestService.contentConvertHandler(contentTransform.getData().getContentUUID(), YesOrNo.YES);

			// 3-2-5. 작업 저장
			this.processRepository.save(newProcess);

			// 3-2-6. 컨텐츠의 타겟 정보를 작업의 타겟 정보에 저장
			getTargetFromContent(newProcess, contentTarget);

			// 3-2-7. 기존 컨텐츠로 세부 공정 정보 리스트 생성
			log.info("{}", contentApiResponse.getData().getContents().toString());
			ContentRestDto.Content metadata = contentApiResponse.getData().getContents();
			Map<String, ContentRestDto.SceneGroup> sceneGroupMap = new HashMap<>();

			log.debug("ConetntMetadata {}", metadata);

			metadata.getSceneGroups().forEach(sceneGroup -> sceneGroupMap.put(sceneGroup.getId(), sceneGroup));

			// 3-2-8. 작업에 하위작업 추가
			addSubProcessOnProcess(registerNewProcess, sceneGroupMap, newProcess);
		}

		List<ProcessTargetResponse> targetResponseList = new ArrayList<>();
		newProcess.getTargetList().forEach(target -> {
			ProcessTargetResponse processTargetResponse = this.modelMapper.map(target, ProcessTargetResponse.class);
			targetResponseList.add(processTargetResponse);
		});

		ProcessRegisterResponse processRegisterResponse = ProcessRegisterResponse.builder()
			.taskId(newProcess.getId())
			.name(newProcess.getName())
			.startDate(newProcess.getStartDate())
			.totalSubTask(newProcess.getSubProcessList().size())
			.endDate(newProcess.getEndDate())
			.conditions(newProcess.getConditions())
			.progressRate(newProcess.getProgressRate())
			.state(newProcess.getState())
			.target(targetResponseList)
			.workspaceUUID(registerNewProcess.getWorkspaceUUID())
			.build();

		return new ApiResponse<>(processRegisterResponse);
	}

	/**
	 * 복제된 컨텐츠 삭제
	 *
	 * @param contentUUID
	 * @param userUUID
	 */
	private void rollbackDuplicateContent(String contentUUID, String workspaceUUID) {
		this.contentRestService.contentConvertHandler(contentUUID, YesOrNo.NO);
		String[] contentUUIDs = {contentUUID};

		ContentDeleteRequest contentDeleteRequest = new ContentDeleteRequest();
		contentDeleteRequest.setContentUUIDs(contentUUIDs);
		contentDeleteRequest.setWorkspaceUUID(workspaceUUID);
		//contentDeleteRequest.setWorkerUUID(userUUID);

		this.contentRestService.contentDeleteRequestHandler(contentDeleteRequest);
	}

	/**
	 * 컨텐츠를 작업으로 복제(Duplicate)할 때 - 작업의 새로운 타겟을 만든다. (매뉴얼 + 작업 보고)
	 *
	 * @param newProcess
	 * @param targetType
	 */
	private void addTargetToProcess(Process newProcess, float targetSize, final TargetType targetType) {
		// 타겟데이터
		try {
			String targetData = UUID.randomUUID().toString();
			// cd07565a-dfe8-4441-8924-b047d525ea79

			log.info(
				">>>>>>>>>>>>>>>>>>> targetData : {}, targetSize: {}, targetType: {}", targetData, targetSize,
				targetType.toString()
			);

			/*
			복제 시나리오 -> 작업서버에서 신규 타겟 발급 시에 타겟 구분 추가
			 */
			String imgPath = null;
			if (targetType.equals(TargetType.QR)) {
				// 컨텐츠 서버에 담겨진 QR코드 경로는 원본 값을 QR코드로 만든 것이다.
				imgPath = getImgPath(targetData); //= this.fileUploadService.base64ImageUpload(targetData);
			}
			if (targetType.equals(TargetType.VTarget)) {
				//imgPath = fileUploadUrl + fileUploadPath + defaultVTarget;defaultVTarget
				imgPath = fileUploadService.getFilePath("virnect_target.png");
			}
			if (targetType.equals(TargetType.VR)) {
				imgPath = null;
			}

			// 컨텐츠 서버에 담겨진 targetData (= Make에서 제공하는 targetData) 는 AES256인코딩 후 URL인코딩을 한 값이다.
			// 컨텐츠 서버와 targetData 인코딩을 맞춰주기 위해서 해당 인코딩을 수행한다.
			String aes256Encoded = AES256EncryptUtils.encryptByBytes("virnect", targetData);

			log.info(">>>>>>>>>>>>>>>>>>>>> aes256Encoded : {}", aes256Encoded);

			String urlEncoded = URLEncoder.encode(aes256Encoded, "UTF-8");

			log.info(">>>>>>>>>>>>>>>>>>>>> urlEncoded : {}", urlEncoded);

			Target target = Target.builder()
				.type(targetType)
				.process(newProcess)
				.data(urlEncoded)
				.imgPath(imgPath)
				.size(targetSize)
				.build();

			this.targetRepository.save(target);

			log.debug("TARGET : {}", target.toString());
			log.debug("newProcess : {}", newProcess.toString());
			newProcess.addTarget(target);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("ERROR : ADD TARGET ON PROCESS getMessage: {}, getCause: {}", e.getMessage(), e.getCause());
			//rollbackDuplicateContent(newProcess.getContentUUID(), newProcess.getContentManagerUUID());
			rollbackDuplicateContent(newProcess.getContentUUID(), newProcess.getWorkspaceUUID());
			throw new ProcessServiceException(ErrorCode.ERR_TARGET_REGISTER);
		}
	}

	/**
	 * 컨텐츠를 작업으로 변환(Transform)할 때 - 기존 컨텐츠의 타겟을 작업 타겟으로 설정.( 메뉴얼 X, Only 작업 보고)
	 *
	 * @param newProcess
	 * @param contentTargetResponse
	 */
	private void getTargetFromContent(Process newProcess, ContentTargetResponse contentTargetResponse) {
		try {
			String targetData = contentTargetResponse.getData();
			TargetType targetType = contentTargetResponse.getType();
			String imgPath = contentTargetResponse.getImgPath(); //this.fileUploadService.base64ImageUpload(targetData);

			//타겟 사이즈가 없는 컨텐츠들의 기본값은 10.
			float targetSize = 10f;
			if (contentTargetResponse.getSize() != null) {
				targetSize = contentTargetResponse.getSize();
			}

			Target target = Target.builder()
				.type(targetType)
				.process(newProcess)
				.data(targetData)
				.imgPath(imgPath)
				.size(targetSize)
				.build();

			this.targetRepository.save(target);

			newProcess.addTarget(target);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("ERROR : GET TARGET FROM CONTENT getMessage: {}, getCause: {}", e.getMessage(), e.getCause());
			throw new ProcessServiceException(ErrorCode.ERR_TARGET_REGISTER);
		}
	}

	/**
	 * 작업을 추가 작업으로 변환(Transform)할 때 - 기존 작업의 타겟을 작업 타겟으로 설정.
	 *
	 * @param newProcess
	 * @param contentTargetResponse
	 */
	private void getTargetFromTask(Process newProcess, Target taskTarget) {
		try {
			if (Objects.isNull(taskTarget)) {
				throw new ProcessServiceException(ErrorCode.ERR_TARGET_REGISTER);
			}

			Target target = taskTarget;

			this.targetRepository.save(target);

			newProcess.addTarget(target);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("ERROR : GET TARGET FROM CONTENT getMessage: {}, getCause: {}", e.getMessage(), e.getCause());
			throw new ProcessServiceException(ErrorCode.ERR_TARGET_REGISTER);
		}
	}

	/**
	 * 신규 공정에 세부 공정 내역 추가 처리
	 *
	 * @param registerNewProcess - 신규 공정 생성 요청 데이터
	 * @param sceneGroupMap      - 메타데이터로부터 파싱된 세부공정(씬그룹)관련 정보
	 * @param newProcess         - 신규 공정 관련 정보
	 */
	private void addSubProcessOnProcess(
		ProcessRegisterRequest registerNewProcess, Map<String, ContentRestDto.SceneGroup> sceneGroupMap,
		Process newProcess
	) {
		try {
			registerNewProcess.getSubTaskList().forEach(
				newSubProcess -> {
					// 작업자가 지정되지 않았을 때 에러.
					if (Objects.isNull(newSubProcess.getWorkerUUID())) {
						throw new ProcessServiceException(ErrorCode.ERR_SUB_PROCESS_REGISTER_NO_WORKER);
					}

					ContentRestDto.SceneGroup sceneGroup = sceneGroupMap.get(newSubProcess.getId());
					SubProcess subProcess = SubProcess.builder()
						.process(newProcess)
						.startDate(newSubProcess.getStartDate())
						.endDate(newSubProcess.getEndDate())
						.name(newSubProcess.getName())
						.priority(newSubProcess.getPriority())
						.conditions(INIT_CONDITIONS)
						.progressRate(INIT_PROGRESS_RATE)
						.isRecent(INIT_IS_RECENT)
						.reportedDate(null)
						.workerUUID(newSubProcess.getWorkerUUID())
						.jobList(new ArrayList<>())
						.build();

					this.subProcessRepository.save(subProcess);
					newProcess.addSubProcess(subProcess);

					// SceneGroup(subTask)에 Job(Step)이 있을 경우
					if (sceneGroup.getJobTotal() > 0) {
						// SubProces 에 job 정보 추가
						addJobToSubProcess(sceneGroup, subProcess, newProcess);
					}
				});
		} catch (Exception e) {
			e.printStackTrace();
			log.error("ERROR : ADD SUB-PROCESS ON PROCESS: {}", e.getMessage());
			//rollbackDuplicateContent(newProcess.getContentUUID(), newProcess.getContentManagerUUID());
			rollbackDuplicateContent(newProcess.getContentUUID(), newProcess.getWorkspaceUUID());
			throw new ProcessServiceException(ErrorCode.ERR_SUB_PROCESS_REGISTER);
		}
	}

	/**
	 * 세부 공정에 작업 내역 추가 처리
	 *
	 * @param sceneGroup - 메타데이터로부터 파싱된 세부공정(씬구룹) 정보
	 * @param subProcess - 신규 세부 공정 관련 정보
	 */
	private void addJobToSubProcess(ContentRestDto.SceneGroup sceneGroup, SubProcess subProcess, Process newProcess) {
		try {
			log.debug(">>>>>>>> sceneGroup.getScenes() -> {}", sceneGroup.getScenes());

			sceneGroup.getScenes().forEach(
				scene -> {
					Job job = Job.builder()
						.name(scene.getName())
						.priority(scene.getPriority())
						.progressRate(INIT_PROGRESS_RATE)
						.conditions(INIT_CONDITIONS)
						.isReported(INIT_IS_REPORTED)
						.result(INIT_RESULT)
						.issueList(new ArrayList<>())
						.reportList(new ArrayList<>())
						.build();

					job = this.jobRepository.save(job);
					subProcess.addJob(job);

					// Job 에 Report 아이템 추가하기
					addJobToReport(scene, job, newProcess);

					job.setConditions(INIT_CONDITIONS);

					this.jobRepository.save(job);
				});
		} catch (Exception e) {
			e.printStackTrace();
			log.error("ERROR : ADD JOB ON SUB-PROCESS: {}", e.getMessage());
			//rollbackDuplicateContent(newProcess.getContentUUID(), newProcess.getContentManagerUUID());
			rollbackDuplicateContent(newProcess.getContentUUID(), newProcess.getWorkspaceUUID());
			throw new ProcessServiceException(ErrorCode.ERR_JOB_REGISTER);
		}
	}

	/**
	 * 작업에 하위 작업 추가 처리
	 *
	 * @param scene - 메타데이터로부터 파싱된 작업(씬) 정보
	 * @param job   - 신규 작업 관련 정보
	 */
	private void addJobToReport(ContentRestDto.Scene scene, Job job, Process newProcess) {
		try {
			// 널체크 추가..
			if (scene.getReportObjects() != null) {
				scene.getReportObjects().forEach(reportObject -> {
					log.info(">>>>>>>>>>>>>>> reportObject.getItems() {}", reportObject.getItems());
					// Item이 있다면 (널 체크 추가)
					if (reportObject.getItems() != null) {
						Report report = new Report();
						reportObject.getItems().forEach(reportObjectItem -> {
							Item item = Item.builder()
								.type(reportObjectItem.getType())
								.title(reportObjectItem.getTitle())
								.answer("")
								.path("")
								.priority(reportObjectItem.getPriority())
								.result(INIT_RESULT)
								.report(report)
								.build();
							//report.addItem(item);
							this.itemRepository.save(item);
						});

						this.reportRepository.save(report);
						job.addReport(report);
						this.itemRepository.saveAll(report.getItemList());
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("ERROR : ADD REPORT ON JOB: {}", e.getMessage());
			//rollbackDuplicateContent(newProcess.getContentUUID(), newProcess.getContentManagerUUID());
			rollbackDuplicateContent(newProcess.getContentUUID(), newProcess.getWorkspaceUUID());
			throw new ProcessServiceException(ErrorCode.ERR_REPORT_REGISTER);
		}
	}

	@Transactional
	public ApiResponse<ProcessRegisterResponse> duplicateTheProcess(ProcessDuplicateRequest duplicateRequest) {
		// 공정 생성 요청 처리
		log.info("CREATE THE PROCESS requestBody ---> {}", duplicateRequest.toString());

		// 1. 컨텐츠 메타데이터 가져오기
		ApiResponse<ContentRestDto> contentApiResponse = this.contentRestService.getContentMetadata(
			duplicateRequest.getContentUUID());

		// 1-1. 에러가 난 경우
		if (contentApiResponse.getCode() != 200) {
			throw new ProcessServiceException(ErrorCode.ERR_PROCESS_REGISTER);
		}

		if (contentApiResponse.getData() == null) {
			throw new ProcessServiceException(ErrorCode.ERR_PROCESS_REGISTER);
		}

		log.debug("----------content uuid : {}", contentApiResponse.getData().getContents().getUuid());

		// 4. 공정 정보 저장
		Process newProcess = Process.builder()
			.startDate(duplicateRequest.getStartDate())
			.endDate(duplicateRequest.getEndDate())
			.name(duplicateRequest.getName())
			.position(duplicateRequest.getPosition())
			.conditions(INIT_CONDITIONS)
			.progressRate(INIT_PROGRESS_RATE)
			.state(INIT_STATE)
			.subProcessList(new ArrayList<>())
			.workspaceUUID(duplicateRequest.getWorkspaceUUID())
			.contentUUID(contentApiResponse.getData().getContents().getUuid())
			.contentManagerUUID(contentApiResponse.getData().getContents().getManagerUUID())
			.build();

		// 메뉴얼(컨텐츠)도 보고 작업(보고)도 필요한 경우 = 복제
		if ("duplicate".equals(duplicateRequest.getTargetSetting())) {
			// 컨텐츠 파일 복제 요청
			ApiResponse<ContentUploadResponse> contentDuplicate = this.contentRestService.contentDuplicate(
				duplicateRequest.getContentUUID()
				, duplicateRequest.getWorkspaceUUID()
				, duplicateRequest.getOwnerUUID());
			try {
				log.info(
					"DUPLICATE THE PROCESS - sourceContentUUID : [{}], createContentUUID : [{}]",
					duplicateRequest.getContentUUID(), contentDuplicate.getData().getContentUUID()
				);

				// 복제된 컨텐츠 식별자 등록
				newProcess.setContentUUID(contentDuplicate.getData().getContentUUID());
				newProcess.setContentManagerUUID(duplicateRequest.getOwnerUUID());

				// 컨텐츠의 전환상태 변경
				//duplicate api에서 한꺼번에 하는 걸로 수정.
				//this.contentRestService.contentConvertHandler(contentDuplicate.getData().getContentUUID(), YesOrNo.YES);

				// 작업 저장
				this.processRepository.save(newProcess);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("CONTENT UPLOAD ERROR: {}", e.getMessage());
				//rollbackDuplicateContent(contentDuplicate.getData().getContentUUID(), duplicateRequest.getOwnerUUID());
				rollbackDuplicateContent(
					contentDuplicate.getData().getContentUUID(), duplicateRequest.getWorkspaceUUID());
				throw new ProcessServiceException(ErrorCode.ERR_PROCESS_REGISTER);
			}

			/*
			복제된 컨텐츠는 타겟정보가 없다. 따라서 클라이언트에서 받는다.
			*/
			addTargetToProcess(newProcess, duplicateRequest.getTargetSize(), duplicateRequest.getTargetType());

			// addSubProcessOnProcess에 들어갈 객체
			ProcessRegisterRequest registerNewProcess = new ProcessRegisterRequest();

			registerNewProcess.setSubTaskList(duplicateRequest.getSubTaskList());

			ApiResponse<ContentRestDto> duplicatedContent = this.contentRestService.getContentMetadata(
				contentDuplicate.getData().getContentUUID());

			// 5. 복제된 컨텐츠로 세부 공정 정보 리스트 생성
			log.info("{}", duplicatedContent.getData().getContents().toString());
			ContentRestDto.Content duplicatedMetadata = duplicatedContent.getData().getContents();
			Map<String, ContentRestDto.SceneGroup> sceneGroupMap = new HashMap<>();

			log.debug("Duplicated ConetntMetadata {}", duplicatedMetadata);

			duplicatedMetadata.getSceneGroups()
				.forEach(sceneGroup -> sceneGroupMap.put(sceneGroup.getId(), sceneGroup));

			addSubProcessOnProcess(registerNewProcess, sceneGroupMap, newProcess);
		}
		// 메뉴얼(컨텐츠)은 필요없고 작업(보고)만 필요한 경우.
		else {
			log.info("DUPLICATE THE PROCESS  - transform sourceContentUUID : [{}]", duplicateRequest.getContentUUID());
			Process targetProcess = this.processRepository.findById(duplicateRequest.getTaskId())
				.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
			ApiResponse<ContentInfoResponse> contentTransfrom = this.contentRestService.getContentInfo(
				duplicateRequest.getContentUUID());

			// 기존 컨텐츠 식별자 등록
			newProcess.setContentUUID(duplicateRequest.getContentUUID());
			newProcess.setContentManagerUUID(duplicateRequest.getOwnerUUID());

			// 컨텐츠의 전환상태 변경
			this.contentRestService.contentConvertHandler(contentTransfrom.getData().getContentUUID(), YesOrNo.YES);

			// 새로운 작업 저장
			this.processRepository.save(newProcess);

			CheckProcessOwnerRequest checkProcessOwnerRequest = new CheckProcessOwnerRequest();

			checkProcessOwnerRequest.setActorUUID(targetProcess.getContentManagerUUID());

			// 기존의 작업은 CLOSED
			this.setClosedProcess(targetProcess.getId(), checkProcessOwnerRequest);

			Target target = null;

			if (!targetProcess.getTargetList().isEmpty()) {
				target = targetProcess.getTargetList().get(0);
			}

			// 기존 작업의 타겟 정보를 가져옴
			getTargetFromTask(newProcess, target);

			// 5. 세부 공정 정보 리스트 생성
			log.info("{}", contentApiResponse.getData().getContents().toString());
			ContentRestDto.Content metadata = contentApiResponse.getData().getContents();
			Map<String, ContentRestDto.SceneGroup> sceneGroupMap = new HashMap<>();

			log.debug("ConetntMetadata {}", metadata);

			metadata.getSceneGroups().forEach(sceneGroup -> sceneGroupMap.put(sceneGroup.getId(), sceneGroup));

			ProcessRegisterRequest processRegisterRequest = new ProcessRegisterRequest();

			processRegisterRequest.setSubTaskList(duplicateRequest.getSubTaskList());

			addSubProcessOnProcess(processRegisterRequest, sceneGroupMap, newProcess);
		}

		List<ProcessTargetResponse> targetResponseList = new ArrayList<>();
		newProcess.getTargetList().forEach(target -> {
			ProcessTargetResponse processTargetResponse = this.modelMapper.map(target, ProcessTargetResponse.class);
			targetResponseList.add(processTargetResponse);
		});

		ProcessRegisterResponse processRegisterResponse = ProcessRegisterResponse.builder()
			.taskId(newProcess.getId())
			.name(newProcess.getName())
			.startDate(newProcess.getStartDate())
			.totalSubTask(newProcess.getSubProcessList().size())
			.endDate(newProcess.getEndDate())
			.conditions(newProcess.getConditions())
			.progressRate(newProcess.getProgressRate())
			.state(newProcess.getState())
			.target(targetResponseList)
			.workspaceUUID(duplicateRequest.getWorkspaceUUID())
			.build();

		// TODO : 공정전환 완료 후 converted yes로 변경해야 함.

		return new ApiResponse<>(processRegisterResponse);
	}

	public ApiResponse<ProcessContentAndTargetResponse> getRelatedInfoOfProcess(Long processId) {
		// 공정의 타겟 데이터와 contentUUID 가져오기
		Process process = this.processRepository.findById(processId)
			.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
		ProcessContentAndTargetResponse processContentAndTargetResponse = ProcessContentAndTargetResponse.builder()
			.targetList(process.getTargetList()
				.stream()
				.map(target -> this.modelMapper.map(target, ProcessTargetResponse.class))
				.collect(Collectors.toList()))
			.contentUUID(process.getContentUUID())
			.build();

		return new ApiResponse<>(processContentAndTargetResponse);
	}

	/**
	 * 하위 작업 메타데이터 Builder
	 *
	 * @param subProcess
	 * @param workerUUID
	 * @return
	 */
	// CONVERT metadata - SUB PROCESS, worker 권한 확인
	private ProcessMetadataResponse.SubProcess buildMetadataSubProcess(SubProcess subProcess, String workerUUID) {
		String workerSourceUUID = subProcess.getWorkerUUID();
		// 권한 확인
		ProcessMetadataResponse.SubProcess build;
		if (workerSourceUUID.equals(workerUUID)) {
			ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(workerUUID);
			build = ProcessMetadataResponse.SubProcess.builder()
				.subTaskId(subProcess.getId())
				.subTaskName(subProcess.getName())
				.priority(subProcess.getPriority())
				.stepTotal(subProcess.getJobList().size())
				.startDate(subProcess.getStartDate())
				.endDate(subProcess.getEndDate())
				.conditions(subProcess.getConditions())
				.progressRate(subProcess.getProgressRate())
				.workerUUID(workerSourceUUID)
				.workerName(userInfoResponse.getData().getNickname())
				.workerProfile(userInfoResponse.getData().getProfile())
				.syncDate(subProcess.getUpdatedDate())
				.syncUserUUID(subProcess.getWorkerUUID())
				.isRecent(subProcess.getIsRecent())
				.steps(buildJobList(subProcess.getJobList()))
				.build();
		} else
			build = null;
		return build;
	}

	/**
	 * 스텝 리스트 Builder
	 *
	 * @param jobs
	 * @return
	 */
	// CONVERT metadata - JOB LIST
	private List<ProcessMetadataResponse.Job> buildJobList(List<Job> jobs) {
		List<ProcessMetadataResponse.Job> metaJobList = new ArrayList<>();
		for (Job job : jobs) {
			ProcessMetadataResponse.Job metadataJob = buildMetadataJob(job);
			metaJobList.add(metadataJob);
		}
		return metaJobList;
	}

	/**
	 * 스텝 메타데이터 Builder
	 *
	 * @param job
	 * @return
	 */
	// CONVERT metadata - JOB
	private ProcessMetadataResponse.Job buildMetadataJob(Job job) {
		return ProcessMetadataResponse.Job.builder()
			.id(job.getId())
			.name(job.getName())
			.priority(job.getPriority())
			.actionTotal(job.getReportList().size())
			.conditions(job.getConditions())
			.isReported(job.getIsReported())
			.progressRate(job.getProgressRate())
			.reports(buildReportList(job.getReportList()))
			.result(job.getResult())
			.build();
	}

	/**
	 * 페이퍼 리스트 Builder
	 *
	 * @param reports
	 * @return
	 */
	// CONVERT metadata - REPORT LIST
	private List<ProcessMetadataResponse.Report> buildReportList(List<Report> reports) {
		List<ProcessMetadataResponse.Report> metaReportList = new ArrayList<>();
		for (Report report : reports) {
			ProcessMetadataResponse.Report metadataReport = buildMetadataReport(report);
			metaReportList.add(metadataReport);
		}
		return metaReportList;
	}

	/**
	 * 페이퍼 메타데이터 Builder
	 *
	 * @param report
	 * @return
	 */
	// CONVERT metadata - REPORT
	private ProcessMetadataResponse.Report buildMetadataReport(Report report) {
		return ProcessMetadataResponse.Report.builder()
			.id(report.getId())
			.actions(buildReportItemList(report.getItemList()))
			.build();
	}

	/**
	 * 액션 리스트 Builder
	 *
	 * @param items
	 * @return
	 */
	// CONVERT metadata - REPORT ITEM LIST
	private List<ProcessMetadataResponse.ReportItem> buildReportItemList(List<Item> items) {
		List<ProcessMetadataResponse.ReportItem> metaReportItemList = new ArrayList<>();
		for (Item reportItem : items) {
			ProcessMetadataResponse.ReportItem metadataReportItem = buildMetadataReportItem(reportItem);
			metaReportItemList.add(metadataReportItem);
		}
		return metaReportItemList;
	}

	/**
	 * 액션 메타데이터 Builder
	 *
	 * @param item
	 * @return
	 */
	// CONVERT metadata - REPORT ITEM
	private ProcessMetadataResponse.ReportItem buildMetadataReportItem(Item item) {
		return ProcessMetadataResponse.ReportItem.builder()
			.id(item.getId())
			.priority(item.getPriority())
			.type(item.getType())
			.title(item.getTitle())
			.answer(item.getAnswer())
			.photoFile(item.getPath())
			.result(item.getResult())
			.build();
	}

	/**
	 * 이슈와
	 *
	 * @param uploadWorkResult
	 * @return View에서 보고하는 내용을 저장(issue 포함)
	 */
	@Transactional
	public ApiResponse<WorkResultSyncResponse> uploadOrSyncWorkResult(WorkResultSyncRequest uploadWorkResult) {
		// 1. 작업 내용 가져오기
		if (uploadWorkResult.getTasks() != null && uploadWorkResult.getTasks().size() > 0) {
			uploadWorkResult.getTasks().forEach(this::syncProcessResult);
		}

		// 2. 동기화 이슈 가져오기
		if (uploadWorkResult.getIssues() != null && uploadWorkResult.getIssues().size() > 0) {
			List<WorkResultSyncRequest.IssueResult> issueResultList = uploadWorkResult.getIssues();
			syncIssue(issueResultList);
		}

		// TODO : 응답을 동기화 결과 반환해야 함..
		return new ApiResponse<>(new WorkResultSyncResponse(true, LocalDateTime.now()));
	}

	// 작업 및 하위작업 내용 동기화
	public void syncProcessResult(WorkResultSyncRequest.ProcessResult processResult) {

		if (processResult == null) {
			throw new ProcessServiceException(ErrorCode.ERR_PROCESS_WORK_RESULT_SYNC);
		}

		// 3. 공정 불러오기
		Process process = this.processRepository.findById(processResult.getId())
			.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));

		// 3-2. 동기화 대상 세부 작업 가져오기
		// 3-2-1. 동기화 대상 세부 작업 아이디 리스트 추출
		HashMap<Long, WorkResultSyncRequest.SubProcessWorkResult> subProcessWorkResultHashMap = new HashMap<>();
		processResult.getSubTasks().forEach(subProcessWorkResult -> {
			subProcessWorkResultHashMap.put(subProcessWorkResult.getId(), subProcessWorkResult);
		});

		Set<Long> targetSubProcessIdList = subProcessWorkResultHashMap.keySet();

		// 3-2-2. 동기화 대상 세부 작업 원본 가져오기
		List<SubProcess> subProcesses = this.subProcessRepository.findByProcessAndIdIsIn(
			process, targetSubProcessIdList);

		// 동기화 시간
		LocalDateTime nowTime = LocalDateTime.now();
		// 프로세스 동기화시간 업데이트 여부
		AtomicBoolean updateProcess = new AtomicBoolean(false);

		// 3-2-3. 세부 작업 내용 동기화
		subProcesses.forEach(subProcess -> {
			WorkResultSyncRequest.SubProcessWorkResult subProcessWorkResult = subProcessWorkResultHashMap.get(
				subProcess.getId());
			// 할당된 작업자와 동기화 작업자 동일 여부 검사 TODO : 검증 필요.
			if (subProcess.getWorkerUUID().equals(subProcessWorkResult.getSyncUserUUID())) {
				subProcess.setReportedDate(nowTime);
				subProcessRepository.save(subProcess);

				updateProcess.set(true);

				if (subProcessWorkResult.getSteps() != null) {
					syncJobWork(subProcessWorkResult.getSteps(), subProcessWorkResult.getSyncUserUUID());
				}
			} else {
				throw new ProcessServiceException(ErrorCode.ERR_WORKER_NOT_EQUAL_SYNC);
			}
		});

		// 공정의 동기화 시간 업데이트
		if (updateProcess.get()) {
			process.setReportedDate(nowTime);
			processRepository.save(process);
		}
	}

	// 스텝 내용 동기화
	private void syncJobWork(List<WorkResultSyncRequest.JobWorkResult> jobWorkResults, String syncUserUUID) {
		log.info("WORKER:[{}] Job Result Synchronized Begin", syncUserUUID);
		jobWorkResults.forEach(jobWorkResult -> {
			Job job = this.jobRepository.findById(jobWorkResult.getId()).
				orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_PROCESS_WORK_RESULT_SYNC));
			job.setIsReported(jobWorkResult.getIsReported());
			job.setResult(jobWorkResult.getResult() == null ? INIT_RESULT : jobWorkResult.getResult());
			jobRepository.save(job);

			if (jobWorkResult.getReports() != null) {
				syncReportWork(jobWorkResult.getReports());
			}
			if (jobWorkResult.getIssues() != null) {
				syncIssueWork(jobWorkResult.getIssues(), syncUserUUID, job);
			}
		});
	}

	// 페이퍼 내용 동기화
	private void syncReportWork(List<WorkResultSyncRequest.ReportWorkResult> reportWorkResults) {
		reportWorkResults.forEach(reportWorkResult -> {
			if (reportWorkResult.getActions() != null) {
				syncReportItemWork(reportWorkResult.getActions());
			}
		});
	}

	// 페이퍼의 행동 동기화
	private void syncReportItemWork(List<WorkResultSyncRequest.ReportItemWorkResult> reportItemWorkResults) {
		reportItemWorkResults.forEach(reportItemWorkResult -> {
			Item item = this.itemRepository.findById(reportItemWorkResult.getId())
				.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_PROCESS_WORK_RESULT_SYNC));
			if (!StringUtils.isEmpty(reportItemWorkResult.getPhotoFile())) {
				item.setPath(getFileUploadUrl(reportItemWorkResult.getPhotoFile()));
			}
			item.setAnswer(reportItemWorkResult.getAnswer());
			item.setResult(reportItemWorkResult.getResult() == null ? INIT_RESULT : reportItemWorkResult.getResult());
			itemRepository.save(item);
		});
	}

	// 작업 이슈 동기화
	private void syncIssueWork(
		List<WorkResultSyncRequest.WorkIssueResult> workIssueResults, String syncUserUUID, Job job
	) {
		// insert
		workIssueResults.forEach(workIssueResult -> {
			Issue issue = Issue.builder()
				.content(workIssueResult.getCaption())
				.workerUUID(syncUserUUID)
				.build();
			if (!StringUtils.isEmpty(workIssueResult.getPhotoFile())) {
				issue.setPath(getFileUploadUrl(workIssueResult.getPhotoFile()));
			}
			issue.setJob(job);
			this.issueRepository.save(issue);
		});
	}

	// 트러블메모 동기화
	private void syncIssue(List<WorkResultSyncRequest.IssueResult> issueResults) {
		// insert
		issueResults.forEach(issueResult -> {
			Issue issue = Issue.globalIssueBuilder()
				.content(issueResult.getCaption())
				.workerUUID(issueResult.getWorkerUUID())
				.build();
			if (!StringUtils.isEmpty(issueResult.getPhotoFile())) {
				issue.setPath(getFileUploadUrl(issueResult.getPhotoFile()));
			}
			log.info("IssueResult: {}", issueResult);
			log.info("WorkerUUID: [{}]", issueResult.getWorkerUUID());
			log.info("Global Issue: [{}]", issue);
			this.issueRepository.save(issue);
		});
	}

	/**
	 * 전체 작업 목록 조회
	 *
	 * @param workspaceUUID 워크스페이스 UUID
	 * @param search        검색어
	 * @param userUUID      사용자 UUID
	 * @param filter        작업 컨디션
	 * @param pageable      페이징
	 * @return
	 */
	public ApiResponse<ProcessListResponse> getProcessList(
		String myUUID, String workspaceUUID, String search, List<Conditions> filter, Pageable pageable,
		String targetType
	) {
		List<String> userUUIDList = new ArrayList<>();

		if (Objects.nonNull(search) && Objects.nonNull(workspaceUUID)) {
			// 사용자 검색 (이메일 및 닉네임으로 검색)
			List<UserInfoResponse> userInfos = getUserInfo(search, workspaceUUID);

			// 이메일 및 닉네임으로 검색 된 사용자 UUID
			userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
		}

		Page<Process> processPage = null;

		// 정렬에 Conditions가 들어왔을 경우 - Table의 Column이 아니기 때문에 List를 조작하여 처리.
		if ("conditions".equals(pageable.getSort().toList().get(0).getProperty())) {
			//List<Process> processList = this.processRepository.findByWorkspaceUUID(workspaceUUID);
			List<Process> processList = this.processRepository.findByWorkspaceUUIDAndTargetType(
				workspaceUUID, targetType);

			List<Process> edit = new ArrayList<Process>(processList);

			edit.sort(new Comparator<Process>() {
				@Override
				public int compare(Process arg0, Process arg1) {
					String condition1 = arg0.getConditions().getMessage();
					String condition2 = arg1.getConditions().getMessage();

					return condition1.compareTo(condition2);
				}
			});

			processPage = new PageImpl<>(edit, pageable, edit.size());
		}
		// 그 외 컬럼은 쿼리로 처리 가능 (단, issuesTotal은 처리 불가)
		else {
			if (Objects.nonNull(myUUID)) {
				// 내 작업 목록
				processPage = this.processRepository.getMyTask(
					filter, myUUID, workspaceUUID, search, pageable, targetType);
			} else {
				processPage = this.processRepository.getProcessPageSearchUser(
					filter, workspaceUUID, search, userUUIDList, pageable, targetType
				);
			}

			/* 페이징을 거치고 난 후의 값이 아닌 전체 process 데이터를 기준으로 필터링 하기 위해 아래 코드는 주석처리하고 위에서 처리함.
			if (filter != null && filter.size() > 0 && !filter.contains(Conditions.ALL)) {
				processPage = filterConditionsProcessPage(processPage, filter, pageable);
			}*/
		}
		//
		//        if (filter != null && filter.size() > 0 && !filter.contains(Conditions.ALL)) {
		//            processPage = this.processRepository.getProcessPageSearchUser(workspaceUUID, search, userUUIDList, pageable);
		//
		//            processPage = filterConditionsProcessPage(processPage, filter, pageable);
		//        } else {
		//            // TODO : 검증 필요, 타겟 데이터가 중복발생하는 경우가 배제되어 공정이 누락되거나, 중복될 수 있음.
		//            //processPage = this.processRepository.getProcessPageSearchUser(workspaceUUID, search, userUUIDList, pageableCustom);
		////            processPage = this.processRepository.getProcessPageSearchUser(workspaceUUID, search, userUUIDList, pageable);
		//            processPage = this.processRepository.getProcessPageSearchUser(workspaceUUID, search, userUUIDList, pageable);
		//        }
		return getProcessesPageResponseApiResponse(pageable, processPage);
	}

	/**
	 * 작업의 컨디션을 필터링
	 *
	 * @param processList
	 * @param filter
	 * @param pageable
	 * @return
	 */
	private Page<Process> filterConditionsProcessPage(
		Page<Process> processList, List<Conditions> filter, Pageable pageable
	) {
		List<Process> processes = new ArrayList<>();

		for (Process process : processList) {
			// 상태가 일치하는 공정만 필터링
			if (filter.contains(process.getConditions())) {
				processes.add(process);
			}
		}
		return new PageImpl<>(processes, pageable, processes.size());
	}

	private ApiResponse<ProcessListResponse> getProcessesPageResponseApiResponse(
		Pageable pageable, Page<Process> processPage
	) {

		List<ProcessInfoResponse> processInfoResponseList = processPage.stream().map(process -> {
			ProcessInfoResponse processInfoResponse = modelMapper.map(process, ProcessInfoResponse.class);
			processInfoResponse.setSubTaskAssign(this.getSubProcessesAssign(process));
			processInfoResponse.setDoneCount((int)process.getSubProcessList()
				.stream()
				.filter(subProcess -> subProcess.getConditions() == Conditions.COMPLETED
					|| subProcess.getConditions() == Conditions.SUCCESS)
				.count());
			processInfoResponse.setIssuesTotal(this.processRepository.getCountIssuesInProcess(process.getId()));
			processInfoResponse.setSubTaskTotal(process.getSubProcessList().size());

            /*
            작업 조회에서 컨텐츠 용량이 필요해서 작업서버를 통해 조회하는 로직 추가
            (VECHOSYS-1293)
            */
			long contentSize = this.contentRestService.getContentInfo(process.getContentUUID())
				.getData()
				.getContentSize();
			processInfoResponse.setContentSize(contentSize);

			List<ProcessTargetResponse> targetList = process.getTargetList().stream().map(target -> {
				ProcessTargetResponse targetResponse = ProcessTargetResponse.builder()
					.id(target.getId())
					.type(target.getType())
					.data(target.getData())
					.imgPath(target.getImgPath())
					.size(target.getSize())
					.build();

				return targetResponse;
			}).collect(Collectors.toList());

			processInfoResponse.setTargets(targetList);

			return processInfoResponse;
		}).collect(Collectors.toList());

		PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.totalPage(processPage.getTotalPages())
			.totalElements(processPage.getTotalElements())
			.build();

		return new ApiResponse<>(new ProcessListResponse(processInfoResponseList, pageMetadataResponse));
	}

	/**
	 * 작업 종료
	 *
	 * @param taskId
	 * @param actorUUID
	 * @return
	 * @Description 작업 수행 중의 여부와 관계없이 종료됨. 뷰에서는 오프라인으로 작업 후 최종 동기화이기 때문.
	 */
	@Transactional
	public ApiResponse<ProcessInfoResponse> setClosedProcess(Long taskId, CheckProcessOwnerRequest request) {
		// 공정조회
		Process process = this.processRepository.findById(taskId)
			.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));

		log.info("actorUUID : {}, contentManagerUUID : {}", request.getActorUUID(), process.getContentManagerUUID());
/*

		//워크스페이스 설정 작업 종료 권한 체크 - TASK_FINISH_ROLE_SETTING
		WorkspaceSettingInfoListResponse workspaceSettingInfoListResponse = getWorkspaceSettingInfoListResponse(
			process.getWorkspaceUUID());
		if (workspaceSettingInfoListResponse == null) {
			throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);
		}
		Optional<WorkspaceSettingInfoResponse> workspaceSettingInfoResponseOptional = workspaceSettingInfoListResponse.getWorkspaceSettingInfoList()
			.stream()
			.filter(workspaceSettingInfoResponse -> workspaceSettingInfoResponse.getSettingName()
				.equals("TASK_FINISH_ROLE_SETTING"))
			.findAny();

		//기본값은 모든 유저. UNUSED또는 MASTER_OR_MANAGER_MEMBER인 경우에는 기본값에 따름.
		if (workspaceSettingInfoResponseOptional.isPresent()) {
			String settingValue = workspaceSettingInfoResponseOptional.get().getSettingValue();
			AllMemberInfoResponse workspaceUserInfoResponse = getWorkspaceUserInfoResponse(
				process.getWorkspaceUUID(), request.getActorUUID());
			if (workspaceUserInfoResponse == null) {
				throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);
			}
			String requestUserWorkspaceRole = workspaceUserInfoResponse.getRole();
			log.info(
				"[TASK FINISH] workspace setting value : [{}], user workspace role : [{}]", settingValue,
				requestUserWorkspaceRole
			);
			//마스터인 유저만 허용하는 경우
			if (settingValue.equals("MASTER") && !requestUserWorkspaceRole.equals("MASTER")) {
				throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);
			}
			//마스터 또는 매니저 유저만 허용하는 경우
			if (settingValue.equals("MASTER_OR_MANAGER") && !requestUserWorkspaceRole.matches("MASTER|MANAGER")) {
				throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);
			}
		}
*/


		/* https://virtualconnection.atlassian.net/secure/RapidBoard.jspa?rapidView=223&projectKey=DPLA&modal=detail&selectedIssue=DPLA-1126&assignee=5dbfb7e2ffc8c10df0ed7a13
		이슈로 작업 종료 권한 체크 주석처리.
		if (!request.getActorUUID().equals(process.getContentManagerUUID())) {
			throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);
		}*/

		// 마감 상태로 변경
		process.setState(State.CLOSED);
		this.processRepository.save(process);

		// TODO : 다른 서비스를 호출하는 것이 옳은 것인지 확인이 필요. redirect를 해야하나?
		// 작업 상세조회하여 반환
		return this.getProcessInfo(process.getId());
	}

	/**
	 * 작업 상세 정보
	 *
	 * @param processId
	 * @return
	 */
	public ApiResponse<ProcessInfoResponse> getProcessInfo(Long processId) {
		// 작업 단건 조회
		Process process = this.processRepository.findById(processId)
			.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));

		// 타겟 정보
		List<ProcessTargetResponse> targetResponseList = process.getTargetList().stream().map(target -> {
			return ProcessTargetResponse.builder()
				.id(target.getId())
				.type(target.getType())
				.data(target.getData())
				.imgPath(target.getImgPath())
				.build();
		}).collect(Collectors.toList());

		ProcessInfoResponse processInfoResponse = modelMapper.map(process, ProcessInfoResponse.class);
		// contentUUID 추가
		processInfoResponse.setContentUUID(process.getContentUUID());
		processInfoResponse.setSubTaskTotal(Optional.of(process.getSubProcessList().size()).orElse(0));
		processInfoResponse.setDoneCount((int)process.getSubProcessList()
			.stream()
			.filter(subProcess -> subProcess.getConditions() == Conditions.COMPLETED
				|| subProcess.getConditions() == Conditions.SUCCESS)
			.count());
		processInfoResponse.setIssuesTotal(this.processRepository.getCountIssuesInProcess(process.getId()));
		processInfoResponse.setSubTaskAssign(this.getSubProcessesAssign(process));
		processInfoResponse.setTargets(targetResponseList);
		return new ApiResponse<>(processInfoResponse);
	}

	/**
	 * 작업 및 하위의 모든 (세부작업, 단계 등) 정보를 업데이트.
	 *
	 * @param editProcessRequest
	 * @return
	 */
	@Transactional
	public ResponseMessage updateProcess(EditProcessRequest editProcessRequest) {
		// 작업 편집
		try {
			// 1. 작업편집가능여부판단
			// 1-1. 작업 단건 조회
			//Process updateSourceProcess = this.processRepository.getProcessInfo(editProcessRequest.getProcessId()).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
			Process updateSourceProcess = this.processRepository.findById(editProcessRequest.getTaskId())
				.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));

			//워크스페이스 설정 검증 - TASK_UPDATE_ROLE_SETTING
			WorkspaceSettingInfoListResponse workspaceSettingInfoListResponse = getWorkspaceSettingInfoListResponse(
				updateSourceProcess.getWorkspaceUUID());
			if (workspaceSettingInfoListResponse == null) {
				throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);
			}
			Optional<WorkspaceSettingInfoResponse> workspaceSettingInfoResponseOptional = workspaceSettingInfoListResponse
				.getWorkspaceSettingInfoList()
				.stream()
				.filter(workspaceSettingInfoResponse -> workspaceSettingInfoResponse.getSettingName()
					.equals("TASK_UPDATE_ROLE_SETTING"))
				.findAny();

			//설정 값이 있을 때만 검사
			//기본값은 마스터 또는 매니저 . UNUSED또는 MASTER_OR_MANAGER인 경우에는 기본값에 따름.
			if (workspaceSettingInfoResponseOptional.isPresent()) {
				String settingValue = workspaceSettingInfoResponseOptional.get().getSettingValue();
				AllMemberInfoResponse workspaceUserInfoResponse = getWorkspaceUserInfoResponse(
					updateSourceProcess.getWorkspaceUUID(), editProcessRequest.getActorUUID());
				if (workspaceUserInfoResponse == null) {
					throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);
				}
				String requestUserWorkspaceRole = workspaceUserInfoResponse.getRole();
				log.info(
					"[TASK UPDATE] workspace setting value : [{}], user workspace role : [{}]", settingValue,
					requestUserWorkspaceRole
				);
				//마스터인 유저만 허용하는 경우
				if (settingValue.equals("MASTER") && !requestUserWorkspaceRole.equals("MASTER")) {
					throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);
				}
				//마스터 또는 매니저 유저만 허용하는 경우
				if (settingValue.equals("MASTER_OR_MANAGER") && !requestUserWorkspaceRole.matches("MASTER|MANAGER")) {
					throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);
				}
			}
/*
            if (!updateSourceProcess.getContentManagerUUID().equals(editProcessRequest.getActorUUID()))
                throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);*/

			// 공정진행중여부확인 - 편집할 수 없는 상태라면 에러
			if (updateSourceProcess.getState() == State.CLOSED || updateSourceProcess.getState() == State.DELETED) {
				throw new ProcessServiceException(ErrorCode.ERR_PROCESS_UPDATED);
			}

			// 2. 공정 상세정보 편집
			updateSourceProcess.setStartDate(Optional.of(editProcessRequest)
				.map(EditProcessRequest::getStartDate)
				.orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")));
			updateSourceProcess.setEndDate(Optional.of(editProcessRequest)
				.map(EditProcessRequest::getEndDate)
				.orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")));
			updateSourceProcess.setPosition(
				Optional.of(editProcessRequest).map(EditProcessRequest::getPosition).orElseGet(() -> ""));
			updateSourceProcess = this.processRepository.save(updateSourceProcess);

			// 2-1. 세부공정 상세정보 편집
			editProcessRequest.getSubTaskList().forEach(editSubProcessRequest -> {
				if (!(boolean)subTaskService.updateSubProcess(
					editSubProcessRequest.getSubTaskId(), editSubProcessRequest).getData().get("result"))
					new ProcessServiceException(ErrorCode.ERR_PROCESS_UPDATED);
			});

			return new ResponseMessage().addParam("result", true);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("PROCESS UPDATE ERROR : {}", e.getMessage());
			throw new ProcessServiceException(ErrorCode.ERR_PROCESS_UPDATED);
		}
	}

	/**
	 * 작업 삭제
	 *
	 * @param checkProcessOwnerRequest
	 * @return
	 */
	@Transactional
	public ApiResponse<ProcessSimpleResponse> deleteTheProcess(CheckProcessOwnerRequest checkProcessOwnerRequest) {
		Long processId = checkProcessOwnerRequest.getTaskId();
		String actorUUID = checkProcessOwnerRequest.getActorUUID();
		// 작업 단건 조회
		Process process = this.processRepository.findById(processId)
			.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));

		//워크스페이스 기능 설정 검증 -> TASK_DELETE_ROLE_SETTING
		WorkspaceSettingInfoListResponse workspaceSettingInfoListResponse = getWorkspaceSettingInfoListResponse(
			process.getWorkspaceUUID());
		if (workspaceSettingInfoListResponse == null) {
			throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);
		}
		Optional<WorkspaceSettingInfoResponse> workspaceSettingInfoResponseOptional = workspaceSettingInfoListResponse.getWorkspaceSettingInfoList()
			.stream()
			.filter(workspaceSettingInfoResponse -> workspaceSettingInfoResponse.getSettingName()
				.equals("TASK_DELETE_ROLE_SETTING"))
			.findAny();

		//설정 값이 있을 때만 검사
		//기본값은 마스터 또는 매니저 . UNUSED또는 MASTER_OR_MANAGER인 경우에는 기본값에 따름.
		if (workspaceSettingInfoResponseOptional.isPresent()) {
			String settingValue = workspaceSettingInfoResponseOptional.get().getSettingValue();
			AllMemberInfoResponse workspaceUserInfoResponse = getWorkspaceUserInfoResponse(
				process.getWorkspaceUUID(), checkProcessOwnerRequest.getActorUUID());
			if (workspaceUserInfoResponse == null) {
				throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);
			}
			String requestUserWorkspaceRole = workspaceUserInfoResponse.getRole();
			log.info(
				"[TASK DELETE] workspace setting value : [{}], user workspace role : [{}]", settingValue,
				requestUserWorkspaceRole
			);
			//마스터인 유저만 허용하는 경우
			if (settingValue.equals("MASTER") && !requestUserWorkspaceRole.equals("MASTER")) {
				throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);
			}
			//마스터 또는 매니저 유저만 허용하는 경우
			if (settingValue.equals("MASTER_OR_MANAGER") && !requestUserWorkspaceRole.matches("MASTER|MANAGER")) {
				throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);
			}
		}

		// 삭제 조건 중 컨텐츠의 작업 전환상태를 NO로 만들어야 삭제조건에 부합하므로 미리 조건처리함.
		this.contentRestService.contentConvertHandler(process.getContentUUID(), YesOrNo.NO);

		// 컨텐츠 삭제
		String[] processes = {process.getContentUUID()};

		ContentDeleteRequest contentDeleteRequest = new ContentDeleteRequest();

		contentDeleteRequest.setContentUUIDs(processes);
		//contentDeleteRequest.setWorkerUUID(actorUUID);
		contentDeleteRequest.setWorkspaceUUID(process.getWorkspaceUUID());
		ApiResponse<ContentDeleteListResponse> apiResponse = this.contentRestService.contentDeleteRequestHandler(
			contentDeleteRequest);

		log.debug("apiResponse : {}", apiResponse.getData().toString());

		// TODO : 컨텐츠 삭제 실패시 롤백 구현 안됨

		// TODO : 공정 삭제시 히스토리를 남기고 상태값만 바꾼다면, 이슈, 리포트 등 작업 하위의 아이템들을 어떻게 할 것인지 확인해야 함.

		process.getSubProcessList().stream().forEach(subProcess -> {
			subProcess.getJobList().stream().forEach(job -> {
				job.getReportList().stream().forEach(report -> {
					report.getItemList().stream().forEach(item -> {
						//item 삭제
						this.itemRepository.delete(item);
					});
					//report 삭제
					this.reportRepository.delete(report);
				});
				job.getIssueList().stream().forEach(issue -> {
					//issue 삭제
					this.issueRepository.delete(issue);
				});
				//job 삭제
				this.jobRepository.delete(job);
			});
			//sub process 삭제
			this.subProcessRepository.delete(subProcess);
		});

		process.getTargetList().stream().forEach(target -> {
			//target 삭제
			this.targetRepository.delete(target);
		});

		//process 삭제
		this.processRepository.delete(process);

		return new ApiResponse<>(
			new ProcessSimpleResponse(apiResponse.getData().getDeleteResponseList().get(0).getResult()));
	}

	private AllMemberInfoResponse getWorkspaceUserInfoResponse(
		String workspaceUUID, String userUUID
	) {
		ApiResponse<AllMemberInfoResponse> apiResponse = workspaceRestService
			.getWorkspaceUserInfo(workspaceUUID, userUUID);
		if (apiResponse.getCode() != 200) {
			log.error("[GET WORKSPACE USER INFO] response message : {}", apiResponse.getMessage());
			return null;
		}
		return apiResponse.getData();
	}

	private WorkspaceSettingInfoListResponse getWorkspaceSettingInfoListResponse(String workspaceUUID) {
		ApiResponse<WorkspaceSettingInfoListResponse> apiResponse = workspaceRestService.getWorkspaceSettingList(
			workspaceUUID, "WORKSTATION");
		if (apiResponse.getCode() != 200) {
			log.error("[GET WORKSPACE SETTING INFO LIST] response message : {}", apiResponse.getMessage());
			return null;
		}
		return apiResponse.getData();
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
	 * 공정조회시 세부공정에 할당되어 있는 작업자들의 목록을 조회
	 *
	 * @param process
	 * @return
	 */
	private List<SubProcessAssignedResponse> getSubProcessesAssign(Process process) {
		// 작업자 목록 조회
		List<SubProcessAssignedResponse> subProcessesAssign = new ArrayList<>();
		for (SubProcess subProcess : process.getSubProcessList()) {
			ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(
				subProcess.getWorkerUUID());
			subProcessesAssign.add(SubProcessAssignedResponse.builder()
				.subTaskId(subProcess.getId())
				.workerUUID(subProcess.getWorkerUUID())
				.workerName(userInfoResponse.getData().getNickname())
				.workerProfile(userInfoResponse.getData().getProfile())
				.build()
			);
		}
		return subProcessesAssign;
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

	/**
	 * 이미지 업로드 후 업로드 경로 반환
	 *
	 * @param targetData
	 * @return
	 */
	private String getImgPath(String targetData) {

		String qrString = "";

		try {
			// 현재는 QR밖에 없어서 모든 데이터를 QR 이미지로 변환. 추후 다른 타입이 있을 경우 수정 필요.
			BufferedImage qrImage = QRcodeGenerator.generateQRCodeImage(targetData, 240, 240);

			ByteArrayOutputStream os = new ByteArrayOutputStream();

			ImageIO.write(qrImage, "png", os);
			os.toByteArray();

			qrString = Base64.getEncoder().encodeToString(os.toByteArray());

		} catch (Exception e) {
			e.printStackTrace();
		}

		String imgPath = this.fileUploadService.base64ImageUpload(qrString);

		return imgPath;
	}

	/**
	 * 워크스페이스 멤버 정보
	 *
	 * @param workspaceUUID
	 * @return
	 */
	public ApiResponse<WorkspaceUserListResponse> getWorkspaceUserInfo(String workspaceUUID, Pageable pageable) {
		log.debug(workspaceUUID);
		List<MemberInfoDTO> memberList = this.workspaceRestService.getSimpleWorkspaceUserList(workspaceUUID)
			.getData()
			.getMemberInfoList();

		List<String> userUUIDList = new ArrayList<>();

		for (MemberInfoDTO t : memberList) {
			userUUIDList.add(t.getUuid());
		}

		List<WorkspaceUserInfoResponse> resultList = new ArrayList<>();

		for (MemberInfoDTO memberInfo : memberList) {

			String userUUID = memberInfo.getUuid();

			List<ContentCountResponse> countList = this.contentRestService.countContents(workspaceUUID, userUUIDList)
				.getData();
			List<SubProcess> subProcessList = this.subProcessRepository.getSubProcessList(workspaceUUID, userUUID);
			LocalDateTime lastestTime = this.subProcessRepository.getLastestReportedTime(workspaceUUID, userUUID);

			int ing = 0;
			for (SubProcess subProcess : subProcessList) {
				Process process = subProcess.getProcess();
				State state = process.getState();
				String processWorkspaceUUID = process.getWorkspaceUUID();
				// 공정상태가 종료 또는 삭제가 아니고 그리고 세부공정상태가 대기상태가 아닐 때, 마지막으로 워크스페이스가 동일한 프로세스에 대해 필터
				/*
				if ((state != State.CLOSED || state != State.DELETED) && subProcess.getConditions() != Conditions.WAIT
					&& (workspaceUUID == null || processWorkspaceUUID.equals(workspaceUUID))) {
					ing++;
				}*/

				// 세부공정 상태가 진행중일때 && 워크스페이스가 동일한 프로세스에 대해 필터링 함.
				//https://virtualconnection.atlassian.net/browse/DPLA-535?atlOrigin=eyJpIjoiMmI1MGU1ZWUzNjdjNGUxZGIwZDA5YmU4Mzg1ODZkNzciLCJwIjoiaiJ9 으로 아래와 같이 수정함
				if (subProcess.getConditions() == Conditions.PROGRESSING && (workspaceUUID == null
					|| processWorkspaceUUID.equals(workspaceUUID))) {
					ing++;
				}
			}

			long countContent = 0L;

			for (ContentCountResponse response : countList) {
				if (memberInfo.getUuid().equals(response.getUserUUID())) {
					countContent = response.getCountContents();
				}
			}

			int percent = 0;

			if (!subProcessList.isEmpty()) {
				percent = (int)(((double)ing / (double)subProcessList.size()) * 100);
			}

			WorkspaceUserInfoResponse workspaceUserInfoResponse = new WorkspaceUserInfoResponse();
			workspaceUserInfoResponse.setWorkerUUID(memberInfo.getUuid());
			workspaceUserInfoResponse.setWorkerName(memberInfo.getNickName());
			workspaceUserInfoResponse.setWorkerProfile(memberInfo.getProfile());
			workspaceUserInfoResponse.setWorkerEmail(memberInfo.getEmail());
			workspaceUserInfoResponse.setCountAssigned(subProcessList.size());
			workspaceUserInfoResponse.setCountProgressing(ing);
			workspaceUserInfoResponse.setCountContent(countContent);
			workspaceUserInfoResponse.setPercent(percent);
			workspaceUserInfoResponse.setLastestReportedTime(lastestTime);

			resultList.add(workspaceUserInfoResponse);
		}

		List<WorkspaceUserInfoResponse> list = new ArrayList<>();

		// 기존 페이징 처리와는 다른 부분이어서 따로 페이징 처리함.
		int currentPage = pageable.getPageNumber();
		int currentSize = pageable.getPageSize();
		int totalElements = resultList.size();
		int totalPage = totalElements / currentSize;

		if (totalElements % currentSize != 0) {
			totalPage += 1;
		}

		int fromIdx = 0;
		int toIdx = 0;

		if (currentPage == 0) {
			fromIdx = currentPage;
			toIdx = currentSize;
		} else {
			fromIdx = currentPage * currentSize;
			toIdx = (currentPage + 1) * currentSize;
		}

		if (toIdx > resultList.size()) {
			toIdx = resultList.size();
		}

		list = resultList.subList(fromIdx, toIdx);

		PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.totalPage(totalPage)
			.totalElements(totalElements)
			.build();

		return new ApiResponse<>(new WorkspaceUserListResponse(list, pageMetadataResponse));
	}

	/**
	 * sync시 필요한 데이터와 동일한 형태의 데이터를 만들기
	 *
	 * @param taskId
	 * @param subTaskIds
	 * @return
	 */
	public ApiResponse<WorkSyncResponse> getSyncMeta(Long taskId, Long[] subTaskIds) {

		Process process = this.processRepository.findById(taskId)
			.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));

		WorkSyncResponse workSyncResponse = new WorkSyncResponse();

		WorkSyncResponse.ProcessResult processResult = buildSyncProcess(process, subTaskIds);

		List<WorkSyncResponse.ProcessResult> resultList = new ArrayList<>();

		resultList.add(processResult);

		workSyncResponse.setTasks(resultList);

		return new ApiResponse<>(workSyncResponse);
	}

	/**
	 * 작업 싱크 메타데이터 Builder
	 *
	 * @param process
	 * @param subTaskIds
	 * @return
	 */
	// CONVERT syncdata - PROCESS LIST
	private WorkSyncResponse.ProcessResult buildSyncProcess(Process process, Long[] subTaskIds) {

		List<WorkSyncResponse.SubProcessWorkResult> syncSubProcessList = buildSyncSubProcess(
			process.getSubProcessList(), subTaskIds);

		if (syncSubProcessList.isEmpty())
			return null;
		else {
			return WorkSyncResponse.ProcessResult.builder()
				.id(process.getId())
				.subTasks(syncSubProcessList)
				.build();
		}
	}

	/**
	 * 하위 작업 싱크 리스트 Bulider
	 *
	 * @param subProcesses
	 * @param subTaskIds
	 * @return
	 */
	// CONVERT syncdata - SUB PROCESS LIST
	private List<WorkSyncResponse.SubProcessWorkResult> buildSyncSubProcess(
		List<SubProcess> subProcesses, Long[] subTaskIds
	) {
		List<WorkSyncResponse.SubProcessWorkResult> syncSubProcessList = new ArrayList<>();
		ArrayList<Long> longs = null;
		for (SubProcess subProcess : subProcesses) {
			WorkSyncResponse.SubProcessWorkResult syncSubProcess = null;

			if (subTaskIds != null && subTaskIds.length > 0) {

				for (Long subTaskId : subTaskIds) {
					if (subProcess.getId().equals(subTaskId)) {
						syncSubProcess = buildSyncDataSubProcess(subProcess);
					}
				}
			} else {
				syncSubProcess = buildSyncDataSubProcess(subProcess);
			}

			// 권한으로 인해 세부공정이 없을 수 있으므로 null체크
			if (!Objects.isNull(syncSubProcess)) {
				syncSubProcessList.add(syncSubProcess);
			}
		}
		return syncSubProcessList;
	}

	/**
	 * 하위 작업 싱크 메타데이터 Builder
	 *
	 * @param subProcess
	 * @return
	 */
	// CONVERT syncdata - SUB PROCESS, worker 권한 확인
	private WorkSyncResponse.SubProcessWorkResult buildSyncDataSubProcess(SubProcess subProcess) {
		WorkSyncResponse.SubProcessWorkResult build
			= WorkSyncResponse.SubProcessWorkResult.builder()
			.id(subProcess.getId())
			.syncUserUUID(subProcess.getWorkerUUID())
			.priority(subProcess.getPriority())
			.steps(buildSyncJobList(subProcess.getJobList()))
			.build();

		return build;
	}

	/**
	 * 스텝 싱크 리스트 Builder
	 *
	 * @param jobs
	 * @return
	 */
	// CONVERT syncdata - JOB LIST
	private List<WorkSyncResponse.JobWorkResult> buildSyncJobList(List<Job> jobs) {
		List<WorkSyncResponse.JobWorkResult> syncJobList = new ArrayList<>();
		for (Job job : jobs) {
			WorkSyncResponse.JobWorkResult syncDataJob = buildSyncDataJob(job);
			syncJobList.add(syncDataJob);
		}
		return syncJobList;
	}

	/**
	 * 스텝 싱크 메타데이터 Builder
	 *
	 * @param job
	 * @return
	 */
	// CONVERT syncdata - JOB
	private WorkSyncResponse.JobWorkResult buildSyncDataJob(Job job) {
		return WorkSyncResponse.JobWorkResult.builder()
			.id(job.getId())
			.isReported(job.getIsReported())
			.reports(buildSyncReportList(job.getReportList()))
			.result(job.getResult())
			.build();
	}

	/**
	 * 페이퍼 싱크 리스트 Builder
	 *
	 * @param reports
	 * @return
	 */
	// CONVERT syncdata - REPORT LIST
	private List<WorkSyncResponse.ReportWorkResult> buildSyncReportList(List<Report> reports) {
		List<WorkSyncResponse.ReportWorkResult> syncReportList = new ArrayList<>();
		for (Report report : reports) {
			WorkSyncResponse.ReportWorkResult syncDataReport = buildSyncDataReport(report);
			syncReportList.add(syncDataReport);
		}
		return syncReportList;
	}

	/**
	 * 페이퍼 싱크 메타데이터 Builder
	 *
	 * @param report
	 * @return
	 */
	// CONVERT syncdata - REPORT
	private WorkSyncResponse.ReportWorkResult buildSyncDataReport(Report report) {
		return WorkSyncResponse.ReportWorkResult.builder()
			.id(report.getId())
			.actions(buildSyncReportItemList(report.getItemList()))
			.build();
	}

	/**
	 * 액션 싱크 리스트 Builder
	 *
	 * @param items
	 * @return
	 */
	// CONVERT syncdata - REPORT ITEM LIST
	private List<WorkSyncResponse.ReportItemWorkResult> buildSyncReportItemList(List<Item> items) {
		List<WorkSyncResponse.ReportItemWorkResult> syncReportItemList = new ArrayList<>();

		for (Item reportItem : items) {
			WorkSyncResponse.ReportItemWorkResult syncDataReportItem = buildSyncDataReportItem(reportItem);
			syncReportItemList.add(syncDataReportItem);
		}
		return syncReportItemList;
	}

	/**
	 * 액션 싱크 메타데이터 Bulider
	 *
	 * @param item
	 * @return
	 */
	// CONVERT syncdata - REPORT ITEM
	private WorkSyncResponse.ReportItemWorkResult buildSyncDataReportItem(Item item) {

		return WorkSyncResponse.ReportItemWorkResult.builder()
			.id(item.getId())
			.answer(item.getAnswer())
			.photoFile(item.getPath())
			.result(item.getResult())
			.build();
	}

	@Transactional
	public TaskSecessionResponse deleteAllTaskInfo(String workspaceUUID, String userUUID) {
		log.info("[DELETE ALL TASK] request workspace = [{}], request user = [{}]", workspaceUUID, userUUID);
		List<DailyTotalWorkspace> dailyTotalWorkspaceList = dailyTotalWorkspaceRepository.findAllByWorkspaceUUID(
			workspaceUUID);

		List<Process> processList = processRepository.findByWorkspaceUUID(workspaceUUID);
		if (processList.isEmpty()) {
			return new TaskSecessionResponse(workspaceUUID, true, LocalDateTime.now());
		}
		List<SubProcess> subProcessList = subProcessRepository.findByProcessIn(processList);
		List<Job> jobList = jobRepository.findBySubProcessIn(subProcessList);
		List<Report> reportList = reportRepository.findByJobIn(jobList);
		List<Item> itemList = itemRepository.findByReportIn(reportList);
		List<Issue> issueList = issueRepository.findByJobIn(jobList);
		List<Issue> troubleMemoList = issueRepository.findAllByWorkerUUIDAndJobIsNull(userUUID);
		List<Target> targetList = targetRepository.findByProcessIn(processList);

		// 이미지 삭제 -> item, issue, troubleMemo, target
		itemList.forEach(item -> {
			if (StringUtils.hasText(item.getPath())) {
				fileUploadService.delete(item.getPath());
			}
		});
		issueList.forEach(issue -> {
			if (StringUtils.hasText(issue.getPath())) {
				fileUploadService.delete(issue.getPath());
			}
		});
		troubleMemoList.forEach(issue -> {
			if (StringUtils.hasText(issue.getPath())) {
				fileUploadService.delete(issue.getPath());
			}
		});
		targetList.forEach(target -> {
			if (StringUtils.hasText(target.getImgPath()) && !FilenameUtils.getBaseName(target.getImgPath())
				.equals("virnect_target")) {
				fileUploadService.delete(target.getImgPath());
			}
		});

		// daily_total_workspace 삭제
		dailyTotalWorkspaceRepository.deleteAllDailyTotalWorkspaceByWorkspaceUUID(workspaceUUID);

		// daily_total 삭제
		dailyTotalWorkspaceList.forEach(
			dailyTotalWorkspace -> dailyTotalRepository.delete(dailyTotalWorkspace.getDailyTotal()));

		// item 삭제
		itemRepository.deleteAllItemByReportList(reportList);

		// report 삭제
		reportRepository.deleteAllReportByJobList(jobList);

		// issue 삭제
		issueRepository.deleteAllIssueByJobList(jobList);

		// 트러블 메모 삭제
		issueRepository.deleteAllIssueByUserUUID(userUUID);

		// job 삭제
		jobRepository.deleteAllJobBySubProcessList(subProcessList);

		// sub process 삭제
		subProcessRepository.deleteAllSubProcessByProcessList(processList);

		// target 삭제
		targetRepository.deleteAllTargetByProcessList(processList);

		// process 삭제
		processRepository.deleteByWorkspaceUUID(workspaceUUID);

		return new TaskSecessionResponse(workspaceUUID, true, LocalDateTime.now());

	}
}