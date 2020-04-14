package com.virnect.process.application;

import com.virnect.process.application.content.ContentRestService;
import com.virnect.process.application.user.UserRestService;
import com.virnect.process.dao.*;
import com.virnect.process.dao.aruco.ArucoRepository;
import com.virnect.process.dao.process.ProcessRepository;
import com.virnect.process.domain.Process;
import com.virnect.process.domain.*;
import com.virnect.process.dto.request.*;
import com.virnect.process.dto.response.*;
import com.virnect.process.dto.rest.request.content.ContentStatusChangeRequest;
import com.virnect.process.dto.rest.response.content.ContentRestDto;
import com.virnect.process.dto.rest.response.user.UserInfoListResponse;
import com.virnect.process.dto.rest.response.user.UserInfoResponse;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.common.PageMetadataResponse;
import com.virnect.process.global.common.ResponseMessage;
import com.virnect.process.global.error.ErrorCode;
import com.virnect.process.infra.file.FileUploadService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Project: PF-SMIC_CUSTOM
 * DATE: 2020-01-23
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Process Service Class
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcessService {
    private static final String CONTENT_DOWNLOAD_CONTEXT_PATH = "/contents/upload/";
    private static final String CONTENT_DOWNLOAD_CONTEXT_FILE_EXTENSION = ".Ares";
    private static final LocalDateTime DEFATUL_LOCAL_DATE_TIME = LocalDateTime.parse("1500-01-01T00:00:00");
    private static final Conditions INIT_CONDITIONS = Conditions.WAIT;
    private static final Integer INIT_PROGRESS_RATE = 0;
    private static final State INIT_STATE = State.CREATED;
    private static final YesOrNo INIT_IS_RECENT = YesOrNo.NO;
    private static final YesOrNo INIT_IS_REPORTED = YesOrNo.NO;
    private static final Result INIT_RESULT = Result.NOK;

    private final ArucoRepository arucoRepository;
    private final ProcessRepository processRepository;
    private final SubProcessRepository subProcessRepository;
    private final IssueRepository issueRepository;
    private final ReportRepository reportRepository;
    private final ItemRepository itemRepository;
    private final SmartToolRepository smartToolRepository;
    private final JobRepository jobRepository;
    private final SmartToolItemRepository smartToolItemRepository;
    private final DailyTotalRepository dailyTotalRepository;

    private final ContentRestService contentRestService;
    private final UserRestService userRestService;

    private final ModelMapper modelMapper;
    private final FileUploadService fileUploadService;


    /**
     * Aruco 및 컨텐츠 식별자 발급 처리
     *
     * @return - aruco 및 컨텐츠 식별자 정보
     */
    public ApiResponse<ArucoWithContentUUIDResponse> getAruco() {
        Aruco aruco = this.arucoRepository.getAruco()
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NO_MORE_ARUCO));
        aruco.setContentUUID(UUID.randomUUID().toString());
        this.arucoRepository.save(aruco);
        return new ApiResponse<>(new ArucoWithContentUUIDResponse(aruco.getId(), aruco.getContentUUID()));
    }

    /**
     * Aruco 컨텐츠 할당 해제 처리
     *
     * @return - 할당 해제 처리 결과
     */
    public ApiResponse<ArucoDeallocateResponse> emptyAruco(ArucoDeallocateRequest arucoInfo) {
        // get aruco
        Aruco aruco = this.arucoRepository.findByContentUUID(arucoInfo.getContentUUID())
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));
        // set aruco in dto
        aruco.setContentUUID(null);
        this.arucoRepository.save(aruco);
        return new ApiResponse<>(new ArucoDeallocateResponse(true));
    }

    @Transactional
    public ApiResponse<ProcessRegisterResponse> createTheProcess(ProcessRegisterRequest registerNewProcess) {
        // 공정 생성 요청 처리
        log.info("CREATE THE PROCESS requestBody ---> {}", registerNewProcess.toString());

        // 1. 컨텐츠 메타데이터 가져오기
        ApiResponse<ContentRestDto> contentApiResponse = this.contentRestService.getContentMetadata(registerNewProcess.getContentUUID());
        // TODO : 컨텐츠 메타데이터의 id를 활용하고 있는데, 워크스테이션에서 컨텐츠 id들을 알아야만 공정을 생성시킬 수 있는 상황임. 향후 지속할 것인지 확인필요.

        log.info("CONTENT_METADATA: [{}]", contentApiResponse.getData().toString());

        // 1-1. 에러가 난 경우
        if (contentApiResponse.getCode() != 200) {
            throw new ProcessServiceException(ErrorCode.ERR_PROCESS_REGISTER);
        }

        if (contentApiResponse.getData() == null) {
            throw new ProcessServiceException(ErrorCode.ERR_PROCESS_REGISTER);
        }

        // 3. Aruco 값 가져오기
        Aruco aruco = this.arucoRepository.findByContentUUID(registerNewProcess.getContentUUID())
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_PROCESS_REGISTER));

        log.info("ARUCO: [{}]", aruco.toString());

        // TODO SMIC 예외사항 :  하나의 컨텐츠가 여러 공정을 가질 수 없으므로(1:1) 예외발생. 제품에서는 1:N이므로 대책필요.
        // ARUCO로 공정목록 가져오기
        List<Long> processeIds = this.processRepository.getProcessIdList(aruco.getId());
        // 공정이 존재할 경우 에러. 단 마감된 공정은 다시 진행하지 못하고 재생성 가능하므로 마감된 공정은 제외함.
        for (Long processId : processeIds) {
            Process process = this.processRepository.getProcessInfo(processId).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
            if (!(process.getState().equals(State.CLOSED) || process.getState().equals(State.DELETED))) {
                throw new ProcessServiceException(ErrorCode.ERR_HAS_ALREADY_PROCESS_OF_THIS_CONTENT);
            }
        }

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
                .build();

        try {
            this.processRepository.save(newProcess);

            // 4-1. 공정에 aruco 값 할당
            aruco.addProcess(newProcess);

            // 7. 작업 등록
            this.arucoRepository.save(aruco);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("CONTENT UPLOAD ERROR: {}", e.getMessage());
            throw new ProcessServiceException(ErrorCode.ERR_PROCESS_REGISTER);
        }

        log.info("{}", contentApiResponse.getData().getContents().toString());

        // 5. 세부 공정 정보 리스트 생성

        // TODO 제품 : 컨텐츠 메타데이터에 들어 있는 id값들을 DB에 넣어 놓아야 함.. 차후 컨텐츠 수정시 활용..
        ContentRestDto.Content metadata = contentApiResponse.getData().getContents();
        Map<String, ContentRestDto.SceneGroup> sceneGroupMap = new HashMap<>();

        metadata.getSceneGroups().forEach(sceneGroup -> sceneGroupMap.put(sceneGroup.getId(), sceneGroup));

        addSubProcessOnProcess(registerNewProcess, sceneGroupMap, newProcess);


        ProcessRegisterResponse processRegisterResponse = ProcessRegisterResponse.builder()
                .processId(newProcess.getId())
                .name(newProcess.getName())
                .startDate(newProcess.getStartDate())
                .totalSubProcess(newProcess.getSubProcessList().size())
                .endDate(newProcess.getEndDate())
                .conditions(newProcess.getConditions())
                .progressRate(newProcess.getProgressRate())
                .state(newProcess.getState())
                .build();

        ContentStatusChangeRequest contentStatusChangeRequest = new ContentStatusChangeRequest();
        contentStatusChangeRequest.setContentUUID(registerNewProcess.getContentUUID());
        contentStatusChangeRequest.setStatus("MANAGED");

        this.contentRestService.changeContentStatus(contentStatusChangeRequest);

        return new ApiResponse<>(processRegisterResponse);
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
            registerNewProcess.getSubProcessList().forEach(
                    newSubProcess -> {
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
                                .workerUUID("")
                                .jobList(new ArrayList<>())
                                .build();

                        if (registerNewProcess.getOwnerUUID() == null) {
                            subProcess.setWorkerUUID(newSubProcess.getWorkerUUID());
                        } else {
                            subProcess.setWorkerUUID(registerNewProcess.getOwnerUUID());
                        }

                        this.subProcessRepository.save(subProcess);
                        newProcess.addSubProcess(subProcess);

                        // SubProcess 에 job 정보 추가
                        addJobToSubProcess(sceneGroup, subProcess);
                    });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ERROR : ADD SUB-PROCESS ON PROCESS: {}", e.getMessage());
            throw new ProcessServiceException(ErrorCode.ERR_SUB_PROCESS_REGISTER);
        }
    }

    /**
     * 세부 공정에 작업 내역 추가 처리
     *
     * @param sceneGroup - 메타데이터로부터 파싱된 세부공정(씬구룹) 정보
     * @param subProcess - 신규 세부 공정 관련 정보
     */
    private void addJobToSubProcess(ContentRestDto.SceneGroup sceneGroup, SubProcess subProcess) {
        try {
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
                                .smartToolList(new ArrayList<>())
                                .build();

                        job = this.jobRepository.save(job);
                        subProcess.addJob(job);

                        // Job 에 Report 아이템 추가하기
                        addJobToReport(scene, job);
                        addSmartToolInfoToJob(scene, job);


                        // Calculate number of sub job on main job
                        int smartToolJobs = Optional.of(job.getSmartToolList().size()).orElse(0);
                        int reportJobs = Optional.of(job.getReportList().size()).orElse(0);
                        job.setConditions(INIT_CONDITIONS);

                        this.jobRepository.save(job);
                    });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ERROR : ADD JOB ON SUB-PROCESS: {}", e.getMessage());
            throw new ProcessServiceException(ErrorCode.ERR_JOB_REGISTER);
        }
    }

    /**
     * 작업에 하위 작업 추가 처리
     *
     * @param scene - 메타데이터로부터 파싱된 작업(씬) 정보
     * @param job   - 신규 작업 관련 정보
     */
    private void addJobToReport(ContentRestDto.Scene scene, Job job) {
        try {
            if (!scene.getReportObjects().isEmpty()) {
                scene.getReportObjects().forEach(reportObject -> {
                    // Item이 잇다면
                    if (!reportObject.getItems().isEmpty()) {
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
            throw new ProcessServiceException(ErrorCode.ERR_REPORT_REGISTER);
        }
    }

    /**
     * 작업에 스마트 툴 및 레포트 관련 정보 추가 처리
     *
     * @param scene - 메타데이터로부터 파싱된 작업(씬) 정보
     * @param job   - 신규 작업 관련 정보
     */
    private void addSmartToolInfoToJob(ContentRestDto.Scene scene, Job job) {
        // Job에 SmartTool 정보 추가
        try {
            if (!scene.getSmartToolObjects().isEmpty()) {
                scene.getSmartToolObjects().forEach(smartToolObject -> {
                    SmartTool smartTool = SmartTool.builder()
                            // smart tool job id
                            .jobId(smartToolObject.getJobId())
                            .normalToque(smartToolObject.getNormalTorque() + "")
                            .progressRate(INIT_PROGRESS_RATE)
                            .smartToolItemList(new ArrayList<>())
                            .build();

                    job.addSmartTool(smartTool);
                    this.smartToolRepository.save(smartTool);

                    smartToolObject.getItems().forEach(item -> smartTool.addSmartToolItem(SmartToolItem.builder()
                            .batchCount(item.getBatchCount())
                            .workingToque(null)
                            .result(INIT_RESULT)
                            .build()));
                    this.smartToolItemRepository.saveAll(smartTool.getSmartToolItemList());
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ERROR : ADD SMART-TOOL ON JOB: {}", e.getMessage());
            throw new ProcessServiceException(ErrorCode.ERR_SMART_TOOL_REGISTER);
        }
    }

    public ApiResponse<RecentSubProcessResponse> getNewWork(String workerUUID) {
        // 신규 작업 존재 여부 검사 요청 처리
        boolean hasNewSubProcess = this.subProcessRepository.existsByIsRecentAndWorkerUUID(YesOrNo.YES, workerUUID);
        return new ApiResponse<>(new RecentSubProcessResponse(hasNewSubProcess));
    }

    public ApiResponse<ProcessIdRetrieveResponse> getProcessIdOfContent(String contentUUID) {
        // contentUUID 로부터 aruco id 가져오기
        Aruco aruco = this.arucoRepository.findByContentUUID(contentUUID)
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_ARUCO));
        // aruco id 로부터 process id 가져오기
        List<Long> processeIds = this.processRepository.getProcessIdList(aruco.getId());
        // 생성된 공정이 없거나, 공정의 상태들을 모두 조회 후 모든 공정이 closed이면 공정생성이 가능
        boolean canCreate = true;
        for (Long processId : processeIds) {
            Process process = this.processRepository.getProcessInfo(processId).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
            // 컨텐츠로 등록된 공정들을 모두 조회하여 state가 CREATED, UPDATED인지 확인하여 하나라도 존재하면 업데이트 할 수 없음.
            if (process.getState() == State.CREATED || process.getState() == State.UPDATED) {
                canCreate = false;
            }
        }
        if (processeIds.isEmpty() || canCreate) {
            throw new ProcessServiceException(ErrorCode.ERR_CAN_CREATE_PROCESS);
        }
        return new ApiResponse<>(new ProcessIdRetrieveResponse(processeIds));
    }

    public ApiResponse<ArucoWithContentUUIDResponse> getRelatedInfoOfProcess(Long processId) {
        // 공정의 ARUCO와 contentUUID 가져오기
        Process process = this.processRepository.getProcessInfo(processId).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
        ArucoWithContentUUIDResponse arucoWithContentUUIDResponse = ArucoWithContentUUIDResponse.builder()
                .aruco(process.getAruco().getId())
                .contentUUID(process.getAruco().getContentUUID())
                .build();
        return new ApiResponse<>(arucoWithContentUUIDResponse);
    }

    public ApiResponse<ProcessMetadataResponse.ProcessesMetadata> getMetadataTheProcess(Long[] processesId, String workerUUID) {
        // 공정의 메타데이터 가져오기
        // 반환할 공정 메타데이터
        List<ProcessMetadataResponse.Process> returnProcessMetadataList = new ArrayList<>();

        for (Long processId : processesId) {
            // 요청한 공정들을 모두 조회
            Process process = this.processRepository.getProcessInfo(processId).orElseGet(() -> null);
            if (Objects.isNull(process)) {
                log.info("getMetadataTheProcess - getProcessInfo is null");
                // 조회되는 공정이 없을 경우 예외처리
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
                .processes(returnProcessMetadataList)
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
            Process process = this.processRepository.getProcessInfo(processId).orElseGet(() -> null);
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
                .processes(returnProcessMetadataList)
                .build());
    }

    // CONVERT metadata - PROCESS
    private ProcessMetadataResponse.Process buildMetadataProcess(Process process, Long[] subProcessesId, String workerUUID) {
        List<ProcessMetadataResponse.SubProcess> metaSubProcessList = buildSubProcessList(process.getSubProcessList(), subProcessesId, workerUUID);
        if (metaSubProcessList.isEmpty()) return null;
        else {
            return ProcessMetadataResponse.Process.builder()
                    .arucoId(process.getAruco().getId())
                    .processId(process.getId())
                    .processName(process.getName())
                    .managerUUID(null)
                    .position(process.getPosition())
                    .subProcessTotal(process.getSubProcessList().size())
                    .startDate(process.getStartDate())
                    .endDate(process.getEndDate())
                    .conditions(process.getConditions())
                    .state(process.getState())
                    .progressRate(process.getProgressRate())
                    .subProcesses(metaSubProcessList)
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
            build = ProcessMetadataResponse.SubProcess.builder()
                    .subProcessId(subProcess.getId())
                    .subProcessName(subProcess.getName())
                    .priority(subProcess.getPriority())
                    .jobTotal(subProcess.getJobList().size())
                    .startDate(subProcess.getStartDate())
                    .endDate(subProcess.getEndDate())
                    .conditions(subProcess.getConditions())
                    .progressRate(subProcess.getProgressRate())
                    .workerUUID(workerSourceUUID)
                    .syncDate(subProcess.getUpdatedDate())
                    .syncUserUUID(subProcess.getWorkerUUID())
                    .isRecent(subProcess.getIsRecent())
                    .jobs(buildJobList(subProcess.getJobList()))
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
                .subJobTotal(job.getReportList().size() + job.getSmartToolList().size())
                .conditions(job.getConditions())
                .isReported(job.getIsReported())
                .progressRate(job.getProgressRate())
                .smartTools(buildSmartToolList(job.getSmartToolList()))
                .reports(buildReportList(job.getReportList()))
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
                .items(buildReportItemList(report.getItemList()))
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
                .build();
    }

    // CONVERT metadata - SMART TOOL LIST
    private List<ProcessMetadataResponse.SmartTool> buildSmartToolList(List<SmartTool> smartTools) {
        List<ProcessMetadataResponse.SmartTool> metaSmartToolList = new ArrayList<>();
        for (SmartTool smartTool : smartTools) {
            ProcessMetadataResponse.SmartTool metadataSmartTool = buildMetadataSmartTool(smartTool);
            metaSmartToolList.add(metadataSmartTool);
        }
        return metaSmartToolList;
    }

    // CONVERT metadata - SMART TOOL
    private ProcessMetadataResponse.SmartTool buildMetadataSmartTool(SmartTool smartTool) {
        return ProcessMetadataResponse.SmartTool.builder()
                .smartToolId(smartTool.getId())
                .smartToolJobId(smartTool.getJobId())
                .normalToque(smartTool.getNormalToque())
                .items(buildSmartToolItemList(smartTool.getSmartToolItemList()))
                .build();
    }

    // CONVERT metadata - SMART TOOL ITEM LIST
    private List<ProcessMetadataResponse.SmartToolItem> buildSmartToolItemList(List<SmartToolItem> smartToolItems) {
        List<ProcessMetadataResponse.SmartToolItem> metaSmartToolItemList = new ArrayList<>();
        for (SmartToolItem smartToolItem : smartToolItems) {
            ProcessMetadataResponse.SmartToolItem metadataSmartToolItem = buildMetadataSmartToolItem(smartToolItem);
            metaSmartToolItemList.add(metadataSmartToolItem);
        }
        return metaSmartToolItemList;
    }

    // CONVERT metadata - SMART TOOL ITEM
    private ProcessMetadataResponse.SmartToolItem buildMetadataSmartToolItem(SmartToolItem smartToolItem) {
        return ProcessMetadataResponse.SmartToolItem.builder()
                .id(smartToolItem.getId())
                .batchCount(smartToolItem.getBatchCount())
                .workingToque(smartToolItem.getWorkingToque())
                .result(smartToolItem.getResult())
                .build();
    }

    public ApiResponse<HourlyReportCountListResponse> getHourlyReports(String targetDate, String status) {
        // 해당일의 상태의 조건으로 리포트 개수를 시간대별로 조회
        List<HourlyReportCountOfaDayResponse> hourlyReportCountOfaDayResponses = this.reportRepository.selectHourlyReports(targetDate);
        return new ApiResponse<>(new HourlyReportCountListResponse(hourlyReportCountOfaDayResponses));
    }

    public ApiResponse<MonthlyStatisticsResponse> getDailyTotalRateAtMonth(String month) {
        List<DailyTotal> dailyTotals = this.dailyTotalRepository.getDailyTotalRateAtMonth(month);

        return new ApiResponse<>(MonthlyStatisticsResponse.builder()
                .dailyTotal(dailyTotals.stream().map(dailyTotal -> {
                    return MonthlyStatisticsResponse.DailyTotalResponse.builder()
                            .id(dailyTotal.getId())
                            // UTC -> KST 로 9시간을 더해서 날짜산정 해야할 것 같은데 결과는 하지 않아도 KST로 주는 것 같음. 자바로 넘어오면서 KST로 바뀌는 것 같음.
                            .onDay(dailyTotal.getUpdatedDate().toLocalDate())
                            .totalRate(dailyTotal.getTotalRate())
                            .totalProcesses(dailyTotal.getTotalCountProcesses())
                            .build();
                }).collect(Collectors.toList()))
                .build());
    }

    public ApiResponse<IssuesResponse> getIssuesInSearchUserName(String search, Pageable pageable) {
        List<UserInfoResponse> userInfos = getUserInfoSearch(search);
        List<String> userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
        // querydsl 에서는 null처리를 자동으로 해주지만 native이기 때문에 null처리 해야만 함.
        if (userUUIDList.size() == 0) userUUIDList = null;
        Page<Issue> issuePage = this.issueRepository.getIssuesInSearchUserName(userUUIDList, pageable);

        return getIssuesResponseApiResponse(pageable, issuePage);
    }

    public ApiResponse<IssuesResponse> getIssuesOutSearchUserName(String search, Pageable pageable) {
        List<UserInfoResponse> userInfos = getUserInfoSearch(search);
        List<String> userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
        // querydsl 에서는 null처리를 자동으로 해주지만 native이기 때문에 null처리 해야만 함.
        if (userUUIDList.size() == 0) userUUIDList = null;
        Page<Issue> issuePage = this.issueRepository.getIssuesOutSearchUserName(userUUIDList, pageable);

        return getIssuesResponseApiResponse(pageable, issuePage);
    }

    public ApiResponse<IssuesResponse> getIssuesAllSearchUserName(String search, Pageable pageable) {
        List<UserInfoResponse> userInfos = getUserInfoSearch(search);
        List<String> userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
        // querydsl 에서는 null처리를 자동으로 해주지만 native이기 때문에 null처리 해야만 함.
        if (userUUIDList.size() == 0) userUUIDList = null;
        Page<Issue> issuePage = this.issueRepository.getIssuesAllSearchUserName(userUUIDList, pageable);

        return getIssuesResponseApiResponse(pageable, issuePage);
    }

    public ApiResponse<IssuesResponse> getIssuesIn(Pageable pageable) {
        Page<Issue> issuePage = this.issueRepository.getIssuesIn(pageable);

        return getIssuesResponseApiResponse(pageable, issuePage);
    }

    public ApiResponse<IssuesResponse> getIssuesOut(Pageable pageable) {
        Page<Issue> issuePage = this.issueRepository.getIssuesOut(pageable);

        return getIssuesResponseApiResponse(pageable, issuePage);
    }

    public ApiResponse<IssuesResponse> getIssuesAll(Pageable pageable) {
        Page<Issue> issuePage = this.issueRepository.getIssuesAll(pageable);

        return getIssuesResponseApiResponse(pageable, issuePage);
    }

    public ApiResponse<IssuesResponse> getIssuesInSearchProcessTitle(String title, Pageable pageable) {
        Page<Issue> issuePage = this.issueRepository.getIssuesInSearchProcessTitle(title, pageable);

        return getIssuesResponseApiResponse(pageable, issuePage);
    }

    public ApiResponse<IssuesResponse> getIssuesInSearchSubProcessTitle(String title, Pageable pageable) {
        Page<Issue> issuePage = this.issueRepository.getIssuesInSearchSubProcessTitle(title, pageable);

        return getIssuesResponseApiResponse(pageable, issuePage);
    }

    public ApiResponse<IssuesResponse> getIssuesInSearchJobTitle(String title, Pageable pageable) {
        Page<Issue> issuePage = this.issueRepository.getIssuesInSearchJobTitle(title, pageable);

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
                    .processId(Objects.isNull(process) ? 0 : process.getId())
                    .processName(Objects.isNull(process) ? null : process.getName())
                    .subProcessId(Objects.isNull(subProcess) ? 0 : subProcess.getId())
                    .subProcessName(Objects.isNull(subProcess) ? null : subProcess.getName())
                    .jobId(Objects.isNull(job) ? 0 : job.getId())
                    .jobName(Objects.isNull(job) ? null : job.getName())
                    .workerUUID(userInfo.getUuid())
                    .workerName(userInfo.getName())
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

    public ApiResponse<ReportsResponse> getReports(Long processId, Long subProcessId, String search, Boolean reported, Pageable pageable) {
        // 리포트 목록을 category 내에서 조회
        Page<Report> reportPage = this.reportRepository.getReports(processId, subProcessId/*, search*/, reported, pageable);
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
            return ReportInfoResponse.builder()
                    .processId(process.getId())
                    .subProcessId(subProcess.getId())
                    .jobId(job.getId())
                    .reportId(report.getId())
                    .reportedDate(report.getJob().getSubProcess().getReportedDate())
                    .processName(process.getName())
                    .subProcessName(subProcess.getName())
                    .jobName(job.getName())
                    // TODO : user info
                    .workerUUID(Optional.of(subProcess).map(SubProcess::getWorkerUUID).orElseGet(() -> ""))
                    .reportItems(itemResponseList)
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

    public ApiResponse<SmartToolsResponse> getSmartToolJobs(Long subProcessId, String search, Boolean reported, Pageable pageable) {
        // category 범주 내의 스마트툴 작업 목록 조회
        Page<SmartTool> smartToolPage = this.smartToolRepository.getSmartToolJobs(subProcessId, search, reported, pageable);
//        Page<SmartTool> smartToolPage = this.smartToolRepository.findSmartToolsByJob(processId, subProcessId, pageable);
        List<SmartToolResponse> smartToolResponseList = smartToolPage.stream().map(smartTool -> {
            List<SmartToolItem> smartToolItems = Optional.ofNullable(this.smartToolItemRepository.findBySmartTool(smartTool)).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SMART_TOOL_ITEM));
            // 스마트툴의 아이템
            List<SmartToolItemResponse> smartToolItemResponseList = smartToolItems.stream().map(smartToolItem -> {
                return SmartToolItemResponse.builder()
                        .id(smartToolItem.getId())
                        .batchCount(Optional.of(smartToolItem).map(SmartToolItem::getBatchCount).orElseGet(() -> 0))
                        .workingToque(Optional.of(smartToolItem).map(SmartToolItem::getWorkingToque).orElseGet(() -> ""))
                        .result(Optional.of(smartToolItem).map(SmartToolItem::getResult).orElseGet(() -> INIT_RESULT))
                        .build();
            }).collect(Collectors.toList());
            Job job = Optional.of(smartTool).map(SmartTool::getJob).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_JOB));
            SubProcess subProcess = Optional.ofNullable(job).map(Job::getSubProcess).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SUBPROCESS));
            Process process = Optional.ofNullable(subProcess).map(SubProcess::getProcess).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
            return SmartToolResponse.builder()
                    .processId(process.getId())
                    .subProcessId(subProcess.getId())
                    .jobId(job.getId())
                    .smartToolId(smartTool.getId())
                    .reportedDate(subProcess.getReportedDate())
                    .processName(process.getName())
                    .subProcessName(subProcess.getName())
                    .jobName(job.getName())
                    .smartToolJobId(smartTool.getJobId())
                    .smartToolBatchTotal(smartTool.getSmartToolItemList().size())
                    .smartToolWorkedCount(smartTool.getSmartToolItemList().stream().filter(smartToolItem ->
                            Optional.of(smartToolItem).map(SmartToolItem::getResult).orElseGet(() -> INIT_RESULT).equals(Result.OK)).count())
                    .normalToque(smartTool.getNormalToque())
                    // TODO : user info
                    .workerUUID(subProcess.getWorkerUUID())
                    // TODO : 작업자 이미지 가져오기
                    .smartToolItems(smartToolItemResponseList)
                    .build();
        }).collect(Collectors.toList());

        PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
                .currentPage(pageable.getPageNumber())
                .currentSize(pageable.getPageSize())
                .totalPage(smartToolPage.getTotalPages())
                .totalElements(smartToolPage.getTotalElements())
                .build();
        return new ApiResponse<>(new SmartToolsResponse(smartToolResponseList, pageMetadataResponse));
    }

    @Transactional
    public ApiResponse<WorkResultSyncResponse> uploadOrSyncWorkResult(WorkResultSyncRequest uploadWorkResult) {
        // 1. 작업 내용 가져오기
        if (uploadWorkResult.getProcesses() != null && uploadWorkResult.getProcesses().size() > 0) {
            uploadWorkResult.getProcesses().forEach(this::syncProcessResult);
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
        processResult.getSubProcesses().forEach(subProcessWorkResult -> {
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
            subProcess.setReportedDate(nowTime);
            subProcessRepository.save(subProcess);

            updateProcess.set(true);

            if (subProcessWorkResult.getJobs() != null) {
                syncJobWork(subProcessWorkResult.getJobs(), subProcessWorkResult.getSyncUserUUID());
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
            if (jobWorkResult.getIsReported() != null) {
                job.setIsReported(jobWorkResult.getIsReported());
            }
            if (jobWorkResult.getResult() != null) {
                job.setResult(jobWorkResult.getResult());
            }
            if (jobWorkResult.getIsReported() != null || jobWorkResult.getResult() != null) {
                jobRepository.save(job);
            }

            if (jobWorkResult.getReports() != null) {
                syncReportWork(jobWorkResult.getReports());
            }
            if (jobWorkResult.getSmartTools() != null) {
                syncSmartToolWork(jobWorkResult.getSmartTools());
            }
            if (jobWorkResult.getIssues() != null) {
                syncIssueWork(jobWorkResult.getIssues(), syncUserUUID, job);
            }
        });
    }

    // 레포트 내용 동기화
    private void syncReportWork(List<WorkResultSyncRequest.ReportWorkResult> reportWorkResults) {
        reportWorkResults.forEach(reportWorkResult -> {
            if (reportWorkResult.getItems() != null) {
                syncReportItemWork(reportWorkResult.getItems());
            }
        });
    }

    // 레포트 작업 아이템 동기화
    private void syncReportItemWork(List<WorkResultSyncRequest.ReportItemWorkResult> reportItemWorkResults) {
        reportItemWorkResults.forEach(reportItemWorkResult -> {
            Item item = this.itemRepository.findById(reportItemWorkResult.getId())
                    .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_PROCESS_WORK_RESULT_SYNC));
            if (reportItemWorkResult != null && !StringUtils.isEmpty(reportItemWorkResult.getPhotoFile())) {
                item.setPath(getFileUploadUrl(reportItemWorkResult.getPhotoFile()));
            }
            item.setAnswer(reportItemWorkResult.getAnswer());
            item.setResult(reportItemWorkResult.getResult() == null ? INIT_RESULT : reportItemWorkResult.getResult());
            itemRepository.save(item);
        });
    }

    // 스마트 툴 작업 동기화
    private void syncSmartToolWork(List<WorkResultSyncRequest.SmartToolWorkResult> smartToolWorkResults) {
        smartToolWorkResults.forEach(smartToolWorkResult -> {
            if (smartToolWorkResult.getItems() != null) {
                syncSmartToolItemWork(smartToolWorkResult.getItems());
            }
        });
    }

    // 스마트 툴 작업 아이템 동기화
    private void syncSmartToolItemWork(List<WorkResultSyncRequest.SmartToolItemWorkResult> smartToolItemWorkResults) {
        smartToolItemWorkResults.forEach(smartToolItemWorkResult -> {
            SmartToolItem smartToolItem = smartToolItemRepository.findById(smartToolItemWorkResult.getId())
                    .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_PROCESS_WORK_RESULT_SYNC));
            smartToolItem.setBatchCount(smartToolItemWorkResult.getBatchCount());
            smartToolItem.setWorkingToque(smartToolItemWorkResult.getWorkingTorque());
            smartToolItem.setResult(smartToolItemWorkResult.getResult() == null ? INIT_RESULT : smartToolItemWorkResult.getResult());
            this.smartToolItemRepository.save(smartToolItem);
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
            if (workIssueResult != null && !StringUtils.isEmpty(workIssueResult.getPhotoFile())) {
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
            if (issueResult != null && !StringUtils.isEmpty(issueResult.getPhotoFile())) {
                issue.setPath(getFileUploadUrl(issueResult.getPhotoFile()));
            }
            log.info("IssueResult: {}", issueResult);
            log.info("WorkerUUID: [{}]", issueResult.getWorkerUUID());
            log.info("Global Issue: [{}]", issue);
            this.issueRepository.save(issue);
        });
    }

    public ResponseMessage getTotalRate() {
        // 전체 공정 진행률 조회
        List<Process> processes = this.processRepository.getProcesses();
        return new ResponseMessage().addParam("totalRate", ProgressManager.getAllProcessesTotalProgressRate(processes));
    }

    public ApiResponse<ProcessesStatisticsResponse> getStatistics() {
        // 전체 공정 진행률 및 공정진행상태별 현황 조회
        int totalRate = 0, totalProcesses = 0, categoryWait = 0, categoryStarted = 0, categoryEnded = 0, wait = 0, unprogressing = 0, progressing = 0, completed = 0, incompleted = 0, failed = 0, success = 0, fault = 0;
        List<Process> processes = this.processRepository.getProcesses();
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
                .totalProcesses(totalProcesses)
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

    public ApiResponse<ProcessListResponse> getProcessList(String search, List<Conditions> filter, Pageable pageable) {
        // 전체 공정의 목록을 조회
        // 사용자 검색
        List<UserInfoResponse> userInfos = getUserInfoSearch(search);
        List<String> userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
        // querydsl 에서는 null처리를 자동으로 해주지만 native이기 때문에 null처리 해야만 함.
        if (userUUIDList.size() == 0) userUUIDList = null;

        // pageable 을 멀티로 여러개 받는 방법을 몰라서 서버에서 임의로 공정생성주기와 업데이트 일자로 부가정렬을 추가함.
        Pageable pageableCustom = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort().and(Sort.by(Sort.Direction.DESC, "state")).and(Sort.by(Sort.Direction.DESC, "updated_at")));

        Page<Process> processPage = null;
        if (filter != null && filter.size() > 0) {
            List<Process> processList = this.processRepository.getProcessListSearchUser(search, userUUIDList, pageableCustom.getSort());
            processPage = filterConditionsProcessPage(processList, filter, pageable);
        } else {
            processPage = this.processRepository.getProcessPageSearchUser(search, userUUIDList, pageableCustom);
        }
        return getProcessesPageResponseApiResponse(pageable, processPage);
    }

    private Page<Process> filterConditionsProcessPage(List<Process> processList, List<Conditions> filter, Pageable pageable) {
        List<Process> processes = new ArrayList<>();
        for (Process process : processList) {
            // 상태가 일치하는 공정만 필터링
            if (filter.contains(process.getConditions())) {
                processes.add(process);
            }
        }
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > processes.size() ? processes.size() : (start + pageable.getPageSize());
        return new PageImpl<>(processes.subList(start, end), pageable, processes.size());
    }

    private ApiResponse<ProcessListResponse> getProcessesPageResponseApiResponse(Pageable pageable, Page<Process> processPage) {
        List<ProcessInfoResponse> processInfoResponseList = processPage.stream().map(process -> {
            return ProcessInfoResponse.builder()
                    .id(process.getId())
                    .name(process.getName())
                    .contentUUID(process.getAruco().getContentUUID())
                    .state(Optional.of(process).map(Process::getState).orElseGet(() -> State.CREATED))
                    .position(process.getPosition())
                    .progressRate(process.getProgressRate())
                    .conditions(process.getConditions())
                    .doneCount((int) process.getSubProcessList().stream().filter(subProcess -> subProcess.getConditions() == Conditions.COMPLETED || subProcess.getConditions() == Conditions.SUCCESS).count())
                    .issuesTotal(this.processRepository.getCountIssuesInProcess(process.getId()))
                    .startDate(Optional.of(process).map(Process::getStartDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
                    .endDate(Optional.of(process).map(Process::getEndDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
                    .reportedDate(process.getReportedDate())
                    .createdDate(process.getCreatedDate())
                    .updatedDate(process.getUpdatedDate())
                    .subProcessTotal(Optional.of(process.getSubProcessList().size()).orElse(0))
                    .subProcessAssign(this.getSubProcessesAssign(process))
                    .aruco_id(process.getAruco().getId())
                    .build();
        }).collect(Collectors.toList());

        PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
                .currentPage(pageable.getPageNumber())
                .currentSize(pageable.getPageSize())
                .totalPage(processPage.getTotalPages())
                .totalElements(processPage.getTotalElements())
                .build();
        return new ApiResponse<>(new ProcessListResponse(processInfoResponseList, pageMetadataResponse));
    }

    @Transactional
    public ApiResponse<ProcessInfoResponse> setClosedProcess(Long processId) {
        // 공정마감
        // 공정수행중의 여부와 관계없이 마감됨. 뷰에서는 오프라인으로 작업 후 최종 동기화이기 때문.
        // 공정조회
        Process process = this.processRepository.getProcessInfo(processId).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
        // 마감 상태로 변경
        process.setState(State.CLOSED);
        this.processRepository.save(process);

        // 컨텐츠의 상태를 대기중으로 변경. 공정 종료된 컨텐츠는 더이상 관리 대상이 아님. 종료공정을 증강하거나, 공정을 다시 시작하지 않기 때문.
        ContentStatusChangeRequest contentStatusChangeRequest = new ContentStatusChangeRequest();
        contentStatusChangeRequest.setContentUUID(process.getAruco().getContentUUID());
        contentStatusChangeRequest.setStatus("WAIT");

        this.contentRestService.changeContentStatus(contentStatusChangeRequest);

        // TODO : 다른 서비스를 호출하는 것이 옳은 것인지 확인이 필요. redirect를 해야하나?
        // 공정상세조회하여 반환
        return this.getProcessInfo(process.getId());
    }

    public ApiResponse<ProcessInfoResponse> getProcessInfo(Long processId) {
        // 공정상세조회
        Process process = this.processRepository.getProcessInfo(processId).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
        ProcessInfoResponse processInfoResponse = modelMapper.map(process, ProcessInfoResponse.class);
        // contentUUID 추가
        processInfoResponse.setContentUUID(process.getAruco().getContentUUID());
        processInfoResponse.setSubProcessTotal(Optional.of(process.getSubProcessList().size()).orElse(0));
        processInfoResponse.setDoneCount((int) process.getSubProcessList().stream().filter(subProcess -> subProcess.getConditions() == Conditions.COMPLETED || subProcess.getConditions() == Conditions.SUCCESS).count());
        processInfoResponse.setIssuesTotal(this.processRepository.getCountIssuesInProcess(process.getId()));
        processInfoResponse.setSubProcessAssign(this.getSubProcessesAssign(process));
        return new ApiResponse<>(processInfoResponse);
    }

    /*
    공정 및 그 하위의 모든(세부공정, 작업 등) 정보를 업데이트함.
     */
    @Transactional
    public ResponseMessage updateProcess(EditProcessRequest editProcessRequest) {
        // 공정편집
        try {
            // 1 공정편집가능여부판단
            // 1-1 공정조회
            Process updateSourceProcess = this.processRepository.getProcessInfo(editProcessRequest.getProcessId()).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));

            // 공정진행중여부확인 - 편집할 수 없는 상태라면 에러
            if (updateSourceProcess.getState() == State.CLOSED || updateSourceProcess.getState() == State.DELETED) {
                throw new ProcessServiceException(ErrorCode.ERR_PROCESS_UPDATED);
            }
            // 2 공정 상세정보 편집
            updateSourceProcess.setStartDate(Optional.of(editProcessRequest).map(EditProcessRequest::getStartDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")));
            updateSourceProcess.setEndDate(Optional.of(editProcessRequest).map(EditProcessRequest::getEndDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")));
            updateSourceProcess.setPosition(Optional.of(editProcessRequest).map(EditProcessRequest::getPosition).orElseGet(() -> ""));
            updateSourceProcess = this.processRepository.save(updateSourceProcess);

            // 2-1 세부공정 상세정보 편집
            editProcessRequest.getSubProcessList().forEach(editSubProcessRequest -> {
                if (!(boolean) updateSubProcess(editSubProcessRequest.getSubProcessId(), editSubProcessRequest).getData().get("result"))
                    new ProcessServiceException(ErrorCode.ERR_PROCESS_UPDATED);
            });

            return new ResponseMessage().addParam("result", true);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("PROCESS UPDATE ERROR : {}", e.getMessage());
            throw new ProcessServiceException(ErrorCode.ERR_PROCESS_UPDATED);
        }
    }

    public ApiResponse<SubProcessListResponse> getSubProcessList(Long processId, String search, List<Conditions> filter, Pageable pageable) {
        // 공정내 세부공정목록조회
        // 공정정보
        Process process = this.processRepository.getProcessInfo(processId).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
        String processName = process.getName();
        // 검색어로 사용자 목록 조회
        List<UserInfoResponse> userInfos = getUserInfoSearch(search);
        List<String> userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
        // querydsl 에서는 null처리를 자동으로 해주지만 native이기 때문에 null처리 해야만 함.
        if (userUUIDList.size() == 0) userUUIDList = null;
        // 세부공정 목록
        Page<SubProcess> subProcessPage = null;
        if (filter != null && filter.size() > 0) {
            List<SubProcess> subProcessList = this.subProcessRepository.selectSubProcessList(processId, search, userUUIDList, pageable.getSort());
            subProcessPage = filterConditionsSubProcessPage(subProcessList, filter, pageable);
        } else {
            subProcessPage = this.subProcessRepository.selectSubProcesses(processId, search, userUUIDList, pageable);
        }
        List<EditSubProcessResponse> editSubProcessResponseList = subProcessPage.stream().map(subProcess -> {
            return EditSubProcessResponse.builder()
                    .subProcessId(subProcess.getId())
                    .priority(subProcess.getPriority())
                    .name(subProcess.getName())
                    .jobTotal(subProcess.getJobList().size())
                    .conditions(Optional.of(subProcess).map(SubProcess::getConditions).orElseGet(() -> Conditions.WAIT))
                    .startDate(Optional.of(subProcess).map(SubProcess::getStartDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
                    .endDate(Optional.of(subProcess).map(SubProcess::getEndDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
                    .progressRate(Optional.of(subProcess).map(SubProcess::getProgressRate).orElseGet(() -> 0))
                    .reportedDate(Optional.of(subProcess).map(SubProcess::getReportedDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
                    .isRecent(Optional.of(subProcess).map(SubProcess::getIsRecent).orElseGet(() -> YesOrNo.NO))
                    // TODO : user info
                    .workerUUID(Optional.of(subProcess).map(SubProcess::getWorkerUUID).orElseGet(() -> ""))
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
        return new ApiResponse<>(new SubProcessListResponse(processId, processName, editSubProcessResponseList, pageMetadataResponse));
    }

    private Page<SubProcess> filterConditionsSubProcessPage(List<SubProcess> subProcessList, List<Conditions> filter, Pageable pageable) {
        List<SubProcess> subProcesses = new ArrayList<>();
        for (SubProcess subProcess : subProcessList) {
            // 상태가 일치하는 공정만 필터링
            if (filter.contains(subProcess.getConditions())) {
                subProcesses.add(subProcess);
            }
        }
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > subProcesses.size() ? subProcesses.size() : (start + pageable.getPageSize());
        return new PageImpl<>(subProcesses.subList(start, end), pageable, subProcesses.size());
    }

    public ApiResponse<SubProcessesResponse> getSubProcesses(Long processId, String search, Pageable pageable) {
        // 워크스페이스 전체의 세부공정목록조회
        // 검색어로 사용자 목록 조회
        List<UserInfoResponse> userInfos = getUserInfoSearch(search);
        List<String> userUUIDList = userInfos.stream().map(UserInfoResponse::getUuid).collect(Collectors.toList());
        // querydsl 에서는 null처리를 자동으로 해주지만 native이기 때문에 null처리 해야만 함.
        if (userUUIDList.size() == 0) userUUIDList = null;
        // 세부공정 목록
        Page<SubProcess> subProcessPage = this.subProcessRepository.selectSubProcesses(processId, search, userUUIDList, pageable);
        List<SubProcessReportedResponse> editSubProcessResponseList = subProcessPage.stream().map(subProcess -> {
            return SubProcessReportedResponse.builder()
                    .processId(subProcess.getProcess().getId())
                    .processName(subProcess.getProcess().getName())
                    .subProcessId(subProcess.getId())
                    .name(subProcess.getName())
                    .conditions(subProcess.getConditions())
                    .reportedDate(Optional.of(subProcess).map(SubProcess::getReportedDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
                    // TODO : user info
                    .workerUUID(Optional.of(subProcess).map(SubProcess::getWorkerUUID).orElseGet(() -> ""))
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

    public ApiResponse<SubProcessInfoResponse> getSubProcess(Long subProcessId) {
        // 세부공정 상세조회
        SubProcess subProcess = this.subProcessRepository.findById(subProcessId)
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER));
        return new ApiResponse<>(SubProcessInfoResponse.builder()
                .processId(subProcess.getProcess().getId())
                .processName(subProcess.getProcess().getName())
                .subProcessId(subProcess.getId())
                .name(subProcess.getName())
                .priority(subProcess.getPriority())
                .jobTotal(subProcess.getJobList().size())
                .conditions(subProcess.getConditions())
                .startDate(subProcess.getStartDate())
                .endDate(subProcess.getEndDate())
                .progressRate(subProcess.getProgressRate())
                .reportedDate(subProcess.getReportedDate())
                .isRecent(subProcess.getIsRecent())
                // TODO : user info
                .workerUUID(subProcess.getWorkerUUID())
                .issuesTotal(this.issueRepository.countIssuesInSubProcess(subProcess.getId()))
                .doneCount((int) subProcess.getJobList().stream().filter(job -> job.getConditions() == Conditions.COMPLETED || job.getConditions() == Conditions.SUCCESS).count())
                .build());
    }

    @Transactional
    public ApiResponse<MyWorkListResponse> getMyWorks(String workerUUID, Long processId, String search, Pageable pageable) {
        // 내 작업 조회. 종료된 공정은 제외.
        // TODO : ARUCO테이블에 process id가 없어도 조회되어야 함. 공정이 마감되면 컨텐츠와 링크는 끊기지만, 공정은 계속 조회할 수 있어야 함.
        Page<SubProcess> subProcessPage = this.subProcessRepository.getMyWorksInProcess(workerUUID, processId, search, pageable);
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
            Aruco aruco = Optional.of(process).map(Process::getAruco).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_ARUCO));
            // 공정이 정상적으로 생성되지 않고, 임의로 데이터를 넣으면 process_id가 null일 수 있음.
            return MyWorksResponse.builder()
                    .processId(process.getId())
                    .processName(process.getName())
                    .contentUUID(aruco.getContentUUID())
                    // TODO : 해당 컨텐츠(contentUUID)로 컨텐츠 DB에서 조회하여 경로를 가져와야 정상이지만, 임의로 처리함.
                    .downloadPath(CONTENT_DOWNLOAD_CONTEXT_PATH.concat(aruco.getId().toString()).concat(CONTENT_DOWNLOAD_CONTEXT_FILE_EXTENSION))
                    .subProcessId(subProcess.getId())
                    .priority(subProcess.getPriority())
                    .name(subProcess.getName())
                    .jobTotal(Optional.of(subProcess).map(SubProcess::getJobList).map(List<Job>::size).orElseGet(() -> 0))
                    .conditions(Optional.of(subProcess).map(SubProcess::getConditions).orElseGet(() -> Conditions.WAIT))
                    .startDate(subProcess.getStartDate())
                    .endDate(subProcess.getEndDate())
                    .progressRate(Optional.of(subProcess).map(SubProcess::getProgressRate).orElseGet(() -> 0))
                    .isRecent(Optional.of(subProcess).map(SubProcess::getIsRecent).orElseGet(() -> YesOrNo.NO))
                    // TODO : user info
                    .workerUUID(subProcess.getWorkerUUID())
                    .reportedDate(Optional.of(subProcess).map(SubProcess::getReportedDate).orElseGet(() -> LocalDateTime.parse("1500-01-01T00:00:00")))
                    .doneCount((int) subProcess.getJobList().stream().filter(job -> job.getConditions() == Conditions.COMPLETED || job.getConditions() == Conditions.SUCCESS).count())
                    .state(process.getState())
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

    public ApiResponse<SubProcessesOfArucoResponse> getSubProcessesOfAruco(Long arucoId, Pageable pageable) {
        // ARUCO로 공정을 조회
        Process process = this.processRepository.getProcessUnClosed(arucoId).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS_OF_ARUCO));
        Page<SubProcess> subProcessPage = this.subProcessRepository.selectSubProcesses(process.getId(), null, null, pageable);
        SubProcessesOfArucoResponse subProcessesOfArucoResponse = SubProcessesOfArucoResponse.builder()
                .processId(process.getId())
                .processName(process.getName())
                .contentUUID(process.getAruco().getContentUUID())
                .downloadPath(CONTENT_DOWNLOAD_CONTEXT_PATH.concat(process.getAruco().getId().toString()).concat(CONTENT_DOWNLOAD_CONTEXT_FILE_EXTENSION))
                .subProcesses(subProcessPage.getContent().stream().map(subProcess -> {
                    return SubProcessOfArucoResponse.builder()
                            .subProcessId(subProcess.getId())
                            .name(subProcess.getName())
                            .priority(subProcess.getPriority())
                            .jobTotal(subProcess.getJobList().size())
                            .startDate(subProcess.getStartDate())
                            .endDate(subProcess.getEndDate())
                            .reportedDate(subProcess.getReportedDate())
                            .conditions(subProcess.getConditions())
                            .progressRate(subProcess.getProgressRate())
                            .isRecent(subProcess.getIsRecent())
                            // TODO : user info
                            .workerUUID(subProcess.getWorkerUUID())
                            .build();
                }).collect(Collectors.toList()))
                .pageMeta(PageMetadataResponse.builder()
                        .currentPage(pageable.getPageNumber())
                        .currentSize(pageable.getPageSize())
                        .totalPage(subProcessPage.getTotalPages())
                        .totalElements(subProcessPage.getTotalElements())
                        .build())
                .build();
        return new ApiResponse<>(subProcessesOfArucoResponse);
    }

    public ResponseMessage updateSubProcess(Long subProcessId, EditSubProcessRequest subProcessRequest) {
        // 세부공정 조회
        SubProcess subProcess = this.subProcessRepository.findById(subProcessId)
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER));
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

    public ApiResponse<JobListResponse> getJobs(Long subProcessId, String search, List<Conditions> filter, Pageable pageable) {
        // 작업목록조회
        // 세부공정조회
        SubProcess subProcess = this.subProcessRepository.findById(subProcessId)
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SUBPROCESS));
        // 공정조회
        Process process = this.processRepository.findById(subProcess.getProcess().getId())
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
        Page<Job> jobPage = null;
        if (filter != null && filter.size() > 0) {
            List<Job> jobList = Optional.ofNullable(this.jobRepository.getJobList(subProcessId, search, pageable.getSort())).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_JOB));
            jobPage = filterConditionsJobPage(jobList, filter, pageable);
        } else {
            jobPage = Optional.ofNullable(this.jobRepository.getJobs(subProcessId, search, pageable)).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_JOB));
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
            // report, issue, smart tool은 job 하위에 1개씩만 생성하는 것으로 메이크와 협의됨.
            if (job.getReportList().size() > 0) {
                JobResponse.Report report = JobResponse.Report.builder()
                        .id(job.getReportList().get(0).getId())
                        .build();
                jobResponse.setReport(report);
            }
            if (job.getIssueList().size() > 0) {
                JobResponse.Issue issue = JobResponse.Issue.builder()
                        .id(job.getIssueList().get(0).getId())
                        .build();
                jobResponse.setIssue(issue);
            }
            if (job.getSmartToolList().size() > 0) {
                JobResponse.SmartTool smartTool = JobResponse.SmartTool.builder()
                        .id(job.getSmartToolList().get(0).getId())
                        .smartToolJobId(job.getSmartToolList().get(0).getJobId())
                        .smartToolWorkedCount(job.getSmartToolList().get(0).getSmartToolItemList().stream().filter(smartToolItem -> !Objects.isNull(smartToolItem.getResult()) && smartToolItem.getResult().equals(Result.OK)).count())
                        .smartToolBatchTotal(job.getSmartToolList().get(0).getSmartToolItemList().size())
                        .build();
                jobResponse.setSmartTool(smartTool);
            }
            return jobResponse;
        }).collect(Collectors.toList());

        PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
                .currentPage(pageable.getPageNumber())
                .currentSize(pageable.getPageSize())
                .totalPage(jobPage.getTotalPages())
                .totalElements(jobPage.getTotalElements())
                .build();

        JobListResponse jobListResponse = JobListResponse.builder()
                .processId(process.getId())
                .processName(process.getName())
                .subProcessId(subProcessId)
                .subProcessName(subProcess.getName())
                .jobs(jobList)
                .pageMeta(pageMetadataResponse)
                .build();
        return new ApiResponse<>(jobListResponse);
    }

    private Page<Job> filterConditionsJobPage(List<Job> jobList, List<Conditions> filter, Pageable pageable) {
        List<Job> jobs = new ArrayList<>();
        for (Job job : jobList) {
            // 상태가 일치하는 공정만 필터링
            if (filter.contains(job.getConditions())) {
                jobs.add(job);
            }
        }
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > jobs.size() ? jobs.size() : (start + pageable.getPageSize());
        return new PageImpl<>(jobs.subList(start, end), pageable, jobs.size());
    }

    public ApiResponse<IssueInfoResponse> getIssueInfo(Long issueId) {
        // 이슈상세조회
        Issue issue = this.issueRepository.getIssue(issueId)
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_ISSUE));
        Job job = issue.getJob();
        IssueInfoResponse issueInfoResponse = null;
        if (Objects.isNull(job)) {
            // global issue
            log.debug("JOB is NULL");
            issueInfoResponse = IssueInfoResponse.builder()
                    .issueId(issue.getId())
                    .reportedDate(Optional.of(issue).map(Issue::getUpdatedDate).orElseGet(() -> DEFATUL_LOCAL_DATE_TIME))
                    .photoFilePath(Optional.of(issue).map(Issue::getPath).orElseGet(() -> ""))
                    .workerUUID(issue.getWorkerUUID())
                    .caption(Optional.of(issue).map(Issue::getContent).orElseGet(() -> ""))
                    .build();
        } else {
            // job issue
            log.debug("JOB is NOT!! NULL");
            SubProcess subProcess = Optional.ofNullable(job).map(Job::getSubProcess).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SUBPROCESS));
            Process process = Optional.ofNullable(subProcess).map(SubProcess::getProcess).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
            issueInfoResponse = IssueInfoResponse.builder()
                    .processId(process.getId())
                    .processName(process.getName())
                    .subProcessId(subProcess.getId())
                    .subProcessName(subProcess.getName())
                    .jobId(job.getId())
                    .jobName(job.getName())
                    .issueId(issue.getId())
                    .reportedDate(subProcess.getReportedDate())
                    // TODO : user info
                    .workerUUID(subProcess.getWorkerUUID())
                    .photoFilePath(issue.getPath())
                    .caption(issue.getContent())
                    .build();
        }
        return new ApiResponse<>(issueInfoResponse);
    }

    public ApiResponse<ReportInfoResponse> getReportInfo(Long reportId) {
        // 리포트상세조회
        Report report = this.reportRepository.getReport(reportId)
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_REPORT));
        Job job = Optional.of(report).map(Report::getJob).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_JOB));
        SubProcess subProcess = Optional.ofNullable(job).map(Job::getSubProcess).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SUBPROCESS));
        Process process = Optional.ofNullable(subProcess).map(SubProcess::getProcess).orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
        ReportInfoResponse reportInfoResponse = ReportInfoResponse.builder()
                .processId(process.getId())
                .processName(process.getName())
                .subProcessId(subProcess.getId())
                .subProcessName(subProcess.getName())
                .jobId(job.getId())
                .jobName(job.getName())
                .reportId(report.getId())
                .reportedDate(subProcess.getReportedDate())
                // TODO : user info
                .workerUUID(subProcess.getWorkerUUID())
                .reportItems(report.getItemList().stream().map(item -> {
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

    @Transactional
    public ApiResponse<ProcessSimpleResponse> deleteTheProcess(Long processId) {
        // 공정 삭제
        Process process = this.processRepository.findById(processId)
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));

        // 공정삭제 후 공정과 연결된 컨텐츠의 상태 변경
        ContentStatusChangeRequest contentStatusChangeRequest = new ContentStatusChangeRequest();
        contentStatusChangeRequest.setContentUUID(process.getAruco().getContentUUID());
        contentStatusChangeRequest.setStatus("WAIT");

        this.contentRestService.changeContentStatus(contentStatusChangeRequest);

        // TODO : 공정 삭제시 히스토리를 남기고 상태값만 바꾼다면, 이슈, 리포트 등 작업 하위의 아이템들을 어떻게 할 것인지 확인해야 함.
        this.processRepository.delete(process);
        return new ApiResponse<>(new ProcessSimpleResponse(true));
    }

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
     * 공정조회시 세부공정에 할당되어 있는 작업자들의 목록을 조회
     *
     * @param process
     * @return
     */
    private List<SubProcessAssignedResponse> getSubProcessesAssign(Process process) {
        // 작업자 목록 조회
        List<SubProcessAssignedResponse> subProcessesAssign = new ArrayList<>();
        for (SubProcess subProcess : process.getSubProcessList()) {
            subProcessesAssign.add(SubProcessAssignedResponse.builder()
                    .subProcessId(subProcess.getId())
                    .workerUUID(subProcess.getWorkerUUID())
                    // TODO : 작업자들의 프로필 추가 필요
                    .profile("")
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
        List<Process> processes = this.processRepository.getProcesses();
        int totalRate = 0, totalProcesses = 0;
        for (Process process : processes) {
            totalRate = totalRate + process.getProgressRate();
            totalProcesses++;
        }

        // 공정률 총계
        totalRate = totalProcesses == 0 ? 0 : totalRate / totalProcesses;

        // 일자별 총계 엔티티생성
        DailyTotal dailyTotal = DailyTotal.builder()
                .totalRate(totalRate)
                .totalCountProcesses(totalProcesses)
                .build();

        this.dailyTotalRepository.save(dailyTotal);
    }

    public ApiResponse<ProcessTargetInfoResponse> getProcessInfoByTarget(long targetId) {
        Process process = this.processRepository.findByArucoIdAndState(targetId, State.CREATED)
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));
        ProcessTargetInfoResponse targetInfoResponse = this.modelMapper.map(process, ProcessTargetInfoResponse.class);
        return new ApiResponse<>(targetInfoResponse);
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

    public ApiResponse<CountSubProcessOnWorkerResponse> getCountSubProcessOnWorker(String workerUUID) {
        List<SubProcess> subProcesses = this.subProcessRepository.findByWorkerUUID(workerUUID);
        int ing = 0;
        for (SubProcess subProcess : subProcesses) {
            State state = subProcess.getProcess().getState();
            // 공정상태가 종료 또는 삭제가 아니고 그리고 세부공정상태가 대기상태가 아닐 때
            if ((state != State.CLOSED || state != State.DELETED) && subProcess.getConditions() != Conditions.WAIT)
                ing++;
        }
        CountSubProcessOnWorkerResponse response = CountSubProcessOnWorkerResponse.builder()
                .workerUUID(workerUUID)
                .countAssigned(subProcesses.size())
                .countProgressing(ing)
                .build();
        return new ApiResponse<>(response);
    }
}
