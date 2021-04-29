package com.virnect.content.application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.querydsl.core.Tuple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.application.license.LicenseRestService;
import com.virnect.content.application.process.ProcessRestService;
import com.virnect.content.application.user.UserRestService;
import com.virnect.content.application.workspace.WorkspaceRestService;
import com.virnect.content.dao.TypeRepository;
import com.virnect.content.dao.content.ContentRepository;
import com.virnect.content.dao.contentdonwloadlog.ContentDownloadLogRepository;
import com.virnect.content.dao.scenegroup.SceneGroupRepository;
import com.virnect.content.dao.target.TargetRepository;
import com.virnect.content.domain.Content;
import com.virnect.content.domain.SceneGroup;
import com.virnect.content.domain.Target;
import com.virnect.content.domain.TargetType;
import com.virnect.content.domain.Types;
import com.virnect.content.domain.YesOrNo;
import com.virnect.content.dto.MetadataInfo;
import com.virnect.content.dto.request.ContentDeleteRequest;
import com.virnect.content.dto.request.ContentInfoRequest;
import com.virnect.content.dto.request.ContentUpdateRequest;
import com.virnect.content.dto.request.ContentUploadRequest;
import com.virnect.content.dto.response.ContentCountResponse;
import com.virnect.content.dto.response.ContentDeleteListResponse;
import com.virnect.content.dto.response.ContentDeleteResponse;
import com.virnect.content.dto.response.ContentInfoListResponse;
import com.virnect.content.dto.response.ContentInfoResponse;
import com.virnect.content.dto.response.ContentPropertiesResponse;
import com.virnect.content.dto.response.ContentResourceUsageInfoResponse;
import com.virnect.content.dto.response.ContentSecessionResponse;
import com.virnect.content.dto.response.ContentStatisticResponse;
import com.virnect.content.dto.response.ContentTargetResponse;
import com.virnect.content.dto.response.ContentUploadResponse;
import com.virnect.content.dto.response.SceneGroupInfoListResponse;
import com.virnect.content.dto.response.SceneGroupInfoResponse;
import com.virnect.content.dto.rest.LicenseInfoResponse;
import com.virnect.content.dto.rest.MemberInfoDTO;
import com.virnect.content.dto.rest.MemberListResponse;
import com.virnect.content.dto.rest.MyLicenseInfoListResponse;
import com.virnect.content.dto.rest.MyLicenseInfoResponse;
import com.virnect.content.dto.rest.ProcessInfoResponse;
import com.virnect.content.dto.rest.UserInfoListResponse;
import com.virnect.content.dto.rest.UserInfoResponse;
import com.virnect.content.dto.rest.WorkspaceInfoListResponse;
import com.virnect.content.dto.rest.WorkspaceInfoResponse;
import com.virnect.content.event.ContentUpdateFileRollbackEvent;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.common.PageMetadataResponse;
import com.virnect.content.global.error.ErrorCode;
import com.virnect.content.global.util.AES256EncryptUtils;
import com.virnect.content.global.util.QRcodeGenerator;
import com.virnect.content.infra.file.download.FileDownloadService;
import com.virnect.content.infra.file.upload.FileUploadService;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-15
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {
	private static final String ARES_FILE_EXTENSION = ".Ares";
	private static final YesOrNo INIT_IS_SHARED = YesOrNo.NO;
	private static final YesOrNo INIT_IS_CONVERTED = YesOrNo.NO;
	private static final YesOrNo INIT_IS_DELETED = YesOrNo.NO;
	private static final Long MEGA_BYTE = 1024L * 1024L;
	private final FileUploadService fileUploadService;
	private final FileDownloadService fileDownloadService;
	private final ContentRepository contentRepository;
	private final SceneGroupRepository sceneGroupRepository;
	private final TargetRepository targetRepository;
	private final TypeRepository typeRepository;
	private final ContentDownloadLogRepository contentDownloadLogRepository;
	private final UserRestService userRestService;
	private final ProcessRestService processRestService;
	private final WorkspaceRestService workspaceRestService;
	private final LicenseRestService licenseRestService;
	private final MetadataService metadataService;
	private final ModelMapper modelMapper;
	private final ObjectMapper objectMapper;
	private final ApplicationEventPublisher eventPublisher;
	private final Gson gson;
	/*@Value("${upload.dir}")
	private String uploadPath;*/
	@Value("${file.upload-path}")
	private String fileUploadPath;

	//@Value("${file.url}")
	//private String fileUploadUrl;

	private String defaultVTarget = "virnect_target.png";

	/**
	 * 콘텐츠 업로드
	 * 컨텐츠UUID는 컨텐츠를 관리하기 위한 서버에서의 식별자이면서 동시에 파일명
	 * 타겟데이터는 컨텐츠를 뷰에서 인식하여 컨텐츠를 다운로드하거나 정보를 가져오기 위한 키.
	 *
	 * @param uploadRequest - 콘텐츠 업로드 요청 데이터
	 * @return - 업로드된 콘텐츠 정보
	 */
	@Transactional
	public ApiResponse<ContentUploadResponse> contentUpload(final ContentUploadRequest uploadRequest) {
		/**
		 * 1.   콘텐츠 업로드 파일 저장
		 * 2-1. 프로퍼티 메타데이터 생성
		 * 2-2. 업로드 컨텐츠 정보 수집
		 * 3.   컨텐츠 씬그룹 관련 정보 파싱 및 컨텐츠 정보에 추가
		 * 3-1. 메타데이터 파싱
		 * 3-2. 씬그룹 데이터 파싱
		 * 3-3. 씬그룹 데이터 컨텐츠에 추가
		 * 4.   업로드 요청 컨텐츠 정보 저장
		 */

		String workspaceUUID = uploadRequest.getWorkspaceUUID();
		Long contentSize = uploadRequest.getContent().getSize();

		LicenseInfoResponse licenseInfoResponse = checkLicenseStorage(
			workspaceUUID, contentSize, uploadRequest.getUserUUID());

		// 1. 콘텐츠 업로드 파일 저장
		try {
			String contentUUID = UUID.randomUUID().toString();

			log.info("CONTENT UPLOAD - contentUUID : {}, request : {}", contentUUID, uploadRequest.toString());

			// 파일명은 컨텐츠 식별자(contentUUID)와 동일
			String fileUploadPath = this.fileUploadService.uploadByFileInputStream(
				uploadRequest.getContent(), contentUUID + "");

			// 2-1. 프로퍼티로 메타데이터 생성
			MetadataInfo metadataInfo = metadataService.convertMetadata(
				uploadRequest.getProperties(), uploadRequest.getUserUUID(), uploadRequest.getName());
			String metadata = gson.toJson(metadataInfo);

			// 2-2. 업로드 컨텐츠 정보 수집
			Content content = Content.builder()
				// TODO : 유효한 워크스페이스 인지 검증 필요.
				.workspaceUUID(uploadRequest.getWorkspaceUUID())
				.uuid(contentUUID)
				.name(uploadRequest.getName())
				.metadata(metadata)
				.properties(uploadRequest.getProperties())
				.userUUID(uploadRequest.getUserUUID())
				.shared(INIT_IS_SHARED)
				.converted(INIT_IS_CONVERTED)
				.deleted(INIT_IS_DELETED)
				.size(uploadRequest.getContent().getSize())
				.path(fileUploadPath)
				.build();

			// 3. 컨텐츠 씬그룹 관련 정보 파싱 및 컨텐츠 정보에 추가
			addSceneGroupToContent(content, content.getMetadata());

			// 타겟 저장 후 타겟데이터 반환
			String targetData = null;

			// contentDuplicate의 경우 targetData가 없을 수 있으므로 체크한다.
			if (Objects.nonNull(uploadRequest.getTargetData())) {
				// 이미 있는 타겟 데이터인지 체크
				if (isExistTargetData(uploadRequest.getTargetData())) {
					throw new ContentServiceException(ErrorCode.ERR_TARGET_DATA_ALREADY_EXIST);
				} else {
					targetData = addTargetToContent(
						content, uploadRequest.getTargetType(), uploadRequest.getTargetData());
				}
			}

			// 4. 업로드 요청 컨텐츠 정보 저장
			this.contentRepository.save(content);

			ContentUploadResponse result = this.modelMapper.map(content, ContentUploadResponse.class);

			result.setLicenseInfo(licenseInfoResponse);

			List<Target> targets = content.getTargetList();
			List<ContentTargetResponse> contentTargetResponseList = targets.stream()
				.map(target -> this.modelMapper.map(target, ContentTargetResponse.class))
				.collect(Collectors.toList());

			// 반환할 타겟정보
			result.setTargets(contentTargetResponseList);
			result.setContentUUID(contentUUID);

			log.info("[RESPONSE LOGGER] :: [{}]", result.toString());
			return new ApiResponse<>(result);
		} catch (IOException e) {
			log.info("CONTENT UPLOAD ERROR: {}", e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		}
	}

	/**
	 * 컨텐츠 객체에 씬그룹 추가
	 *
	 * @param content
	 * @param metadata
	 */
	private void addSceneGroupToContent(Content content, String metadata) {
		try {
			// 3-1. 메타데이터 파싱
			MetadataInfo metadataInfo = objectMapper.readValue(metadata, MetadataInfo.class);
			metadataInfo.getContents().getSceneGroups().forEach(sceneGroupDto -> {
				// 3-2. 씬그룹 데이터 파싱
				SceneGroup sceneGroup = SceneGroup.builder()
					.name(sceneGroupDto.getName())
					.jobTotal(sceneGroupDto.getJobTotal())
					.priority(sceneGroupDto.getPriority())
					.uuid(sceneGroupDto.getId())
					.build();
				// 3-3. 씬그룹 데이터 컨텐츠에 추가
				content.addSceneGroup(sceneGroup);
			});
		} catch (JsonProcessingException e) {
			log.info("SCENEGROUP UPLOAD ERROR: {}", e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		}
	}

	private String addTargetToContent(Content content, TargetType targetType, String targetData) {
		String imgPath = null;

		if (Objects.nonNull(targetData)) {
			if (targetType.equals(TargetType.QR)) {
				imgPath = decodeData(targetData);
			}
			if (targetType.equals(TargetType.VTarget)) {
				//imgPath = fileUploadUrl + fileUploadPath + defaultVTarget;
				imgPath = fileDownloadService.getFilePath(fileUploadPath, defaultVTarget);
			}
		}

		//content metadata 안의 targetsize 추출(VECHOSYS-1282)
		JsonParser jsonParse = new JsonParser();
		JsonObject propertyObj = (JsonObject)jsonParse.parse(content.getMetadata());
		JsonObject contents = propertyObj.getAsJsonObject("contents");
		float targetSize = contents.get("targetSize").getAsFloat();

		Target target = Target.builder()
			.type(targetType)
			.content(content)
			.data(targetData)
			.imgPath(imgPath)
			.size(targetSize)
			.build();

		content.addTarget(target);

		this.targetRepository.save(target);

		return targetData;
	}

	private Boolean isExistTargetData(String targetData) {
		Boolean flag = false;

		int targetCnt = this.targetRepository.countByData(targetData);

		if (targetCnt > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 콘텐츠 수정 요청 처리
	 *
	 * @param contentUUID   - 콘텐츠 고유 식별자
	 * @param updateRequest - 콘텐츠 수정 요청 데이터
	 * @return -  수정된 콘텐츠 정보
	 */
	@Transactional
	public ApiResponse<ContentUploadResponse> contentUpdate(
		final String contentUUID, final ContentUpdateRequest updateRequest
	) {
		// 1. 수정 대상 컨텐츠 데이터 조회
		Content targetContent = this.contentRepository.findByUuid(contentUUID)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_UPDATE));

		// 컨텐츠 소유자 확인
		// [오후 6:07] 강현석
		// 허지용님. 메이크에서 이미 업로드 되어 있는 컨텐츠에 업로드할 때 업로드한 사용자가 다르면 다음과 같은 리턴과 함께 업로드가 되지 않습니다.
		// "code":4015, "message":"An error occurred in the request. Because it is NOT ownership.
		// 혹시 업로드할 때 사용자의 확인을 해제하여 주실 수 있으신지요? 해당 부분에 관련하여 수환님과는 논의가 되었습니다.
		// 이런 이유로 컨텐츠 소유자 확인하지 않음.
		// if (!targetContent.getUserUUID().equals(updateRequest.getUserUUID()))
		//     throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);

		// 수정할 수 없는 조건(공유 상태 관련 논의 필요)
		// 2020/06/08 공유 상태 조건은 제거 (공유 상태 제한을 건 이유 :
		// [오전 10:23] 김수환
		// 네 ~ 승호님이 말씀하신 상태는 SMIC 사업때 작업 관리 중인 상태에서 콘텐츠 업데이트 시 문제 발생하여 막아두자고 해서 임의로 막아둔 것
		// 이여서 승호님 말씀대로 변경하는 것이 제품 적용에 맞는 방향입니다 (smile))
		if (targetContent.getConverted() != YesOrNo.NO || targetContent.getDeleted() != YesOrNo.NO) {
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_MANAGED);
		}

		// 기존 컨텐츠 크기와 수정하려는 컨텐츠의 크기를 뺀다.
		Long calSize = targetContent.getSize() - updateRequest.getContent().getSize();

		checkLicenseStorage(targetContent.getWorkspaceUUID(), calSize, updateRequest.getUserUUID());

		// 2. 저장된 파일 가져오기
		File oldContent = this.fileUploadService.getFile(targetContent.getPath());

		// 2. 기존 컨텐츠 파일 삭제
		this.fileUploadService.delete(targetContent.getPath());

		// 3. 수정 컨텐츠 저장
		try {
			String fileUploadPath = this.fileUploadService.uploadByFileInputStream(
				updateRequest.getContent(), targetContent.getUuid() + "");

			// 4 수정 컨텐츠 경로 반영
			targetContent.setPath(fileUploadPath);
		} catch (IOException e) {
			log.info("CONTENT UPDATE ERROR: {}", e.getMessage());
			// 3-1. Recover Deleted File.
			eventPublisher.publishEvent(new ContentUpdateFileRollbackEvent(oldContent));
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		}

		// 5. 컨텐츠 소유자 변경
		targetContent.setUserUUID(updateRequest.getUserUUID());

		// 6. 수정 컨텐츠 파일 크기 반영
		targetContent.setSize(updateRequest.getContent().getSize());
		// 7. 컨텐츠명 변경
		targetContent.setName(updateRequest.getName());

		// 8. 컨텐츠 메타데이터 변경 (업데이트 하려는 속성으로 메타데이터 생성)
		MetadataInfo metadataInfo = metadataService.convertMetadata(
			updateRequest.getProperties(), updateRequest.getUserUUID(), updateRequest.getName());
		String metadata = gson.toJson(metadataInfo);

		targetContent.setMetadata(metadata);

		// 속성 메타데이터 변경
		targetContent.setProperties(updateRequest.getProperties());

		// 8-1. 컨텐츠 씬그룹 수정
		targetContent.getSceneGroupList().clear();
		addSceneGroupToContent(targetContent, metadata);

		String targetData = updateRequest.getTargetData();

		// 해당 컨텐츠와 물려있는 타겟 정보를 찾음
		Target target = this.targetRepository.findByContentId(targetContent.getId())
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_NOT_FOUND_TARGET));

		// 기존 타겟 데이터가 없을 경우
		if (!StringUtils.hasText(target.getData())) {
			throw new ContentServiceException(ErrorCode.ERR_NOT_FOUND_TARGET);
		}

		if (!target.getData().equals(updateRequest.getTargetData())
			|| updateRequest.getTargetType() != target.getType()) {
			if (updateRequest.getTargetType().equals(TargetType.QR)) {
				String uploadImgPath = decodeData(updateRequest.getTargetData());
				fileUploadService.delete(target.getImgPath());
				target.setImgPath(uploadImgPath);
			}
			if (updateRequest.getTargetType().equals(TargetType.VTarget)) {
				String uploadImgPath = fileDownloadService.getFilePath(fileUploadPath, defaultVTarget);
				fileUploadService.delete(target.getImgPath());
				target.setImgPath(uploadImgPath);
			}
		}
		target.setData(updateRequest.getTargetData());
		target.setType(updateRequest.getTargetType());
		float targetSize = 10f;
		if (!targetContent.getMetadata().equals(updateRequest.getMetadata())) {
			JsonParser jsonParse = new JsonParser();
			JsonObject propertyObj = (JsonObject)jsonParse.parse(updateRequest.getMetadata());
			JsonObject contents = propertyObj.getAsJsonObject("contents");
			targetSize = contents.get("targetSize").getAsFloat();
		}
		target.setSize(targetSize);
		// 8. 수정 반영
		targetRepository.save(target);
		contentRepository.save(targetContent);
		// 반환할 타겟정보
		List<ContentTargetResponse> contentTargetResponseList = new ArrayList<>();
		ContentTargetResponse contentTargetResponse = ContentTargetResponse.builder()
			.type(updateRequest.getTargetType())
			.data(targetData)
			.build();
		contentTargetResponseList.add(contentTargetResponse);

		ContentUploadResponse updateResult = this.modelMapper.map(targetContent, ContentUploadResponse.class);

		updateResult.setTargets(contentTargetResponseList);

		return new ApiResponse<>(updateResult);
	}

	/**
	 * 콘텐츠 삭제 요청 처리
	 *
	 * @param contentDeleteRequest - 콘텐츠 고유 번호(배열), 컨텐츠를 생성한 사용자의 고유번호
	 * @return - 파일 삭제 결과
	 */
	@Transactional
	public ApiResponse<ContentDeleteListResponse> contentDelete(ContentDeleteRequest contentDeleteRequest) {
		final String[] contentUUIDs = contentDeleteRequest.getContentUUIDs();
		final String workspaceUUID = contentDeleteRequest.getWorkspaceUUID();

		List<ContentDeleteResponse> deleteResponseList = new ArrayList<>();
		for (String contentUUID : contentUUIDs) {
			// 1. 컨텐츠들 조회
			Content content = this.contentRepository.findByUuid(contentUUID)
				.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

			ContentDeleteResponse contentDeleteResponse = ContentDeleteResponse.builder()
				.workspaceUUID(content.getWorkspaceUUID())
				.contentUUID(content.getUuid())
				.contentName(content.getName())
				.shared(content.getShared())
				.uploaderUUID(content.getUserUUID())
				.converted(content.getConverted())
				.updatedDate(content.getUpdatedDate())
				.build();

			// 1-1 권한확인 - 권한이 맞지 않다면 continue. -> 기존에는 컨텐츠 관리자의 정보를 확인하여 삭제. 혹시 몰라 주석처리.
			// TODO : 관리자 관련 처리 되어있지 않음
			//            log.info("Content Delete : contentUploader {}, workerUUID {}", content.getUserUUID(), workerUUID);
			//            if (!content.getUserUUID().equals(workerUUID)) {
			//                contentDeleteResponse.setCode(ErrorCode.ERR_CONTENT_DELETE_OWNERSHIP.getCode());
			//                contentDeleteResponse.setMsg(ErrorCode.ERR_CONTENT_DELETE_OWNERSHIP.getMessage());
			//                contentDeleteResponse.setResult(false);
			//                deleteResponseList.add(contentDeleteResponse);
			//                continue;
			//            }

			// 1-1 권한확인 - 권한이 맞지 않다면 continue. -> 컨텐츠를 삭제하려는 워크스페이스UUID를 받아서 처리.
			log.info(
				"Content Delete : contentWorkspace -> {}, requestWorkspace -> {}", content.getWorkspaceUUID(),
				workspaceUUID
			);
			if (!content.getWorkspaceUUID().equals(workspaceUUID)) {
				contentDeleteResponse.setCode(ErrorCode.ERROR_WORKSPACE.getCode());
				contentDeleteResponse.setMsg(ErrorCode.ERROR_WORKSPACE.getMessage());
				contentDeleteResponse.setResult(false);
				deleteResponseList.add(contentDeleteResponse);
				continue;
			}

			// 1-2 삭제조건 확인 - 전환/공유/삭제 세가지 모두 아니어야 함.
			log.info(
				"Content Delete : getConverted {}, getShared {}, getDeleted {}", content.getConverted(),
				content.getShared(), content.getDeleted()
			);

			// 삭제 시 각각의 케이스를 나눔. (웹 쪽 다국어와 관련하여 errorcode 추가)
			// 작업 전환 여부
			if (YesOrNo.YES.equals(content.getConverted())) {
				contentDeleteResponse.setCode(ErrorCode.ERR_CONTENT_MANAGED.getCode());
				contentDeleteResponse.setMsg(ErrorCode.ERR_CONTENT_MANAGED.getMessage());
				contentDeleteResponse.setResult(false);
				deleteResponseList.add(contentDeleteResponse);
				continue;
			}

			// 컨텐츠 공유 여부
			if (YesOrNo.YES.equals(content.getShared())) {
				contentDeleteResponse.setCode(ErrorCode.ERR_CONTENT_DELETE_SHARED.getCode());
				contentDeleteResponse.setMsg(ErrorCode.ERR_CONTENT_DELETE_SHARED.getMessage());
				contentDeleteResponse.setResult(false);
				deleteResponseList.add(contentDeleteResponse);
				continue;
			}

			// 컨텐츠 논리 삭제 여부 (현재 해당 플래그는 사용하지 않음)
			if (YesOrNo.YES.equals(content.getDeleted())) {
				contentDeleteResponse.setCode(ErrorCode.ERR_CONTENT_MANAGED.getCode());
				contentDeleteResponse.setMsg(ErrorCode.ERR_CONTENT_MANAGED.getMessage());
				contentDeleteResponse.setResult(false);
				deleteResponseList.add(contentDeleteResponse);
				continue;
			}

			// 파일을 실제 삭제하지 않을 경우. 복구 프로세스가 필요할 수도 있어 일부 구현해 놓음.
			if (false) {
				// 2 컨텐츠 삭제 - 삭제여부 YES로 변경, 목록조회시 deleted가 YES인 것은 조회하지 않음.
				content.setDeleted(YesOrNo.YES);
				this.contentRepository.save(content);
			} else {
				// 2 컨텐츠 삭제
				long affectRows = this.contentRepository.deleteByUuid(content.getUuid());
				log.info("deleteByUuid affectRows = {}", affectRows);

				if (affectRows <= 0) {
					throw new ContentServiceException(ErrorCode.ERR_CONTENT_DELETE);
				}

				// 3 파일 존재 유무 확인 및 파일 삭제
				log.info("content.getPath() = {}", content.getPath());

				// NULL 체크
				if (this.fileUploadService.getFile(content.getPath()) != null) {
					if (this.fileUploadService.getFile(content.getPath()).exists()) {
						// 파일이 없다면 파일삭제는 무시함.
						boolean fileDeleteResult = this.fileUploadService.delete(content.getPath());

						if (!fileDeleteResult) {
							throw new ContentServiceException(ErrorCode.ERR_DELETE_CONTENT);
						}
					}
				}
			}
			// 5 삭제 성공 반환
			contentDeleteResponse.setMsg(ErrorCode.ERR_CONTENT_DELETE_SUCCEED.getMessage());
			contentDeleteResponse.setResult(true);
			deleteResponseList.add(contentDeleteResponse);
		}
		// 6 최종 결과 반환
		return new ApiResponse<>(new ContentDeleteListResponse(deleteResponseList));
	}

	/**
	 * 콘텐츠 목록 조회
	 *
	 * @param workspaceUUID - 워크스페이스 식별자
	 * @param userUUID      - 사용자 식별자
	 * @param search        - 검색어(컨텐츠명 / 사용자명)
	 * @param shared        - 공유여부
	 * @param pageable      - 페이징
	 * @return - 컨텐츠 목록
	 */
	@Transactional(readOnly = true)
	public ApiResponse<ContentInfoListResponse> getContentList(
		String workspaceUUID, String userUUID, String search, String shared, String converteds, Pageable pageable
	) {
		List<ContentInfoResponse> contentInfoList;
		Map<String, UserInfoResponse> userInfoMap = new HashMap<>();
		List<String> userUUIDList = new ArrayList<>();

		if (search != null) {
			// 1. 사용자 식별번호 조회
			ApiResponse<UserInfoListResponse> userInfoListResult = getUserInfo(search, workspaceUUID);

			//            ApiResponse<UserInfoListResponse> userInfoListResult = this.userRestService.getUserInfoSearch(search, false);

			UserInfoListResponse userInfoList = userInfoListResult.getData();
			log.info("GET USER INFO BY SEARCH KEYWORD: [{}]", userInfoList);

			userInfoList.getUserInfoList()
				.forEach(userInfoResponse -> userInfoMap.put(userInfoResponse.getUuid(), userInfoResponse));

			userUUIDList = userInfoList.getUserInfoList().stream()
				.map(UserInfoResponse::getUuid).collect(Collectors.toList());
			log.info("[{}]", userInfoList);
		}

		// 2. 콘텐츠 조회
		Page<Content> contentPage = this.contentRepository.getContent(
			workspaceUUID, userUUID, search, shared, converteds, userUUIDList, pageable);

		contentInfoList = contentPage.stream().map(content -> {
			List<ContentTargetResponse> targets = content.getTargetList().stream().map(target -> {
				ContentTargetResponse contentTargetResponse = ContentTargetResponse.builder()
					.id(target.getId())
					.data(target.getData())
					.type(target.getType())
					.imgPath(target.getImgPath())
					.build();
				return contentTargetResponse;
			}).collect(Collectors.toList());

			ContentInfoResponse contentInfoResponse = ContentInfoResponse.builder()
				.workspaceUUID(content.getWorkspaceUUID())
				.contentUUID(content.getUuid())
				.contentName(content.getName())
				.shared(content.getShared())
				.sceneGroupTotal(content.getSceneGroupList().size())
				.contentSize(content.getSize())
				.path(content.getPath())
				.converted(content.getConverted())
				.createdDate(content.getCreatedDate())
				.targets(targets)
				.build();

			if (userInfoMap.containsKey(content.getUserUUID())) {
				contentInfoResponse.setUploaderName(
					userInfoMap.get(content.getUserUUID()).getNickname());    // name -> nickname으로 변경
				contentInfoResponse.setUploaderUUID(userInfoMap.get(content.getUserUUID()).getUuid());
				contentInfoResponse.setUploaderProfile(userInfoMap.get(content.getUserUUID()).getProfile());
			} else {
				ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(
					content.getUserUUID());
				contentInfoResponse.setUploaderProfile(userInfoResponse.getData().getProfile());
				contentInfoResponse.setUploaderName(
					userInfoResponse.getData().getNickname());    // name -> nickname으로 변경
				contentInfoResponse.setUploaderUUID(userInfoResponse.getData().getUuid());
			}

			return contentInfoResponse;
		}).collect(Collectors.toList());
		PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.totalPage(contentPage.getTotalPages())
			.totalElements(contentPage.getTotalElements())
			.build();

		return new ApiResponse<>(new ContentInfoListResponse(contentInfoList, pageMetadataResponse));
	}

	/**
	 * 씬그룹 목록 가져오기
	 *
	 * @param contentUUID - 컨텐츠 UUID
	 * @return - 씬그룹리스트
	 */
	public ApiResponse<SceneGroupInfoListResponse> getContentSceneGroups(String contentUUID) {
		Content content = this.contentRepository.findByUuid(contentUUID)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

		List<SceneGroup> sceneGroupList = content.getSceneGroupList();

		if (sceneGroupList == null || sceneGroupList.isEmpty()) {
			return new ApiResponse<>(new SceneGroupInfoListResponse(content.getUuid(), new ArrayList<>()));
		}

		List<SceneGroupInfoResponse> sceneGroupInfoResponseList = content.getSceneGroupList().stream()
			.map(sceneGroup -> {
				SceneGroupInfoResponse sceneGroupInfo = new SceneGroupInfoResponse();
				sceneGroupInfo.setId(sceneGroup.getUuid());
				sceneGroupInfo.setJobTotal(sceneGroup.getJobTotal());
				sceneGroupInfo.setName(sceneGroup.getName());
				sceneGroupInfo.setPriority(sceneGroup.getPriority());
				return sceneGroupInfo;
			}).collect(Collectors.toList());
		return new ApiResponse<>(new SceneGroupInfoListResponse(content.getUuid(), sceneGroupInfoResponseList));
	}

	public ApiResponse<ContentInfoResponse> getContentInfo(String contentUUID) {
		Content content = this.contentRepository.findByUuid(contentUUID)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

		return getContentInfoResponseApiResponse(content);
	}

	@Transactional
	public ApiResponse<ContentInfoResponse> modifyContentInfo(
		final String contentUUID, ContentInfoRequest contentInfoRequest
	) {

		final YesOrNo shared = contentInfoRequest.getShared();
		final Types contentType = contentInfoRequest.getContentType();
		final String userUUID = contentInfoRequest.getUserUUID();

		Content content = this.contentRepository.findByUuid(contentUUID)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

		// 컨텐츠 소유자 확인
        /*
        2020.07.30 [오후 12:21] 이상백
        컨텐츠 상태 수정 -> 누가 올린지 신경 안 쓰고 가능
        로 인해서 컨텐츠 공유시에 소유자 체크하는 로직 제외함.
         */
		//if (!content.getUserUUID().equals(userUUID)) throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);

		// TODO : 제품 2.0 기능
		//        Type type = this.typeRepository.findByType(contentType).orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_NOT_FOUND_CONTENT_TYPE));
		//        content.setType(type);
		content.setShared(shared);

		this.contentRepository.save(content);

		return getContentInfoResponseApiResponse(content);
	}

	private ApiResponse<ContentInfoResponse> getContentInfoResponseApiResponse(Content content) {
		ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(
			content.getUserUUID());
		List<ContentTargetResponse> targetResponseList = new ArrayList<>();

		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> {}", content.getTargetList());
		for (Target target : content.getTargetList()) {
			ContentTargetResponse map = this.modelMapper.map(target, ContentTargetResponse.class);
			targetResponseList.add(map);
		}

		JsonParser jsonParse = new JsonParser();
		JsonObject metaData = (JsonObject)jsonParse.parse(content.getMetadata());
		JsonObject contents = metaData.getAsJsonObject("contents");
		Float targetSize = 10f;
		if (contents.get("targetSize") != null) {
			targetSize = contents.get("targetSize").getAsFloat();
		}

		ContentInfoResponse contentInfoResponse = ContentInfoResponse.builder()
			.workspaceUUID(content.getWorkspaceUUID())
			.contentUUID(content.getUuid())
			.contentName(content.getName())
			.shared(content.getShared())
			.sceneGroupTotal(content.getSceneGroupList().size())
			.contentSize(content.getSize())
			.uploaderUUID(userInfoResponse.getData().getUuid())
			.uploaderName(userInfoResponse.getData().getNickname())    // name -> nickname으로 변경
			.uploaderProfile(userInfoResponse.getData().getProfile())
			.path(content.getPath())
			.converted(content.getConverted())
			.targets(targetResponseList)
			.createdDate(content.getCreatedDate())
			.targetSize(targetSize)
			.build();
		return new ApiResponse<>(contentInfoResponse);
	}

	/**
	 * 전체 콘텐츠 수 및 공정으로 등록된 콘텐츠 수 조회
	 *
	 * @return - 전체 콘텐츠 수 및 공정 등록 상태 콘텐츠 수
	 */
	public ApiResponse<ContentStatisticResponse> getContentStatusInfo() {
		long numberOfContents = this.contentRepository.count();
		long numberOfManagedContents = 0;
		long numberOfConvertedContents = this.contentRepository.countByConverted(YesOrNo.YES);
		long numberOfSharedContents = this.contentRepository.countByShared(YesOrNo.YES);
		long numberOfDeletedContents = this.contentRepository.countByDeleted(YesOrNo.YES);
		//        long numberOfManagedContents = this.contentRepository.countByStatus(ContentStatus.MANAGED);
		return new ApiResponse<>(
			new ContentStatisticResponse(numberOfContents, numberOfManagedContents, numberOfConvertedContents,
				numberOfSharedContents, numberOfDeletedContents
			));
	}

	@Transactional
	public ApiResponse<ContentUploadResponse> convertTaskToContent(Long taskId, String userUUID) {
		// 작업 가져오기
		ApiResponse<ProcessInfoResponse> response = this.processRestService.getProcessInfo(taskId);
		// 작업의 컨텐츠 식별자 가져오기
		String contentUUID = response.getData().getContentUUID();
		// 컨텐츠 가져오기
		Content content = this.contentRepository.findByUuid(contentUUID)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));
		// 컨텐츠 소유자 확인
		if (!content.getUserUUID().equals(userUUID))
			throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);

		ContentUploadRequest uploadRequest = ContentUploadRequest.builder()
			.workspaceUUID(content.getWorkspaceUUID())
			//.content(convertFileToMultipart(uploadPath.concat(contentUUID).concat(ARES_FILE_EXTENSION)))
			//.content(convertFileToMultipart(contentUUID.concat(ARES_FILE_EXTENSION)))
			.content(fileDownloadService.getMultipartfile(contentUUID.concat(ARES_FILE_EXTENSION)))
			// TODO : 공정 수정 후 반영 예정
			//                .contentType(content.getType().getType())
			.name(response.getData().getName())
			.metadata(content.getMetadata())
			.userUUID(content.getUserUUID())
			// 컨텐츠:타겟은 1:1이므로
			.targetType(content.getTargetList().get(0).getType())
			.build();

		return contentUpload(uploadRequest);
	}

	private MultipartFile convertFileToMultipart(String fileUrl) {
		File file = new File(fileUrl);

		// S3저장소를 쓰는 경우 S3에서 해당 ares파일을 다운받는다.
		fileDownloadService.copyFileS3ToLocal(file.getName());

		log.debug("MULTIPART FILE SOURCE - fileUrl: {}, path: {}", fileUrl, file.getPath());

		try {
			FileItem fileItem = new DiskFileItem(
				"targetFile", Files.probeContentType(file.toPath()), false, file.getName(), (int)file.length(),
				file.getParentFile()
			);
			try (InputStream inputStream = new FileInputStream(file)) {
				OutputStream outputStream = fileItem.getOutputStream();
				IOUtils.copy(inputStream, outputStream);
			}
			return new CommonsMultipartFile(fileItem);
		} catch (IOException e) {
			log.error("ERROR!! CONVERT FILE TO MULTIPARTFILE : {}", e.getMessage());
		}
		return null;
	}

	@Transactional
	public ApiResponse<ContentUploadResponse> contentDuplicate(
		final String contentUUID, final String workspaceUUID, final String userUUID
	) {
		// 컨텐츠 가져오기
		Content content = this.contentRepository.findByUuid(contentUUID)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

		// 컨텐츠의 워크스페이스 확인
		if (!content.getWorkspaceUUID().equals(workspaceUUID))
			throw new ContentServiceException(ErrorCode.ERROR_WORKSPACE);

		// 컨텐츠 소유자 확인
		if (!content.getUserUUID().equals(userUUID))
			throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);

		ContentUploadRequest uploadRequest = ContentUploadRequest.builder()
			.workspaceUUID(workspaceUUID)
			//.content(convertFileToMultipart(uploadPath.concat(contentUUID).concat(ARES_FILE_EXTENSION)))
			//.content(convertFileToMultipart(contentUUID.concat(ARES_FILE_EXTENSION)))
			.content(fileDownloadService.getMultipartfile(contentUUID.concat(ARES_FILE_EXTENSION)))
			// TODO : 공정 수정 후 반영 예정
			//                .contentType(content.getType().getType())
			.name(content.getName())
			.metadata(content.getMetadata())
			.properties(content.getProperties())
			.userUUID(userUUID)
			.build();

		//return contentUpload(uploadRequest);
        /*
        컨텐츠 복제를 통한 작업 생성의 경우 target정보를 작업서버에서 생성하게 되는데, 이때 기준이 되는 값(targetSize, targetType)을 프론트에서 받지 않고
        컨텐츠서버에서 받아 처리하기 위해 아래 코드를 수정함.(VECHOSYS-1282)
         */
		ContentUploadResponse contentUploadResponse = contentUpload(uploadRequest).getData();
		List<ContentTargetResponse> contentTargetResponseList = content.getTargetList().stream()
			.map(target -> this.modelMapper.map(target, ContentTargetResponse.class)).collect(Collectors.toList());

		contentUploadResponse.setTargets(contentTargetResponseList);
		return new ApiResponse<>(contentUploadResponse);

	}

	@Transactional(readOnly = true)
	public ApiResponse<ContentPropertiesResponse> getContentPropertiesMetadata(String contentUUID, String userUUID) {
		// 컨텐츠 조회
		Content content = this.contentRepository.findByUuid(contentUUID)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

		//        if (!content.getUserUUID().equals(userUUID))
		//            throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);
		workspaceMemberCheck(userUUID, content.getWorkspaceUUID());

		// 사용자 조회
		ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(
			content.getUserUUID());

		// 속성 메타데이터 저장
		return new ApiResponse<>(ContentPropertiesResponse.builder()
			.workspaceUUID(content.getWorkspaceUUID())
			.contentUUID(contentUUID)
			.contentName(content.getName())
			.shared(content.getShared())
			.sceneGroupTotal(content.getSceneGroupList().size())
			.contentSize(content.getSize())
			.uploaderUUID(content.getUserUUID())
			.uploaderName(userInfoResponse.getData().getNickname())    // name -> nickname으로 변경
			.uploaderProfile(userInfoResponse.getData().getProfile())
			.path(content.getPath())
			.converted(content.getConverted())
			.createdDate(content.getCreatedDate())
			.propertiesMetadata(content.getProperties())
			.build());
	}

	private void workspaceMemberCheck(String memberUUID, String contentWorkspaceUUID) {
		ApiResponse<WorkspaceInfoListResponse> workspaceInfoResponse = this.workspaceRestService.getMyWorkspaceInfoList(
			memberUUID);
		List<WorkspaceInfoResponse> workspaceInfoList = workspaceInfoResponse.getData().getWorkspaceList();
		boolean isWorkspaceMember = workspaceInfoList.stream()
			.anyMatch(workspaceInfo -> workspaceInfo.getUuid().equals(contentWorkspaceUUID));
		if (!isWorkspaceMember) {
			throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);
		}
	}

	private ApiResponse<UserInfoListResponse> getUserInfo(String search, String workspaceId) {
		ApiResponse<MemberListResponse> userList = workspaceRestService.getSimpleWorkspaceUserList(workspaceId);
		List<String> userUUIDs = new ArrayList<>();

		for (MemberInfoDTO dto : userList.getData().getMemberInfoList()) {
			userUUIDs.add(dto.getUuid());
		}

		ApiResponse<UserInfoListResponse> userInfoListResult = this.userRestService.getUserInfoSearchNickName(
			search, userUUIDs);

		return userInfoListResult;
	}

	public ApiResponse<Boolean> checkTargetData(String targetData) {
		Boolean isExist = false;

		String checkedData = checkParameterEncoded(targetData);

		int cntTargetData = this.targetRepository.countByData(checkedData);

		if (cntTargetData > 0) {
			isExist = true;
		}

		return new ApiResponse<>(isExist);
	}

	public ApiResponse<List<ContentCountResponse>> countMyContents(String workspaceUUID, List<String> userUUIDList) {

		List<Tuple> tupleList = this.contentRepository.countByUsers(workspaceUUID, userUUIDList);

		log.debug(">>>>> : {}", tupleList);

		List<ContentCountResponse> countList = tupleList.stream().map(tuple -> {
			ContentCountResponse response = new ContentCountResponse();

			response.setUserUUID(tuple.get(0, String.class));
			response.setCountContents(tuple.get(1, Long.class));

			return response;
		}).collect(Collectors.toList());

		return new ApiResponse<>(countList);
	}

	/**
	 * 라이선스 허용 최대 용량과 워크스페이스 기준 현재 총 용량 + 업로드 하는 콘텐츠의 용량을 비교
	 *
	 * @param workspaceUUID
	 * @param uploadContentSize
	 * @return
	 */
	private LicenseInfoResponse checkLicenseStorage(String workspaceUUID, Long uploadContentSize, String userUUID) {
		//업로드 사용자의 MAKE 라이선스 체크
		MyLicenseInfoListResponse myLicenseInfoListResponse = this.licenseRestService.getMyLicenseInfoRequestHandler(
			userUUID, workspaceUUID).getData();
		if (myLicenseInfoListResponse == null || myLicenseInfoListResponse.getLicenseInfoList() == null
			|| myLicenseInfoListResponse.getLicenseInfoList().isEmpty()) {
			log.error("[LICENSE CHECK] User License info is empty.");
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD_LICENSE_PRODUCT_NOT_FOUND);
		}

		boolean haveMakeLicense = false;
		for (MyLicenseInfoResponse myLicenseInfoResponse : myLicenseInfoListResponse.getLicenseInfoList()) {
			log.info(
				"[LICENSE CHECK] User License Info. workspaceUUID : [{}], userUUID : [{}], licenseProduct : [{}], licenseProductStatus : [{}]"
				, workspaceUUID, userUUID, myLicenseInfoResponse.getProductName(),
				myLicenseInfoResponse.getProductPlanStatus()
			);
			if (myLicenseInfoResponse.getProductName().equals("MAKE") && myLicenseInfoResponse.getProductPlanStatus()
				.equals("ACTIVE")) {
				haveMakeLicense = true;
			}

		}
		if (!haveMakeLicense) {
			log.error(
				"[LICENSE CHECK] User haven't active Make license. workspaceUUID : [{}], userUUID : [{}], haveMakeLicense : [{}]",
				workspaceUUID, userUUID, haveMakeLicense
			);
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD_LICENSE_PRODUCT_NOT_FOUND);
		}

		//용량 체크
		LicenseInfoResponse licenseInfoResponse = new LicenseInfoResponse();
		LicenseInfoResponse response = this.licenseRestService.getWorkspaceLicenseInfo(workspaceUUID).getData();
		// 업로드를 요청하는 워크스페이스를 기반으로 라이센스 서버의 최대 저장 용량을 가져온다. (MB 단위)
		Long maxCapacity = response.getMaxStorageSize();

		// 업로드를 요청하는 워크스페이스의 현재 총 용량을 가져온다. (byte 단위)
		Long workspaceCapacity = this.contentRepository.getWorkspaceStorageSize(workspaceUUID);

		if (Objects.isNull(workspaceCapacity)) {
			workspaceCapacity = 0L;
		}

		log.info("WorkspaceUUID : {}", workspaceUUID);
		log.info("WorkspaceMaxStorage : {}", workspaceCapacity);
		log.info("ContentSize : {}", uploadContentSize);

		// 워크스페이스 총 용량에 업로드 파일 용량을 더한다. (byte 단위)
		Long sumByteSize = workspaceCapacity + uploadContentSize;

		// byte를 MegaByte로 변환
		Long convertMB = sumByteSize / MEGA_BYTE;

		// 라이선스의 최대 용량이 0인 경우 업로드 프로세스를 수행하지 않는다.
		if (maxCapacity == 0) {
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD_LICENSE);
		}

		// 라이센스 서버의 최대 저장용량을 초과할 경우 업로드 프로세스를 수행하지 않는다.
		if (maxCapacity < convertMB) {
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD_LICENSE);
		} else {
			licenseInfoResponse.setMaxStorageSize(maxCapacity);
			licenseInfoResponse.setWorkspaceStorage(workspaceCapacity);
			licenseInfoResponse.setUploadSize(uploadContentSize);
			licenseInfoResponse.setUsableCapacity(maxCapacity - convertMB);
		}

		return licenseInfoResponse;
	}

	/**
	 * 라이선스 총 다운로드 수와 워크스페이스 기준 총 다운로드 수를 비교
	 *
	 * @param workspaceUUID
	 * @return
	 */
	protected LicenseInfoResponse checkLicenseDownload(String workspaceUUID) {

		LicenseInfoResponse licenseInfoResponse = new LicenseInfoResponse();

		// 라이센스 총 다운로드 횟수
		Long maxDownload = this.licenseRestService.getWorkspaceLicenseInfo(workspaceUUID).getData().getMaxDownloadHit();

		// 현재 워크스페이스의 다운로드 횟수
		Long sumDownload = this.contentRepository.getWorkspaceDownload(workspaceUUID);

		if (maxDownload == 0) {
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD_LICENSE);
		}

		// 워크스페이스 기준으로 처음 다운로드 받을 경우의 처리
		if (Objects.isNull(sumDownload)) {
			sumDownload = 0L;
		}

		if (maxDownload < sumDownload + 1) {
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD_LICENSE);
		} else {
			licenseInfoResponse.setMaxDownloadHit(maxDownload);
			licenseInfoResponse.setWorkspaceDownloadHit(sumDownload);
			licenseInfoResponse.setUsableDownloadHit(maxDownload - sumDownload);
		}

		return licenseInfoResponse;
	}

	public String decodeData(String encodeURL) {
		String imgPath = "";

		try {
			String decoder = URLDecoder.decode(encodeURL, "UTF-8");

			log.debug("{}", decoder);

			String targetData = AES256EncryptUtils.decryptByBytes("virnect", decoder);

			log.debug("{}", targetData);

			imgPath = getImgPath(targetData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return imgPath;
	}

	private String getImgPath(String targetData) {

		String qrString = "";

		try {
			BufferedImage qrImage = QRcodeGenerator.generateQRCodeImage(targetData, 256, 256);

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
	 * get방식에서 URLEncode된 값을 pathVariable로 받을 때 URLEncoding이 풀려서 오는 케이스를 체크.
	 *
	 * @param targetData
	 * @return
	 */
	public String checkParameterEncoded(String targetData) {
		String encodedData = null;

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

		return encodedData;
	}

	@Transactional(readOnly = true)
	public ApiResponse<ContentResourceUsageInfoResponse> getContentResourceUsageInfo(
		String workspaceId, LocalDateTime startDate, LocalDateTime endDate
	) {
		long storageUsage = contentRepository.calculateTotalStorageAmountByWorkspaceId(workspaceId);
		long downloadHit = contentDownloadLogRepository.calculateResourceUsageAmountByWorkspaceIdAndStartDateAndEndDate(
			workspaceId, startDate, endDate);
		return new ApiResponse<>(
			new ContentResourceUsageInfoResponse(workspaceId, storageUsage, downloadHit, LocalDateTime.now()));
	}

	/**
	 * 워크스페이스에 올라간 모든 컨텐츠 정보 삭제 처리
	 * @param workspaceUUID - 워크스페이스 식별자
	 * @return
	 */
	@Transactional
	public ContentSecessionResponse deleteAllContentInfo(String workspaceUUID) {
		List<Content> contentList = contentRepository.findByWorkspaceUUID(workspaceUUID);

		// 1. Content download log 삭제
		contentDownloadLogRepository.deleteAllContentDownloadLogByWorkspaceUUID(workspaceUUID);

		// 2. Scene Group 삭제
		sceneGroupRepository.deleteAllSceneGroupInfoByContent(contentList);

		// 3. Target 삭제
		targetRepository.deleteAllTargetInfoByContent(contentList);

		// 4. Content File 삭제
		contentList.parallelStream().forEach(content -> fileUploadService.delete(content.getPath()));

		// 4. Content 삭제
		contentRepository.deleteAllContentByWorkspaceUUID(workspaceUUID);

		return new ContentSecessionResponse(workspaceUUID, true, LocalDateTime.now());
	}

	@Transactional
	public ApiResponse<ContentInfoResponse> setConverted(final String contentUUID, final YesOrNo converted) {
		Content content = this.contentRepository.findByUuid(contentUUID)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

		content.setConverted(converted);

		this.contentRepository.save(content);

		return getContentInfoResponseApiResponse(content);
	}
}