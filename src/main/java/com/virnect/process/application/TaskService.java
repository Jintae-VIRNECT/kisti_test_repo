package com.virnect.process.application;

import com.virnect.process.application.content.ContentRestService;
import com.virnect.process.application.user.UserRestService;
import com.virnect.process.application.workspace.WorkspaceRestService;
import com.virnect.process.dao.*;
import com.virnect.process.dao.process.ProcessRepository;
import com.virnect.process.domain.Process;
import com.virnect.process.domain.*;
import com.virnect.process.dto.request.*;
import com.virnect.process.dto.response.*;
import com.virnect.process.dto.rest.request.content.ContentDeleteRequest;
import com.virnect.process.dto.rest.response.content.*;
import com.virnect.process.dto.rest.response.user.UserInfoListResponse;
import com.virnect.process.dto.rest.response.user.UserInfoResponse;
import com.virnect.process.dto.rest.response.workspace.MemberInfoDTO;
import com.virnect.process.dto.rest.response.workspace.MemberListResponse;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.common.PageMetadataResponse;
import com.virnect.process.global.common.ResponseMessage;
import com.virnect.process.global.error.ErrorCode;
import com.virnect.process.global.util.QRcodeGenerator;
import com.virnect.process.infra.file.FileUploadService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jdbc.Work;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-23
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Process Service Class
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskService {
    private static final LocalDateTime DEFATUL_LOCAL_DATE_TIME = LocalDateTime.parse("1500-01-01T00:00:00");
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
    private final DailyTotalRepository dailyTotalRepository;
    private final DailyTotalWorkspaceRepository dailyTotalWorkspaceRepository;

    private final ContentRestService contentRestService;
    private final UserRestService userRestService;
    private final WorkspaceRestService workspaceRestService;

    private final ModelMapper modelMapper;
    private final FileUploadService fileUploadService;

    // TODO : 작업저장실패시 컨텐츠 서버도 함께 롤백하는 프로세스 필요. 현재는 만약 예외가 발생할 경우 컨텐츠 서버는 파일 및 컨텐츠가 복제된 채로 그대로 남으므로 저장소가 낭비됨..
    @Transactional
    public ApiResponse<ProcessRegisterResponse> createTheProcess(ProcessRegisterRequest registerNewProcess) {
        // 공정 생성 요청 처리
        log.info("CREATE THE PROCESS requestBody ---> {}", registerNewProcess.toString());

        // 1. 컨텐츠 메타데이터 가져오기
        ApiResponse<ContentRestDto> contentApiResponse = this.contentRestService.getContentMetadata(registerNewProcess.getContentUUID());

        // 1-1. 에러가 난 경우
        if (contentApiResponse.getCode() != 200) {
            throw new ProcessServiceException(ErrorCode.ERR_PROCESS_REGISTER);
        }

        if (contentApiResponse.getData() == null) {
            throw new ProcessServiceException(ErrorCode.ERR_PROCESS_REGISTER);
        }

        log.info("CONTENT_METADATA: [{}]", contentApiResponse.getData().getContents().toString());
        log.debug("----------content uuid : {}", contentApiResponse.getData().getContents().getUuid());

        // 4. 공정 정보 저장
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

        // 메뉴얼(컨텐츠)도 보고 작업(보고)도 필요한 경우 = 복제
        if ("duplicate".equals(registerNewProcess.getTargetSetting())) {
            // 컨텐츠 파일 복제 요청
            ApiResponse<ContentUploadResponse> contentDuplicate = this.contentRestService.contentDuplicate(
                    registerNewProcess.getContentUUID()
                    , registerNewProcess.getWorkspaceUUID()
                    , registerNewProcess.getOwnerUUID());
            try {
                log.info("CREATE THE PROCESS - sourceContentUUID : [{}], createContentUUID : [{}]", registerNewProcess.getContentUUID(), contentDuplicate.getData().getContentUUID());

                // 복제된 컨텐츠 식별자 등록
                newProcess.setContentUUID(contentDuplicate.getData().getContentUUID());
                newProcess.setContentManagerUUID(registerNewProcess.getOwnerUUID());

                // 컨텐츠의 전환상태 변경
                this.contentRestService.contentConvertHandler(contentDuplicate.getData().getContentUUID(), YesOrNo.YES);

                // 작업 저장
                this.processRepository.save(newProcess);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("CONTENT UPLOAD ERROR: {}", e.getMessage());
                rollbackDuplicateContent(contentDuplicate.getData().getContentUUID(), registerNewProcess.getOwnerUUID());
                throw new ProcessServiceException(ErrorCode.ERR_PROCESS_REGISTER);
            }

            // 타겟
            addTargetToProcess(newProcess, registerNewProcess.getTargetType());

            ApiResponse<ContentRestDto> duplicatedContent = this.contentRestService.getContentMetadata(contentDuplicate.getData().getContentUUID());

            // 5. 복제된 컨텐츠로 세부 공정 정보 리스트 생성
            log.info("{}", duplicatedContent.getData().getContents().toString());
            ContentRestDto.Content duplicatedMetadata = duplicatedContent.getData().getContents();
            Map<String, ContentRestDto.SceneGroup> sceneGroupMap = new HashMap<>();

            log.debug("Duplicated ConetntMetadata {}", duplicatedMetadata);

            duplicatedMetadata.getSceneGroups().forEach(sceneGroup -> sceneGroupMap.put(sceneGroup.getId(), sceneGroup));

            addSubProcessOnProcess(registerNewProcess, sceneGroupMap, newProcess);
        }
        // 메뉴얼(컨텐츠)은 필요없고 작업(보고)만 필요한 경우.
        else {
            log.info("CREATE THE PROCESS  - transform sourceContentUUID : [{}]", registerNewProcess.getContentUUID());
            ApiResponse<ContentInfoResponse> contentTransform = this.contentRestService.getContentInfo(registerNewProcess.getContentUUID());

            log.debug("line 149 : {}", contentTransform.getData());
            log.debug("line 150 : {}", contentTransform.getData().getTargets());

            ContentInfoResponse contentInfo = contentTransform.getData();

            // 이미 컨텐츠에서 작업으로 변환 된 경우
            if (YesOrNo.YES.equals(contentInfo.getConverted())) {
                throw new ProcessServiceException(ErrorCode.ERR_ALREADY_TRANSFORMED);
            }

            // 컨텐츠의 타겟이 없을 경우
            if (contentInfo.getTargets().isEmpty()) {
                throw new ProcessServiceException(ErrorCode.ERR_NO_CONTENT_TARGET);
            }

            // 컨텐츠의 타겟값을 가져옴
            ContentTargetResponse contentTarget = contentTransform.getData().getTargets().get(0);

            // 기존 컨텐츠 식별자 등록
            newProcess.setContentUUID(registerNewProcess.getContentUUID());
            newProcess.setContentManagerUUID(registerNewProcess.getOwnerUUID());

            // 컨텐츠의 전환상태 변경
            this.contentRestService.contentConvertHandler(contentTransform.getData().getContentUUID(), YesOrNo.YES);

            // 작업 저장
            this.processRepository.save(newProcess);

            // 컨텐츠의 타겟 정보를 가져옴
            getTargetFromContent(newProcess, contentTarget);

            // 5. 기존 컨텐츠로 세부 공정 정보 리스트 생성
            log.info("{}", contentApiResponse.getData().getContents().toString());
            ContentRestDto.Content metadata = contentApiResponse.getData().getContents();
            Map<String, ContentRestDto.SceneGroup> sceneGroupMap = new HashMap<>();

            log.debug("ConetntMetadata {}", metadata);

            metadata.getSceneGroups().forEach(sceneGroup -> sceneGroupMap.put(sceneGroup.getId(), sceneGroup));

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

        // TODO : 공정전환 완료 후 converted yes로 변경해야 함.

        return new ApiResponse<>(processRegisterResponse);
    }

    private void rollbackDuplicateContent(String contentUUID, String userUUID) {
        this.contentRestService.contentConvertHandler(contentUUID, YesOrNo.NO);
        String[] contentUUIDs = {contentUUID};

        ContentDeleteRequest contentDeleteRequest = new ContentDeleteRequest();
        contentDeleteRequest.setContentUUIDs(contentUUIDs);
        contentDeleteRequest.setWorkerUUID(userUUID);

        this.contentRestService.contentDeleteRequestHandler(contentDeleteRequest);
    }

    /**
     * 컨텐츠를 작업으로 복제(Duplicate)할 때 - 작업의 새로운 타겟을 만든다. (매뉴얼 + 작업 보고)
     * @param newProcess
     * @param targetType
     */
    private void addTargetToProcess(Process newProcess, final TargetType targetType) {
        // 타겟데이터
        try {
            String targetData = UUID.randomUUID().toString();

            String imgPath = getImgPath(targetData); //= this.fileUploadService.base64ImageUpload(targetData);

            Target target = Target.builder()
                    .type(targetType)
                    .process(newProcess)
                    .data(targetData)
                    .imgPath(imgPath)
                    .build();

            this.targetRepository.save(target);

            log.debug("TARGET : {}", target.toString());
            log.debug("newProcess : {}", newProcess.toString());
            newProcess.addTarget(target);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ERROR : ADD TARGET ON PROCESS getMessage: {}, getCause: {}", e.getMessage(), e.getCause());
            rollbackDuplicateContent(newProcess.getContentUUID(), newProcess.getContentManagerUUID());
            throw new ProcessServiceException(ErrorCode.ERR_TARGET_REGISTER);
        }
    }

    /**
     * 컨텐츠를 작업으로 변환(Transform)할 때 - 기존 컨텐츠의 타겟을 작업 타겟으로 설정.( 메뉴얼 X, Only 작업 보고)
     * @param newProcess
     * @param contentTargetResponse
     */
    private void getTargetFromContent(Process newProcess, ContentTargetResponse contentTargetResponse) {
        try {
            String targetData = contentTargetResponse.getData();
            TargetType targetType = contentTargetResponse.getType();
            String imgPath = contentTargetResponse.getImgPath(); //this.fileUploadService.base64ImageUpload(targetData);

            Target target = Target.builder()
                    .type(targetType)
                    .process(newProcess)
                    .data(targetData)
                    .imgPath(imgPath)
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
    private void addSubProcessOnProcess(ProcessRegisterRequest registerNewProcess, Map<String, ContentRestDto.SceneGroup> sceneGroupMap, Process newProcess) {
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
            rollbackDuplicateContent(newProcess.getContentUUID(), newProcess.getContentManagerUUID());
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
            rollbackDuplicateContent(newProcess.getContentUUID(), newProcess.getContentManagerUUID());
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
                                    .build();
                            report.addItem(item);
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
            rollbackDuplicateContent(newProcess.getContentUUID(), newProcess.getContentManagerUUID());
            throw new ProcessServiceException(ErrorCode.ERR_REPORT_REGISTER);
        }
    }

    @Transactional
    public ApiResponse<ProcessRegisterResponse> duplicateTheProcess(ProcessDuplicateRequest duplicateRequest) {
        // 공정 생성 요청 처리
        log.info("CREATE THE PROCESS requestBody ---> {}", duplicateRequest.toString());

        // 1. 컨텐츠 메타데이터 가져오기
        ApiResponse<ContentRestDto> contentApiResponse = this.contentRestService.getContentMetadata(duplicateRequest.getContentUUID());

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
                log.info("DUPLICATE THE PROCESS - sourceContentUUID : [{}], createContentUUID : [{}]", duplicateRequest.getContentUUID(), contentDuplicate.getData().getContentUUID());

                // 복제된 컨텐츠 식별자 등록
                newProcess.setContentUUID(contentDuplicate.getData().getContentUUID());
                newProcess.setContentManagerUUID(duplicateRequest.getOwnerUUID());

                // 컨텐츠의 전환상태 변경
                this.contentRestService.contentConvertHandler(contentDuplicate.getData().getContentUUID(), YesOrNo.YES);

                // 작업 저장
                this.processRepository.save(newProcess);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("CONTENT UPLOAD ERROR: {}", e.getMessage());
                rollbackDuplicateContent(contentDuplicate.getData().getContentUUID(), duplicateRequest.getOwnerUUID());
                throw new ProcessServiceException(ErrorCode.ERR_PROCESS_REGISTER);
            }

            // 타겟
            addTargetToProcess(newProcess, duplicateRequest.getTargetType());

            // addSubProcessOnProcess에 들어갈 객체
            ProcessRegisterRequest registerNewProcess = new ProcessRegisterRequest();

            registerNewProcess.setSubTaskList(duplicateRequest.getSubTaskList());

            ApiResponse<ContentRestDto> duplicatedContent = this.contentRestService.getContentMetadata(contentDuplicate.getData().getContentUUID());

            // 5. 복제된 컨텐츠로 세부 공정 정보 리스트 생성
            log.info("{}", duplicatedContent.getData().getContents().toString());
            ContentRestDto.Content duplicatedMetadata = duplicatedContent.getData().getContents();
            Map<String, ContentRestDto.SceneGroup> sceneGroupMap = new HashMap<>();

            log.debug("Duplicated ConetntMetadata {}", duplicatedMetadata);

            duplicatedMetadata.getSceneGroups().forEach(sceneGroup -> sceneGroupMap.put(sceneGroup.getId(), sceneGroup));

            addSubProcessOnProcess(registerNewProcess, sceneGroupMap, newProcess);
        }
        // 메뉴얼(컨텐츠)은 필요없고 작업(보고)만 필요한 경우.
        else {
            log.info("DUPLICATE THE PROCESS  - transform sourceContentUUID : [{}]", duplicateRequest.getContentUUID());
            Process targetProcess = this.processRepository.findById(duplicateRequest.getTaskId())
                    .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
            ApiResponse<ContentInfoResponse> contentTransfrom = this.contentRestService.getContentInfo(duplicateRequest.getContentUUID());


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

            if (!targetProcess.getTargetList().isEmpty()){
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

    public ApiResponse<RecentSubProcessResponse> getNewWork(String workspaceUUID, String workerUUID) {
        // 신규 작업 존재 여부 검사 요청 처리
        boolean hasNewSubProcess = this.subProcessRepository.existsByIsRecentAndWorkerUUIDAndWorkspaceUUID(YesOrNo.YES, workspaceUUID, workerUUID);
        return new ApiResponse<>(new RecentSubProcessResponse(hasNewSubProcess));
    }

    public ApiResponse<ProcessIdRetrieveResponse> getProcessIdOfContent(String contentUUID) {
        // 컨텐츠 식별자로 작업 조회
        List<Process> processList = this.processRepository.findByContentUUID(contentUUID);

        // 반환할 공정 아이디 목록
        List<Long> processIds = new ArrayList<>();
        processList.forEach(process -> {
            // 공정의 상태 필터링
            if (!(process.getState() == State.CLOSED || process.getState() == State.DELETED)) {
                processIds.add(process.getId());
            }
        });

        return new ApiResponse<>(new ProcessIdRetrieveResponse(processIds));
    }

    public ApiResponse<ProcessContentAndTargetResponse> getRelatedInfoOfProcess(Long processId) {
        // 공정의 타겟 데이터와 contentUUID 가져오기
        Process process = this.processRepository.findById(processId).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
        ProcessContentAndTargetResponse processContentAndTargetResponse = ProcessContentAndTargetResponse.builder()
                .targetList(process.getTargetList().stream().map(target -> this.modelMapper.map(target, ProcessTargetResponse.class)).collect(Collectors.toList()))
                .contentUUID(process.getContentUUID())
                .build();

        return new ApiResponse<>(processContentAndTargetResponse);
    }

    public ApiResponse<ProcessMetadataResponse.ProcessesMetadata> getMetadataTheProcess(Long[] processesId, String workerUUID) {
        // 공정의 메타데이터 가져오기
        // 반환할 공정 메타데이터
        List<ProcessMetadataResponse.Process> returnProcessMetadataList = new ArrayList<>();

        for (Long processId : processesId) {
            // 요청한 공정들을 모두 조회
            Process process = this.processRepository.findById(processId)
                    .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS_FOR_PROCESS_METADATA));
//            Process process = this.processRepository.getProcessInfo(processId).orElseGet(() -> null);
            if (Objects.isNull(process)) {
                log.info("getMetadataTheProcess - getProcessInfo is null");
                // 조회되는 공정이 없을 경우 예외처리
                // TODO : 예외로 던질지, 메시지를 남길지 고민되는 부분임. 메시지를 남기는 것이 사용성이 더 좋을 것으로 생각됨. 추후 개선.
                throw new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS_FOR_PROCESS_METADATA);
            } else {
                // 공정을 메타데이터로 전환 후 리스트에 등록
                ProcessMetadataResponse.Process metadataProcess = buildMetadataProcess(process, null, workerUUID);
                log.info(">>> processId {} , metadataProcess - {}", process.getId(), metadataProcess);
                // (세부)공정의 권한이 없어 조회되지 않을 경우 null로 반환되어 리스트에 적재되지 않음.
                if (!Objects.isNull(metadataProcess)) {
                    returnProcessMetadataList.add(metadataProcess);
                }
            }
        }

        return new ApiResponse<>(ProcessMetadataResponse.ProcessesMetadata.builder()
                .tasks(returnProcessMetadataList)
                .build());
    }

    public ApiResponse<ProcessMetadataResponse.ProcessesMetadata> getMetadataTheSubProcess(Long[] subProcessesId, String workerUUID) {
        // 세부공정의 메타데이터 가져오기
        // 공정 메타데이터 가져오기의 파라미터로 들어갈 공정아이디 배열
        List<ProcessMetadataResponse.Process> returnProcessMetadataList = new ArrayList<>();
        List<Long> processIdList = new ArrayList<>();

        for (Long subProcessId : subProcessesId) {
            // 요청한 세부공정들을 모두 조회
            SubProcess subProcess = this.subProcessRepository.findById(subProcessId).orElseGet(() -> SubProcess.builder().build());
            // 조회되는 세부공정이 없을 경우 예외처리
            if (Objects.isNull(subProcess)) {
                log.info("getMetadataTheSubProcess - subProcessInfo is null");
                throw new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SUBPROCESS_FOR_PROCESS_METADATA);
            } else {
                // 공정 조회 후 배열에 add
                Long processId = Optional.of(subProcess).map(SubProcess::getProcess).map(Process::getId).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SUBPROCESS));
                if (!processIdList.contains(processId)) processIdList.add(processId);
            }
        }

        // 중복제거
        HashSet<Long> processHashSet = new HashSet<>(processIdList);
        ArrayList<Long> processArrayList = new ArrayList<>(processHashSet);

        // 공정 메타데이터 가져오기에 보낼 파라미터 생성
        Long[] processesId = new Long[processArrayList.size()];
        processArrayList.toArray(processesId);
        log.debug("processesId : {}", Arrays.toString(processesId));


        for (Long processId : processesId) {
            // 요청한 공정들을 모두 조회
            Process process = this.processRepository.findById(processId)
                    .orElseGet(() -> null);
//            Process process = this.processRepository.getProcessInfo(processId).orElseGet(() -> null);
            if (Objects.isNull(process)) {
                log.info("getMetadataTheProcess - getProcessInfo is null");
                // 조회되는 공정이 없을 경우 예외처리
                throw new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS_FOR_PROCESS_METADATA);
            } else {
                // 공정을 메타데이터로 전환 후 리스트에 등록
                ProcessMetadataResponse.Process metadataProcess = buildMetadataProcess(process, subProcessesId, workerUUID);
                log.info(">>> processId {} , metadataProcess - {}", process.getId(), metadataProcess);
                // (세부)공정의 권한이 없어 조회되지 않을 경우 null로 반환되어 리스트에 적재되지 않음.
                if (!Objects.isNull(metadataProcess)) {
                    returnProcessMetadataList.add(metadataProcess);
                }
            }
        }

        return new ApiResponse<>(ProcessMetadataResponse.ProcessesMetadata.builder()
                .tasks(returnProcessMetadataList)
                .build());
    }

    // CONVERT metadata - PROCESS
    private ProcessMetadataResponse.Process buildMetadataProcess(Process process, Long[] subProcessesId, String workerUUID) {
        List<ProcessMetadataResponse.SubProcess> metaSubProcessList = buildSubProcessList(process.getSubProcessList(), subProcessesId, workerUUID);
        if (metaSubProcessList.isEmpty()) return null;
        else {
            return ProcessMetadataResponse.Process.builder()
                    .targetList(process.getTargetList().stream().map(target -> this.modelMapper.map(target, ProcessTargetResponse.class)).collect(Collectors.toList()))
                    .taskId(process.getId())
                    .taskName(process.getName())
                    .managerUUID(null)
                    .position(process.getPosition())
                    .subTaskTotal(process.getSubProcessList().size())
                    .startDate(process.getStartDate())
                    .endDate(process.getEndDate())
                    .conditions(process.getConditions())
                    .state(process.getState())
                    .progressRate(process.getProgressRate())
                    .subTasks(metaSubProcessList)
                    .build();
        }
    }

    // CONVERT metadata - SUB PROCESS LIST
    private List<ProcessMetadataResponse.SubProcess> buildSubProcessList(List<SubProcess> subProcesses, Long[] subProcessesId, String workerUUID) {
        List<ProcessMetadataResponse.SubProcess> metaSubProcessList = new ArrayList<>();
        ArrayList<Long> longs = null;
        if (subProcessesId != null) longs = new ArrayList<>(Arrays.asList(subProcessesId));
        for (SubProcess subProcess : subProcesses) {
            ProcessMetadataResponse.SubProcess metadataSubProcess = buildMetadataSubProcess(subProcess, workerUUID);
            // 권한으로 인해 세부공정이 없을 수 있으므로 null체크
            if (!Objects.isNull(metadataSubProcess)) {
                metaSubProcessList.add(metadataSubProcess);
                // subProcessesId로 필터링
                if (subProcessesId != null && longs != null) {
                    if (!longs.contains(subProcess.getId())) {
                        metaSubProcessList.remove(metaSubProcessList.size() - 1);
                    }
                }
            }
        }
        return metaSubProcessList;
    }

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
        } else build = null;
        return build;
    }

    // CONVERT metadata - JOB LIST
    private List<ProcessMetadataResponse.Job> buildJobList(List<Job> jobs) {
        List<ProcessMetadataResponse.Job> metaJobList = new ArrayList<>();
        for (Job job : jobs) {
            ProcessMetadataResponse.Job metadataJob = buildMetadataJob(job);
            metaJobList.add(metadataJob);
        }
        return metaJobList;
    }

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

    // CONVERT metadata - REPORT LIST
    private List<ProcessMetadataResponse.Report> buildReportList(List<Report> reports) {
        List<ProcessMetadataResponse.Report> metaReportList = new ArrayList<>();
        for (Report report : reports) {
            ProcessMetadataResponse.Report metadataReport = buildMetadataReport(report);
            metaReportList.add(metadataReport);
        }
        return metaReportList;
    }

    // CONVERT metadata - REPORT
    private ProcessMetadataResponse.Report buildMetadataReport(Report report) {
        return ProcessMetadataResponse.Report.builder()
                .id(report.getId())
                .actions(buildReportItemList(report.getItemList()))
                .build();
    }

    // CONVERT metadata - REPORT ITEM LIST
    private List<ProcessMetadataResponse.ReportItem> buildReportItemList(List<Item> items) {
        List<ProcessMetadataResponse.ReportItem> metaReportItemList = new ArrayList<>();
        for (Item reportItem : items) {
            ProcessMetadataResponse.ReportItem metadataReportItem = buildMetadataReportItem(reportItem);
            metaReportItemList.add(metadataReportItem);
        }
        return metaReportItemList;
    }

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

    public ApiResponse<HourlyReportCountListResponse> getHourlyReports(String targetDate, String status) {
        // 해당일의 상태의 조건으로 리포트 개수를 시간대별로 조회
        List<HourlyReportCountOfaDayResponse> hourlyReportCountOfaDayResponses = this.reportRepository.selectHourlyReportsTemp(targetDate);
        return new ApiResponse<>(new HourlyReportCountListResponse(hourlyReportCountOfaDayResponses));
    }

    public ApiResponse<MonthlyStatisticsResponse> getDailyTotalRateAtMonth(String workspaceUUID, String month) {
        if (Objects.isNull(workspaceUUID)) {
            List<DailyTotal> dailyTotals = this.dailyTotalRepository.getDailyTotalRateAtMonth(month);

            return new ApiResponse<>(MonthlyStatisticsResponse.builder()
                    .dailyTotal(dailyTotals.stream().map(dailyTotal -> {
                        return MonthlyStatisticsResponse.DailyTotalResponse.builder()
                                .id(dailyTotal.getId())
                                // UTC -> KST 로 9시간을 더해서 날짜산정 해야할 것 같은데 결과는 하지 않아도 KST로 주는 것 같음. 자바로 넘어오면서 KST로 바뀌는 것 같음.
                                .onDay(dailyTotal.getUpdatedDate().toLocalDate())
                                .totalRate(dailyTotal.getTotalRate())
                                .totalTasks(dailyTotal.getTotalCountProcesses())
                                .build();
                    }).collect(Collectors.toList()))
                    .build());
        } else {
//            List<DailyTotalWorkspace> dailyTotalWorkspaceList = this.dailyTotalWorkspaceRepository.getDailyTotalRateAtMonthWithWorkspace(workspaceUUID, month);
            List<DailyTotalWorkspace> dailyTotalWorkspaceList = this.dailyTotalRepository.getDailyTotalRateAtMonthWithWorkspace(workspaceUUID, month);

            return new ApiResponse<>(MonthlyStatisticsResponse.builder()
                    .dailyTotal(dailyTotalWorkspaceList.stream().map(dailyTotalWorkspace -> {
                        return MonthlyStatisticsResponse.DailyTotalResponse.builder()
                                .id(dailyTotalWorkspace.getId())
                                // UTC -> KST 로 9시간을 더해서 날짜산정 해야할 것 같은데 결과는 하지 않아도 KST로 주는 것 같음. 자바로 넘어오면서 KST로 바뀌는 것 같음.
                                .onDay(dailyTotalWorkspace.getUpdatedDate().toLocalDate())
                                .totalRate(dailyTotalWorkspace.getTotalRate())
                                .totalTasks(dailyTotalWorkspace.getTotalCountProcesses())
                                .build();
                    }).collect(Collectors.toList()))
                    .build());
        }
    }

//    public ApiResponse<IssuesResponse> getIssuesInSearchUserName(String userUUID, String workspaceUUID, String search, Pageable pageable) {
//        //List<UserInfoResponse> userInfos = getUserInfoSearch(search);
//        List<UserInfoResponse> userInfos = getUserInfo(search, workspaceUUID);
//        List<String> userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
//        // querydsl 에서는 null처리를 자동으로 해주지만 native이기 때문에 null처리 해야만 함.
//        if (userUUIDList.size() == 0) userUUIDList = null;
////        Page<Issue> issuePage = this.issueRepository.getIssuesInSearchUserName(workspaceUUID, userUUIDList, pageable);
//        Page<Issue> issuePage = this.issueRepository.getIssuesIn(userUUID, workspaceUUID, userUUIDList, pageable);
//
//        return getIssuesResponseApiResponse(pageable, issuePage);
//    }

//    public ApiResponse<IssuesResponse> getIssuesOutSearchUserName(String search, String workspaceUUID, Pageable pageable) {
//        List<MemberInfoDTO> memberList = this.workspaceRestService.getSimpleWorkspaceUserList(workspaceUUID).getData().getMemberInfoList();
//
//        List<String> userUUIDList = new ArrayList<>();
//
//        for (MemberInfoDTO t: memberList) {
//            userUUIDList.add(t.getUuid());
//        }
//
//        List<String> filteredList =userUUIDList.stream().filter(s -> s.contains(search)).collect(Collectors.toList());
//
//        Page<Issue> issuePage = this.issueRepository.getIssuesOut(null, filteredList, pageable);
//
//        return getIssuesResponseApiResponse(pageable, issuePage);
//    }

    public ApiResponse<IssuesResponse> getIssuesAllSearchUserName(String workspaceUUID, String search, Pageable pageable) {
        List<UserInfoResponse> userInfos = getUserInfoSearch(search);
        List<String> userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
        // querydsl 에서는 null처리를 자동으로 해주지만 native이기 때문에 null처리 해야만 함.
        if (userUUIDList.size() == 0) userUUIDList = null;
        Page<Issue> issuePage = this.issueRepository.getIssuesAllSearchUserName(workspaceUUID, userUUIDList, pageable);

        return getIssuesResponseApiResponse(pageable, issuePage);
    }

    public ApiResponse<IssuesResponse> getIssuesIn(String userUUID, String workspaceUUID, String search, Pageable pageable) {

        List<String> userUUIDList = new ArrayList<>();

        if (Objects.nonNull(search) && Objects.nonNull(workspaceUUID)) {
            // 검색어로 워크스페이스 내 사용자 닉네임 및 이메일 검색
            List<UserInfoResponse> userInfos = getUserInfo(search, workspaceUUID);
            userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
        }

        Page<Issue> issuePage = this.issueRepository.getIssuesIn(userUUID, workspaceUUID, search, userUUIDList, pageable);

        return getIssuesResponseApiResponse(pageable, issuePage);
    }

    public ApiResponse<IssuesResponse> getIssuesOut(String myUUID, String workspaceUUID, String search, Pageable pageable) {

        List<String> userUUIDList = new ArrayList<>();

        // 검색어와 워크스페이스UUID를 모두 받은 경우
        if (Objects.nonNull(search) && Objects.nonNull(workspaceUUID)) {
            // 검색어로 워크스페이스 내 사용자의 이메일, 닉네임 검색
            List<UserInfoResponse> userInfos = getUserInfo(search, workspaceUUID);
            userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
        }
        // 검색어 없이 워크스페이스UUID만 받은 경우
        else if (Objects.isNull(search) && Objects.nonNull(workspaceUUID)) {
            // 워크스페이스 사용자 정보
            ApiResponse<MemberListResponse> response = this.workspaceRestService.getSimpleWorkspaceUserList(workspaceUUID);
            List<MemberInfoDTO> memberList = response.getData().getMemberInfoList();
            userUUIDList = memberList.stream().map(MemberInfoDTO::getUuid).collect(Collectors.toList());
        }

        log.debug("{}", userUUIDList);

        Page<Issue> issuePage = this.issueRepository.getIssuesOut(myUUID, search, userUUIDList, pageable);

        return getIssuesResponseApiResponse(pageable, issuePage);
    }

    public ApiResponse<IssuesResponse> getIssuesAll(String workspaceUUID, Pageable pageable) {
        Page<Issue> issuePage = this.issueRepository.getIssuesAll(workspaceUUID, pageable);

        return getIssuesResponseApiResponse(pageable, issuePage);
    }

    public ApiResponse<IssuesResponse> getIssuesInSearchProcessTitle(String workspaceUUID, String title, Pageable pageable) {
        Page<Issue> issuePage = this.issueRepository.getIssuesInSearchProcessTitle(null, workspaceUUID, title, pageable);

        return getIssuesResponseApiResponse(pageable, issuePage);
    }

    public ApiResponse<IssuesResponse> getIssuesInSearchSubProcessTitle(String workspaceUUID, String title, Pageable pageable) {
        Page<Issue> issuePage = this.issueRepository.getIssuesInSearchSubProcessTitle(null, workspaceUUID, title, pageable);

        return getIssuesResponseApiResponse(pageable, issuePage);
    }

    public ApiResponse<IssuesResponse> getIssuesInSearchJobTitle(String workspaceUUID, String title, Pageable pageable) {
        Page<Issue> issuePage = this.issueRepository.getIssuesInSearchJobTitle(null, workspaceUUID, title, pageable);

        return getIssuesResponseApiResponse(pageable, issuePage);
    }

    private ApiResponse<IssuesResponse> getIssuesResponseApiResponse(Pageable pageable, Page<Issue> issuePage) {
        List<IssueInfoResponse> issueInfoResponseList = issuePage.stream().map(issue -> {
            Job job = Optional.of(issue).map(Issue::getJob).orElseGet(() -> null);
            SubProcess subProcess = Optional.of(issue).map(Issue::getJob).map(Job::getSubProcess).orElseGet(() -> null);
            Process process = Optional.of(issue).map(Issue::getJob).map(Job::getSubProcess).map(SubProcess::getProcess).orElseGet(() -> null);
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
     * 리포트 목록 조회
     * @param userUUID
     * @param workspaceUUID
     * @param processId
     * @param subProcessId
     * @param search
     * @param reported
     * @param pageable
     * @return
     */
    public ApiResponse<ReportsResponse> getReports(String myUUID, String workspaceUUID, Long processId, Long subProcessId, String search, Boolean reported, Pageable pageable) {

        List<String> userUUIDList = new ArrayList<>();

        if (Objects.nonNull(search) && Objects.nonNull(workspaceUUID)) {
            // 사용자 검색 (이메일 및 닉네임으로 검색)
            List<UserInfoResponse> userInfos = getUserInfo(search, workspaceUUID);
            userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
        }
        Page<Report> reportPage = this.reportRepository.getPages(myUUID, workspaceUUID, processId, subProcessId, search, userUUIDList, reported, pageable);

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

    // 공정 및 세부공정 내용 동기화
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
        List<SubProcess> subProcesses = this.subProcessRepository.findByProcessAndIdIsIn(process, targetSubProcessIdList);

        // 동기화 시간
        LocalDateTime nowTime = LocalDateTime.now();
        // 프로세스 동기화시간 업데이트 여부
        AtomicBoolean updateProcess = new AtomicBoolean(false);

        // 3-2-3. 세부 작업 내용 동기화
        subProcesses.forEach(subProcess -> {
            WorkResultSyncRequest.SubProcessWorkResult subProcessWorkResult = subProcessWorkResultHashMap.get(subProcess.getId());
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

    // 작업 내용 동기화
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

    // 레포트 내용 동기화
    private void syncReportWork(List<WorkResultSyncRequest.ReportWorkResult> reportWorkResults) {
        reportWorkResults.forEach(reportWorkResult -> {
            if (reportWorkResult.getActions() != null) {
                syncReportItemWork(reportWorkResult.getActions());
            }
        });
    }

    // 레포트 작업 아이템 동기화
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
    private void syncIssueWork(List<WorkResultSyncRequest.WorkIssueResult> workIssueResults, String syncUserUUID, Job job) {
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

    // 이슈 동기화
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

    public ResponseMessage getTotalRate(String workspaceUUID) {
        // 전체 공정 진행률 조회
        List<Process> processes = this.processRepository.findByWorkspaceUUID(workspaceUUID);
        return new ResponseMessage().addParam("totalRate", ProgressManager.getAllProcessesTotalProgressRate(processes));
    }

    public ApiResponse<ProcessesStatisticsResponse> getStatistics(String workspaceUUID) {
        // 전체 공정 진행률 및 공정진행상태별 현황 조회
        int totalRate = 0, totalProcesses = 0, categoryWait = 0, categoryStarted = 0, categoryEnded = 0, wait = 0, unprogressing = 0, progressing = 0, completed = 0, incompleted = 0, failed = 0, success = 0, fault = 0;
        List<Process> processes = this.processRepository.findByWorkspaceUUID(workspaceUUID);
        for (Process process : processes) {
            totalRate = totalRate + process.getProgressRate();
            Conditions conditions = process.getConditions();
            if (conditions == Conditions.WAIT) {
                categoryWait++;
                wait++;
            } else if (conditions == Conditions.UNPROGRESSING) {
                categoryStarted++;
                unprogressing++;
            } else if (conditions == Conditions.PROGRESSING) {
                categoryStarted++;
                progressing++;
            } else if (conditions == Conditions.COMPLETED) {
                categoryStarted++;
                completed++;
            } else if (conditions == Conditions.INCOMPLETED) {
                categoryStarted++;
                incompleted++;
            } else if (conditions == Conditions.FAILED) {
                categoryEnded++;
                failed++;
            } else if (conditions == Conditions.SUCCESS) {
                categoryEnded++;
                success++;
            } else if (conditions == Conditions.FAULT) {
                categoryEnded++;
                fault++;
            }
            totalProcesses++;
        }
        totalRate = totalProcesses == 0 ? 0 : totalRate / totalProcesses;


        ProcessesStatisticsResponse processesStatisticsResponse = ProcessesStatisticsResponse.builder()
                .totalRate(totalRate)
                .totalTasks(totalProcesses)
                .categoryWait(categoryWait)
                .categoryStarted(categoryStarted)
                .categoryEnded(categoryEnded)
                .wait(wait)
                .unprogressing(unprogressing)
                .progressing(progressing)
                .completed(completed)
                .incompleted(incompleted)
                .failed(failed)
                .success(success)
                .fault(fault)
                .build();
        return new ApiResponse<>(processesStatisticsResponse);
    }

    /**
     * 전체 작업 목록 조회
     * @param workspaceUUID 워크스페이스 UUID
     * @param search        검색어
     * @param userUUID      사용자 UUID
     * @param filter        작업 컨디션
     * @param pageable      페이징
     * @return
     */
    public ApiResponse<ProcessListResponse> getProcessList(String myUUID, String workspaceUUID, String search, List<Conditions> filter, Pageable pageable) {
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
            List<Process> processList = this.processRepository.findByWorkspaceUUID(workspaceUUID);

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
                processPage = this.processRepository.getMyTask(myUUID, workspaceUUID, search, pageable);
            } else {
                processPage = this.processRepository.getProcessPageSearchUser(workspaceUUID, search, userUUIDList, pageable);
            }

            if (filter != null && filter.size() > 0 && !filter.contains(Conditions.ALL)) {
                processPage = filterConditionsProcessPage(processPage, filter, pageable);
            }
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
     * @param processList
     * @param filter
     * @param pageable
     * @return
     */
    private Page<Process> filterConditionsProcessPage(Page<Process> processList, List<Conditions> filter, Pageable pageable) {
        List<Process> processes = new ArrayList<>();

        for (Process process : processList) {
            // 상태가 일치하는 공정만 필터링
            if (filter.contains(process.getConditions())) {
                processes.add(process);
            }
        }
        return new PageImpl<>(processes, pageable, processes.size());
    }

    private ApiResponse<ProcessListResponse> getProcessesPageResponseApiResponse(Pageable pageable, Page<Process> processPage) {

        List<ProcessInfoResponse> processInfoResponseList = processPage.stream().map(process -> {
            ProcessInfoResponse processInfoResponse = modelMapper.map(process, ProcessInfoResponse.class);
            processInfoResponse.setSubTaskAssign(this.getSubProcessesAssign(process));
            processInfoResponse.setDoneCount((int) process.getSubProcessList().stream().filter(subProcess -> subProcess.getConditions() == Conditions.COMPLETED || subProcess.getConditions() == Conditions.SUCCESS).count());
            processInfoResponse.setIssuesTotal(this.processRepository.getCountIssuesInProcess(process.getId()));
            processInfoResponse.setSubTaskTotal(process.getSubProcessList().size());

            List<ProcessTargetResponse> targetList = process.getTargetList().stream().map(target -> {
                ProcessTargetResponse targetResponse = ProcessTargetResponse.builder()
                        .id(target.getId())
                        .type(target.getType())
                        .data(target.getData())
                        .imgPath(target.getImgPath())
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

        if (!request.getActorUUID().equals(process.getContentManagerUUID())) {
            throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);
        }

        // 마감 상태로 변경
        process.setState(State.CLOSED);
        this.processRepository.save(process);

        // TODO : 다른 서비스를 호출하는 것이 옳은 것인지 확인이 필요. redirect를 해야하나?
        // 공정상세조회하여 반환
        return this.getProcessInfo(process.getId());
    }

    /**
     * 작업 상세 정보
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
        processInfoResponse.setDoneCount((int) process.getSubProcessList().stream().filter(subProcess -> subProcess.getConditions() == Conditions.COMPLETED || subProcess.getConditions() == Conditions.SUCCESS).count());
        processInfoResponse.setIssuesTotal(this.processRepository.getCountIssuesInProcess(process.getId()));
        processInfoResponse.setSubTaskAssign(this.getSubProcessesAssign(process));
        processInfoResponse.setTargets(targetResponseList);
        return new ApiResponse<>(processInfoResponse);
    }

    /**
     * 작업 및 하위의 모든 (세부작업, 단계 등) 정보를 업데이트.
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

            if (!updateSourceProcess.getContentManagerUUID().equals(editProcessRequest.getActorUUID()))
                throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);

            // 공정진행중여부확인 - 편집할 수 없는 상태라면 에러
            if (updateSourceProcess.getState() == State.CLOSED || updateSourceProcess.getState() == State.DELETED) {
                throw new ProcessServiceException(ErrorCode.ERR_PROCESS_UPDATED);
            }

            // 2. 공정 상세정보 편집
            updateSourceProcess.setStartDate(Optional.of(editProcessRequest).map(EditProcessRequest::getStartDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")));
            updateSourceProcess.setEndDate(Optional.of(editProcessRequest).map(EditProcessRequest::getEndDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")));
            updateSourceProcess.setPosition(Optional.of(editProcessRequest).map(EditProcessRequest::getPosition).orElseGet(() -> ""));
            updateSourceProcess = this.processRepository.save(updateSourceProcess);

            // 2-1. 세부공정 상세정보 편집
            editProcessRequest.getSubTaskList().forEach(editSubProcessRequest -> {
                if (!(boolean) updateSubProcess(editSubProcessRequest.getSubTaskId(), editSubProcessRequest).getData().get("result"))
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
     * 작업 내 하위작업 목록
     * @param processId
     * @param workspaceUUID
     * @param search
     * @param userUUID
     * @param filter
     * @param pageable
     * @return
     */
    public ApiResponse<SubProcessListResponse> getSubProcessList(Long processId, String workspaceUUID, String search, String userUUID, List<Conditions> filter, Pageable pageable) {
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
        subProcessPage = this.subProcessRepository.getSubProcessPage(workspaceUUID, processId, search, userUUIDList, pageable);

        if (filter != null && filter.size() > 0 && !filter.contains(Conditions.ALL)) {
            subProcessPage = filterConditionsSubProcessPage(subProcessPage, filter, pageable);
        }

        List<EditSubProcessResponse> editSubProcessResponseList = subProcessPage.stream().map(subProcess -> {
            ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(subProcess.getWorkerUUID());
            return EditSubProcessResponse.builder()
                    .subTaskId(subProcess.getId())
                    .priority(subProcess.getPriority())
                    .subTaskName(subProcess.getName())
                    .stepTotal(subProcess.getJobList().size())
                    .conditions(Optional.of(subProcess).map(SubProcess::getConditions).orElseGet(() -> Conditions.WAIT))
                    .startDate(Optional.of(subProcess).map(SubProcess::getStartDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
                    .endDate(Optional.of(subProcess).map(SubProcess::getEndDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
                    .progressRate(Optional.of(subProcess).map(SubProcess::getProgressRate).orElseGet(() -> 0))
                    .reportedDate(Optional.of(subProcess).map(SubProcess::getReportedDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
                    .isRecent(Optional.of(subProcess).map(SubProcess::getIsRecent).orElseGet(() -> YesOrNo.NO))
                    .workerUUID(userInfoResponse.getData().getUuid())
                    .workerName(userInfoResponse.getData().getNickname())
                    .workerProfile(userInfoResponse.getData().getProfile())
                    .issuesTotal(this.issueRepository.countIssuesInSubProcess(subProcess.getId()))
                    .doneCount((int) subProcess.getJobList().stream().filter(job -> job.getConditions() == Conditions.COMPLETED || job.getConditions() == Conditions.SUCCESS).count())
                    .build();
        }).collect(Collectors.toList());

        PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
                .currentPage(pageable.getPageNumber())
                .currentSize(pageable.getPageSize())
                .totalPage(subProcessPage.getTotalPages())
                .totalElements(subProcessPage.getTotalElements())
                .build();
        return new ApiResponse<>(new SubProcessListResponse(processId, processName, processState, contentUUID, editSubProcessResponseList, pageMetadataResponse));
    }

    /**
     * 하위 작업의 컨디션을 필터링
     * @param subProcessList
     * @param filter
     * @param pageable
     * @return
     */
    private Page<SubProcess> filterConditionsSubProcessPage(Page<SubProcess> subProcessList, List<Conditions> filter, Pageable pageable) {
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
     * @param workspaceUUID
     * @param processId
     * @param search
     * @param pageable
     * @return
     */
    public ApiResponse<SubProcessesResponse> getSubProcesses(String workspaceUUID, Long processId, String search, Pageable pageable) {
        // 워크스페이스 전체의 세부공정목록조회
        // 검색어로 사용자 목록 조회
        //List<UserInfoResponse> userInfos = getUserInfoSearch(search);
        List<String> userUUIDList = new ArrayList<>();

        if (Objects.nonNull(search) && Objects.nonNull(workspaceUUID)) {
            List<UserInfoResponse> userInfos = getUserInfo(search, workspaceUUID);
            userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
        }
        Page<SubProcess> subProcessPage = this.subProcessRepository.getSubProcessPage(workspaceUUID, processId, search, userUUIDList, pageable);
        List<SubProcessReportedResponse> editSubProcessResponseList = subProcessPage.stream().map(subProcess -> {
            ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(subProcess.getWorkerUUID());
            return SubProcessReportedResponse.builder()
                    .taskId(subProcess.getProcess().getId())
                    .taskName(subProcess.getProcess().getName())
                    .subTaskId(subProcess.getId())
                    .subTaskName(subProcess.getName())
                    .conditions(subProcess.getConditions())
                    .reportedDate(Optional.of(subProcess).map(SubProcess::getReportedDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
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
     * @param subProcessId
     * @return
     */
    public ApiResponse<SubProcessInfoResponse> getSubProcess(Long subProcessId) {
        // 하위 작업 단건 조회
        SubProcess subProcess = this.subProcessRepository.findById(subProcessId)
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER));

        ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(subProcess.getWorkerUUID());

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
                .doneCount((int) subProcess.getJobList().stream().filter(job -> job.getConditions() == Conditions.COMPLETED || job.getConditions() == Conditions.SUCCESS).count())
                .build());
    }

    /**
     * 내 작업 조회
     * @param workspaceUUID
     * @param workerUUID
     * @param processId
     * @param search
     * @param pageable
     * @return
     * @Description 내 작업을 조회. 종료된 작업은 제외한다.
     */
    @Transactional
    public ApiResponse<MyWorkListResponse> getMyWorks(String workspaceUUID, String workerUUID, Long processId, String search, Pageable pageable) {
        Page<SubProcess> subProcessPage = this.subProcessRepository.getMyWorksInProcess(workspaceUUID, workerUUID, processId, search, pageable);
//        Page<SubProcess> subProcessPage = this.subProcessRepository.findByWorkerUUID(workerUUID, pageable);
        
        List<MyWorksResponse> myWorksResponseList = subProcessPage.stream().map(subProcess -> {
            // 신규작업확인 처리
            if (workerUUID.equals(subProcess.getWorkerUUID())) {
                if (subProcess.getIsRecent() == YesOrNo.YES) {
                    subProcess.setIsRecent(YesOrNo.NO);
                    this.subProcessRepository.save(subProcess);
                }
            }

            Process process = Optional.of(subProcess).map(SubProcess::getProcess).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SUBPROCESS));
            ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(subProcess.getWorkerUUID());
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
                    .reportedDate(Optional.of(subProcess).map(SubProcess::getReportedDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
                    .doneCount((int) subProcess.getJobList().stream().filter(job -> job.getConditions() == Conditions.COMPLETED || job.getConditions() == Conditions.SUCCESS).count())
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
     * @param workspaceUUID
     * @param targetData
     * @param pageable
     * @return
     */
    public ApiResponse<SubProcessesOfTargetResponse> getSubProcessesOfTarget(String workspaceUUID, String targetData, Pageable pageable) {
        Process process = this.processRepository.getProcessUnClosed(workspaceUUID, targetData).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS_OF_TARGET));
//        Page<SubProcess> subProcessPage = this.subProcessRepository.selectSubProcesses(null, process.getId(), null, null, pageable);
        Page<SubProcess> subProcessPage = this.subProcessRepository.getSubProcessPage(null, process.getId(), null, null, pageable);
        SubProcessesOfTargetResponse subProcessesOfTargetResponse = SubProcessesOfTargetResponse.builder()
                .taskId(process.getId())
                .taskName(process.getName())
                .contentUUID(process.getContentUUID())
                .downloadPath(this.contentRestService.getContentInfo(process.getContentUUID()).getData().getPath())
                .subTasks(subProcessPage.getContent().stream().map(subProcess -> {
                    ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(subProcess.getWorkerUUID());
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
        if (subProcess.getWorkerUUID() == null || !subProcess.getWorkerUUID().equals(subProcessRequest.getWorkerUUID())) {
            subProcess.setIsRecent(YesOrNo.YES);
        }
        subProcess.setWorkerUUID(Optional.of(subProcessRequest).map(EditSubProcessRequest::getWorkerUUID).orElseGet(() -> ""));
        // 공정의 날짜 범위 체크는 클라이언트에서 함.
        subProcess.setStartDate(Optional.of(subProcessRequest).map(EditSubProcessRequest::getStartDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")));
        subProcess.setEndDate(Optional.of(subProcessRequest).map(EditSubProcessRequest::getEndDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")));
        this.subProcessRepository.save(subProcess);
        return new ResponseMessage().addParam("result", true);
    }

    // TO DO
//    public ApiResponse<JobReportedResponse> getJobsAll(String workspaceUUID, String search, List<Conditions> filter, Pageable pageable) {
//        // 검색어로 사용자 목록 조회
//        List<UserInfoResponse> userInfos = getUserInfoSearch(search);
//        List<String> userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
//
//        // 전체 단계 조회
//        List<JobAll> jobList = this.jobRepository.getJobAll(workspaceUUID, search, userUUIDList, pageable);
//
//        // 세부 공정 조회 (condition조회를 위해 job 객체 필요)
//        List<Job> job = this.jobRepository.findAll();
//
//        // job의 condition 조회
//        List<Map<Long, Conditions>> jobCond = filterConditionsAllJob(job);
//
//        log.debug("jobCond : {}", jobCond);
//
//        job.forEach(j -> {
//            log.debug("{}", j.getId());
//            log.debug("{}", j.getConditions());
//            log.debug("{}", j.getProgressRate());
//        });
//
//        // job객체로 계산된 jobCondition을 jobId를 기준으로 추가
//        jobCond.forEach(c -> {
//            jobList.forEach(l -> {
//                if (c.get(l.getJobId()) != null) {
//                    l.setJobCondition(c.get(l.getJobId()));
//                }
//            });
//        });
//
//
//        List<JobAll> temp = new ArrayList<>();
//
//        if (filter != null && filter.size() > 0) {
//            for (JobAll j : jobList) {
//                if (filter.contains(j.getJobCondition())) {
//                    temp.add(j);
//                }
//            }
//        } else {
//            temp = jobList;
//        }
////
////        temp.forEach( p ->{
////            try {
////                log.info(objectMapper.writeValueAsString(p));
////            } catch (JsonProcessingException e) {
////                e.printStackTrace();
////            }
////        });
//        return null;
//    }
//
//
//    private List<Map<Long, Conditions>> filterConditionsAllJob(List<Job> jobList) {
//        List<Map<Long, Conditions>> jobs = new ArrayList<>();
//        for (Job job : jobList) {
//            Map<Long, Conditions> jobCond = new HashMap<>();
//            jobCond.put(job.getId(), job.getConditions());
//            jobs.add(jobCond);
//        }
//        return jobs;
//    }

    /**
     * 단계 목록 조회
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


            // report, issue는 job 하위에 1개씩만 생성하는 것으로 메이크와 협의됨.
            if (job.getReportList().size() > 0) {
                JobResponse.Paper paper = JobResponse.Paper.builder()
                        .id(job.getReportList().get(0).getId())
                        .build();
                jobResponse.setPaper(paper);
            }
            List<JobResponse.Issue> jobIssueList = new ArrayList<>();

            if (job.getIssueList().size() > 0) {

                for (Issue issue : job.getIssueList()){
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

    /**
     * 이슈 상세 조회
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
            ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(issue.getWorkerUUID());
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
            SubProcess subProcess = Optional.ofNullable(job).map(Job::getSubProcess).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SUBPROCESS));
            Process process = Optional.ofNullable(subProcess).map(SubProcess::getProcess).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
            ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(subProcess.getWorkerUUID());
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
     * 페이지 상세 조회
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
     * 작업 삭제
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

        // 권한 체크
        if (!actorUUID.equals(process.getContentManagerUUID())) {
            throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);
        }

        // 삭제 조건 중 컨텐츠의 작업 전환상태를 NO로 만들어야 삭제조건에 부합하므로 미리 조건처리함.
        this.contentRestService.contentConvertHandler(process.getContentUUID(), YesOrNo.NO);

        // 컨텐츠 삭제
        String[] processes = {process.getContentUUID()};

        ContentDeleteRequest contentDeleteRequest = new ContentDeleteRequest();

        contentDeleteRequest.setContentUUIDs(processes);
        contentDeleteRequest.setWorkerUUID(actorUUID);
        ApiResponse<ContentDeleteListResponse> apiResponse = this.contentRestService.contentDeleteRequestHandler(contentDeleteRequest);

        log.debug("apiResponse : {}", apiResponse.getData().toString());

        // TODO : 컨텐츠 삭제 실패시 롤백 구현 안됨


        // TODO : 공정 삭제시 히스토리를 남기고 상태값만 바꾼다면, 이슈, 리포트 등 작업 하위의 아이템들을 어떻게 할 것인지 확인해야 함.
        this.processRepository.delete(process);

        return new ApiResponse<>(new ProcessSimpleResponse(apiResponse.getData().getDeleteResponseList().get(0).getResult()));
    }

    /**
     * 사용자 검색 (이름, 이메일)
     * @param search
     * @return
     */
    private List<UserInfoResponse> getUserInfoSearch(String search) {
        ApiResponse<UserInfoListResponse> userInfoListResult = this.userRestService.getUserInfoSearch(search, false);
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
     * 워크스페이스 내 사용자 검색(닉네임, 이메일)
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
            ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(subProcess.getWorkerUUID());
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

    // 일자별 총계
    @Transactional
//    @Scheduled(cron = "55 59 23 * * *", zone = "GMT+9:00")
    // UTC 시간으로 스케쥴링함. 스케줄에 zone 설정시 정상적으로 KST로 스케줄이 돌지만 저장되는 날짜와 시간은 UTC로 저장되어 DB를 긁어서 뷰에 전달하게되면 UTC를 반환함.
    // 때문에 UTC로 스케줄을 돌리고 저장되는 일시도 UTC로 저장됨. 그러므로 '일자별 통계' API에서 일시 반환시 UTC -> KST로 서버에서 변환하여 보내기로 함.
    @Scheduled(cron = "55 59 14 * * *")
    public void saveDailyTotal() {
        // 공정목록 조회
        List<Process> processes = this.processRepository.findByWorkspaceUUID(null);

        int totalRate = 0, totalProcesses = 0;
        List<String> workspaces = new ArrayList<>();
        List<Integer> rates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        HashMap<String, Integer> workspaceRate = new HashMap<>();
        HashMap<String, Integer> workspaceCount = new HashMap<>();
        for (Process process : processes) {
            int progressRate = process.getProgressRate();
            totalRate = totalRate + progressRate;
            totalProcesses++;

            if (workspaces.contains(process.getWorkspaceUUID())) {
                int index = workspaces.indexOf(process.getWorkspaceUUID());
                rates.set(index, rates.get(index) + progressRate);
                counts.set(index, counts.get(index) + 1);
            } else {
                workspaces.add(process.getWorkspaceUUID());
                rates.add(progressRate);
                counts.add(1);
            }
        }

        // 공정률 총계
        totalRate = totalProcesses == 0 ? 0 : totalRate / totalProcesses;

        // 일자별 총계 엔티티생성
        DailyTotal dailyTotal = DailyTotal.builder()
                .totalRate(totalRate)
                .totalCountProcesses(totalProcesses)
                .build();

        this.dailyTotalRepository.save(dailyTotal);

        // 워크스페이스별 총계
        DailyTotalWorkspace dailyTotalWorkspace = null;
        int i = 0;
        while (i < workspaces.size()) {
            dailyTotalWorkspace = dailyTotalWorkspace.builder()
                    .workspaceUUID(workspaces.get(i))
                    .totalRate(rates.get(i))
                    .totalCountProcesses(counts.get(i))
                    .build();

            log.debug("IDX : [{}], dailyTotalWorkspace : [{}]", i, dailyTotalWorkspace.toString());
            this.dailyTotalWorkspaceRepository.save(dailyTotalWorkspace);

            dailyTotal.addDailyTotalWorkspace(dailyTotalWorkspace);
            i++;
        }
    }

    public ApiResponse<ProcessInfoResponse> getProcessInfoByTarget(String targetData) {
        Process process = this.processRepository.findByTargetDataAndState(targetData, State.CREATED);

        if (Objects.isNull(process)) {
            throw new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS);
        }

        ProcessInfoResponse processInfoResponse = this.modelMapper.map(process, ProcessInfoResponse.class);
        return new ApiResponse<>(processInfoResponse);
    }

    /**
     * return file path which uploaded
     *
     * @param file - upload file
     * @return - file path
     */
    private String getFileUploadUrl(MultipartFile file) {
        String url = "NONE";
        if (file != null && file.getSize() > 0) {
            try {
                url = this.fileUploadService.upload(file);
            } catch (Exception e) {
                log.error("file upload error: {}", e.getCause().getMessage());
            }
        }
        return url;
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

    public ApiResponse<CountSubProcessOnWorkerResponse> getCountSubProcessOnWorker(String workspaceUUID, String workerUUID) {
        List<SubProcess> subProcesses = this.subProcessRepository.findByWorkerUUID(workerUUID);
        int ing = 0;
        for (SubProcess subProcess : subProcesses) {
            Process process = subProcess.getProcess();
            State state = process.getState();
            String processWorkspaceUUID = process.getWorkspaceUUID();
            // 공정상태가 종료 또는 삭제가 아니고 그리고 세부공정상태가 대기상태가 아닐 때, 마지막으로 워크스페이스가 동일한 프로세스에 대해 필터
            if ((state != State.CLOSED || state != State.DELETED) && subProcess.getConditions() != Conditions.WAIT && (workspaceUUID == null || processWorkspaceUUID.equals(workspaceUUID)))
                ing++;
        }
        ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(workerUUID);
        CountSubProcessOnWorkerResponse response = CountSubProcessOnWorkerResponse.builder()
                .workerUUID(workerUUID)
                .workerName(userInfoResponse.getData().getNickname())
                .workerProfile(userInfoResponse.getData().getProfile())
                .countAssigned(subProcesses.size())
                .countProgressing(ing)
                .build();
        return new ApiResponse<>(response);
    }

    @Transactional
    public ApiResponse<ProcessInfoResponse> convertProcess(Long processId, String workerUUID, State state) {
        Process process = this.processRepository.findById(processId).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
        if (!process.getContentManagerUUID().equals(workerUUID))
            throw new ProcessServiceException(ErrorCode.ERR_OWNERSHIP);

        process.setState(state);

        // 컨텐츠를 작업으로 전환시 컨텐츠의 converted가 YES가 되어 있으므로 NO로 변경하여 작업으로 전환되지 않았암을 저장 요청함.
        //ApiResponse<ContentInfoResponse> contentInfoResponseApiResponse =
        this.contentRestService.contentConvertHandler(process.getContentUUID(), YesOrNo.NO);

        // 작업을 컨텐츠로 전환
        //ApiResponse<ContentUploadResponse> contentUploadResponseApiResponse =
        this.contentRestService.taskToContentConvertHandler(processId, workerUUID);

        ProcessInfoResponse processInfoResponse = modelMapper.map(process, ProcessInfoResponse.class);

        return new ApiResponse<>(processInfoResponse);
    }

    /**
     * 이미지 업로드 후 업로드 경로 반환
     * @param targetData
     * @return
     */
    private String getImgPath(String targetData) {

        String qrString = "";

        try{
            BufferedImage qrImage = QRcodeGenerator.generateQRCodeImage(targetData, 240, 240);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            ImageIO.write(qrImage, "png", os);
            os.toByteArray();

            qrString = Base64.getEncoder().encodeToString(os.toByteArray());

        } catch (Exception e){
            e.printStackTrace();
        }

        String imgPath = this.fileUploadService.base64ImageUpload(qrString);

        return imgPath;
    }

    /**
     * 워크스페이스 멤버 정보
     * @param workspaceUUID
     * @return
     */
    public ApiResponse<WorkspaceUserListResponse> getWorkspaceUserInfo(String workspaceUUID, Pageable pageable) {
        log.debug(workspaceUUID);
        List<MemberInfoDTO> memberList = this.workspaceRestService.getSimpleWorkspaceUserList(workspaceUUID).getData().getMemberInfoList();

        List<String> userUUIDList = new ArrayList<>();

        for (MemberInfoDTO t : memberList) {
            userUUIDList.add(t.getUuid());
        }

        List<WorkspaceUserInfoResponse> resultList = new ArrayList<>();

        for (MemberInfoDTO memberInfo : memberList) {

            String userUUID = memberInfo.getUuid();

            List<ContentCountResponse> countList = this.contentRestService.countContents(workspaceUUID, userUUIDList).getData();
            List<SubProcess> subProcessList = this.subProcessRepository.getSubProcessList(workspaceUUID, userUUID);
            LocalDateTime lastestTime = this.subProcessRepository.getLastestReportedTime(workspaceUUID, userUUID);

            int ing = 0;
            for (SubProcess subProcess : subProcessList) {
                Process process = subProcess.getProcess();
                State state = process.getState();
                String processWorkspaceUUID = process.getWorkspaceUUID();
                // 공정상태가 종료 또는 삭제가 아니고 그리고 세부공정상태가 대기상태가 아닐 때, 마지막으로 워크스페이스가 동일한 프로세스에 대해 필터
                if ((state != State.CLOSED || state != State.DELETED) && subProcess.getConditions() != Conditions.WAIT && (workspaceUUID == null || processWorkspaceUUID.equals(workspaceUUID))) {
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

            if (subProcessList.size() > 0) {
                percent = (int)(((double) ing / (double) subProcessList.size()) * 100);
            }

            WorkspaceUserInfoResponse response = WorkspaceUserInfoResponse.builder()
                    .workerUUID(memberInfo.getUuid())
                    .workerName(memberInfo.getNickName())
                    .workerProfile(memberInfo.getProfile())
                    .countAssigned(subProcessList.size())
                    .countProgressing(ing)
                    .percent(percent)
                    .countContent(countContent)
                    .lastestReportedTime(lastestTime)
                    .build();

            resultList.add(response);
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
     * 컨텐츠UUID로 컨텐츠 다운로드
     * @param contentUUID
     * @param memberUUID
     * @return
     */
    public ResponseEntity<byte[]> contentDownloadForUUIDHandler(final String contentUUID, final String memberUUID) {

        return contentRestService.contentDownloadForUUIDRequestHandler(contentUUID, memberUUID);
    }

    /**
     * 타겟 데이터로 컨텐츠 다운로드
     * @param targetData
     * @param memberUUID
     * @return
     */
    public ResponseEntity<byte[]> contentDownloadForTargetHandler(final String targetData, final String memberUUID) {

        Process process = Optional.ofNullable(this.processRepository.findByTargetDataAndState(checkParameterEncoded(targetData), State.CREATED))
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_TARGET));

        return contentRestService.contentDownloadForUUIDRequestHandler(process.getContentUUID(), memberUUID);
    }

    /**
     * get방식에서 URLEncode된 값을 pathVariable로 받을 때 URLEncoding이 풀려서 오는 케이스를 체크.
     * @param targetData
     * @return
     */
    public String checkParameterEncoded(String targetData) {
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

    /**
     * sync시 필요한 데이터와 동일한 형태의 데이터를 만들기
     * @param taskId
     * @param subTaskIds
     * @return
     */
    public ApiResponse<WorkSyncResponse> getSyncMeta(Long taskId, Long[] subTaskIds) {

        Process process = this.processRepository.findById(taskId).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));

        WorkSyncResponse workSyncResponse = new WorkSyncResponse();

        WorkSyncResponse.ProcessResult processResult = buildSyncProcess(process, subTaskIds);

        List<WorkSyncResponse.ProcessResult> resultList = new ArrayList<>();

        resultList.add(processResult);

        workSyncResponse.setTasks(resultList);

        return new ApiResponse<>(workSyncResponse);
    }

    // CONVERT syncdata - PROCESS LIST
    private WorkSyncResponse.ProcessResult buildSyncProcess(Process process, Long[] subTaskIds) {

        List<WorkSyncResponse.SubProcessWorkResult> syncSubProcessList = buildSyncSubProcess(process.getSubProcessList(), subTaskIds);

        if (syncSubProcessList.isEmpty()) return null;
        else {
            return WorkSyncResponse.ProcessResult.builder()
                    .id(process.getId())
                    .subTasks(syncSubProcessList)
                    .build();
        }
    }

    // CONVERT syncdata - SUB PROCESS LIST
    private List<WorkSyncResponse.SubProcessWorkResult> buildSyncSubProcess(List<SubProcess> subProcesses, Long[] subTaskIds) {
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

    // CONVERT syncdata - JOB LIST
    private List<WorkSyncResponse.JobWorkResult> buildSyncJobList(List<Job> jobs) {
        List<WorkSyncResponse.JobWorkResult> syncJobList = new ArrayList<>();
        for (Job job : jobs) {
            WorkSyncResponse.JobWorkResult syncDataJob = buildSyncDataJob(job);
            syncJobList.add(syncDataJob);
        }
        return syncJobList;
    }

    // CONVERT syncdata - JOB
    private WorkSyncResponse.JobWorkResult buildSyncDataJob(Job job) {
        return WorkSyncResponse.JobWorkResult.builder()
                .id(job.getId())
                .isReported(job.getIsReported())
                .reports(buildSyncReportList(job.getReportList()))
                .result(job.getResult())
                .build();
    }

    // CONVERT syncdata - REPORT LIST
    private List<WorkSyncResponse.ReportWorkResult> buildSyncReportList(List<Report> reports) {
        List<WorkSyncResponse.ReportWorkResult> syncReportList = new ArrayList<>();
        for (Report report : reports) {
            WorkSyncResponse.ReportWorkResult syncDataReport = buildSyncDataReport(report);
            syncReportList.add(syncDataReport);
        }
        return syncReportList;
    }

    // CONVERT syncdata - REPORT
    private WorkSyncResponse.ReportWorkResult buildSyncDataReport(Report report) {
        return WorkSyncResponse.ReportWorkResult.builder()
                .id(report.getId())
                .actions(buildSyncReportItemList(report.getItemList()))
                .build();
    }

    // CONVERT syncdata - REPORT ITEM LIST
    private List<WorkSyncResponse.ReportItemWorkResult> buildSyncReportItemList(List<Item> items) {
        List<WorkSyncResponse.ReportItemWorkResult> syncReportItemList = new ArrayList<>();

        for (Item reportItem : items) {
            WorkSyncResponse.ReportItemWorkResult syncDataReportItem = buildSyncDataReportItem(reportItem);
            syncReportItemList.add(syncDataReportItem);
        }
        return syncReportItemList;
    }

    // CONVERT syncdata - REPORT ITEM
    private WorkSyncResponse.ReportItemWorkResult buildSyncDataReportItem(Item item) {

        return WorkSyncResponse.ReportItemWorkResult.builder()
                .id(item.getId())
                .answer(item.getAnswer())
                .photoFile(item.getPath())
                .result(item.getResult())
                .build();
    }

    /**
     * 트러블 메모 업로드
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

    public void temp(String search, String workspaceId) {

        getUserInfo(search, workspaceId);

//        List<HourlyReportCountOfaDayResponse> resultList = this.reportRepository.selectHourlyReportsTemp("2020-05-19");
    }
}