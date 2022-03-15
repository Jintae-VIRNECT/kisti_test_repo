package com.virnect.serviceserver.serviceremote.application;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.imgscalr.Scalr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.account.AccountRestService;
import com.virnect.data.application.license.LicenseRestService;
import com.virnect.data.dao.file.FileRepository;
import com.virnect.data.dao.file.RecordFileRepository;
import com.virnect.data.dao.member.MemberRepository;
import com.virnect.data.dao.room.RoomRepository;
import com.virnect.data.domain.file.File;
import com.virnect.data.domain.file.FileConvertStatus;
import com.virnect.data.domain.file.FileType;
import com.virnect.data.domain.file.RecordFile;
import com.virnect.data.domain.member.Member;
import com.virnect.data.domain.room.Room;
import com.virnect.data.dto.FileUploadResult;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.data.dto.UploadResult;
import com.virnect.data.dto.request.file.FileUploadRequest;
import com.virnect.data.dto.request.file.RecordFileUploadRequest;
import com.virnect.data.dto.request.file.RoomProfileUpdateRequest;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.response.file.FileDeleteResponse;
import com.virnect.data.dto.response.file.FileDetailInfoListResponse;
import com.virnect.data.dto.response.file.FileDetailInfoResponse;
import com.virnect.data.dto.response.file.FileInfoListResponse;
import com.virnect.data.dto.response.file.FileInfoResponse;
import com.virnect.data.dto.response.file.FilePreSignedResponse;
import com.virnect.data.dto.response.file.FileStorageCheckResponse;
import com.virnect.data.dto.response.file.FileStorageInfoResponse;
import com.virnect.data.dto.response.file.FileUploadResponse;
import com.virnect.data.dto.response.file.FileUserInfoResponse;
import com.virnect.data.dto.response.file.RoomProfileUpdateResponse;
import com.virnect.data.dto.response.file.ShareFileInfoListResponse;
import com.virnect.data.dto.response.file.ShareFileInfoResponse;
import com.virnect.data.dto.response.file.ShareFileUploadResponse;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.dto.rest.WorkspaceLicensePlanInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RemoteServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.global.config.AsyncTaskService;
import com.virnect.data.global.util.paging.PagingUtils;
import com.virnect.data.infra.file.IFileManagementService;
import com.virnect.data.infra.utils.FileUtil;
import com.virnect.data.infra.utils.JsonUtil;
import com.virnect.serviceserver.servicedashboard.dto.response.FileBufferImageResponse;
import com.virnect.serviceserver.serviceremote.dto.mapper.file.FileInfoMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.file.FileUploadMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.file.FileUserInfoMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.file.RecordFileDetailMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.file.RecordFileMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.file.ShareFileInfoMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.file.ShareUploadFileMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

	private static final int THUMBNAIL_WIDTH = 120;
	private static final int THUMBNAIL_HEIGHT = 70;
	private static final int EXPIRY = 60 * 60 * 24;
	private static final String GLTF_EXTENSION = ".gltf";
	private static final String BUFFER_BIN_FILE = "buffer0.bin";

	private final IFileManagementService fileManagementService;
	private final AccountRestService accountRestService;
	private final LicenseRestService licenseRestService;

	private final FileRepository fileRepository;
	private final RoomRepository roomRepository;
	private final RecordFileRepository recordFileRepository;
	private final MemberRepository memberRepository;

	private final FileUploadMapper fileUploadMapper;
	private final RecordFileMapper recordFileMapper;
	private final FileInfoMapper fileInfoMapper;
	private final FileUserInfoMapper fileUserInfoMapper;
	private final RecordFileDetailMapper recordFileDetailMapper;
	private final ShareUploadFileMapper shareUploadFileMapper;
	private final ShareFileInfoMapper shareFileInfoMapper;

	private List<String> shareFileAllowExtensionList = null;
	private List<String> objectFileAllowExtensionList = null;

	private final FileUtil fileUtil;

	private final AsyncTaskService asyncTaskService;

	@PostConstruct
	private void init() {
		try {
			shareFileAllowExtensionList = new ArrayList<>();
			objectFileAllowExtensionList = new ArrayList<>();
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("policy/shareFileExtension.json");

			JsonUtil jsonUtil = new JsonUtil();
			JsonObject jsonObject = jsonUtil.fromInputStreamToJsonObject(inputStream);

			ObjectMapper objectMapper = new ObjectMapper();
			JsonObject resourceObject = jsonObject.getAsJsonObject("resource");
			List<String> docPDFList = objectMapper.readValue(
				resourceObject.getAsJsonArray("document_pdf").toString(), new TypeReference<List<String>>() {
				});
			shareFileAllowExtensionList.addAll(docPDFList);

			List<String> imageList = objectMapper.readValue(
				resourceObject.getAsJsonArray("image").toString(), new TypeReference<List<String>>() {
				});
			shareFileAllowExtensionList.addAll(imageList);

			List<String> file3dList = objectMapper.readValue(
				resourceObject.getAsJsonArray("file_3d").toString(), new TypeReference<List<String>>() {
				});
			objectFileAllowExtensionList.addAll(file3dList);

			inputStream.close();
		} catch (Exception e) {
			log.info("shareFileAllowExtensionList init fail");
		}
	}

	private String generateDirPath(String workspaceId, String sessionId) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("workspace")
			.append("/")
			.append(workspaceId)
			.append("/")
			.append("remote")
			.append("/")
			.append(sessionId)
			.append("/");
		return stringBuilder.toString();
	}

	@Transactional
	public FileUploadResponse uploadFile(FileUploadRequest fileUploadRequest) {

		// Storage check
		FileStorageCheckResponse storageCheckResult = checkStorageCapacity(fileUploadRequest.getWorkspaceId());
		if (storageCheckResult.getErrorCode() != ErrorCode.ERR_SUCCESS) {
			throw new RemoteServiceException(storageCheckResult.getErrorCode());
		}

		String objectName = null;
		try {
			UploadResult uploadResult = fileManagementService.upload(
				fileUploadRequest.getFile(),
				generateDirPath(fileUploadRequest.getWorkspaceId(), fileUploadRequest.getSessionId()),    // bucket path
				FileType.FILE
			);
			switch (uploadResult.getErrorCode()) {
				case ERR_FILE_ASSUME_DUMMY:
				case ERR_FILE_UNSUPPORTED_EXTENSION:
				case ERR_FILE_SIZE_LIMIT:
					throw new RemoteServiceException(uploadResult.getErrorCode());
				case ERR_SUCCESS:
					objectName = uploadResult.getResult();
					break;
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			throw new RemoteServiceException(ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
		}

		File file = buildFile(fileUploadRequest, objectName, FileType.FILE);
		if (ObjectUtils.isEmpty(fileRepository.save(file))) {
			throw new RemoteServiceException(ErrorCode.ERR_FILE_UPLOAD_FAILED);
		}

		FileUploadResponse fileUploadResponse = fileUploadMapper.toDto(file);
		fileUploadResponse.setUsedStoragePer(storageCheckResult.getUsedStoragePer());
		return fileUploadResponse;
	}

	@Transactional
	public FileUploadResponse uploadRecordFile(RecordFileUploadRequest recordFileUploadRequest) {

		String objectName = null;
		try {
			UploadResult uploadResult = fileManagementService.upload(
				recordFileUploadRequest.getFile(),
				generateDirPath(recordFileUploadRequest.getWorkspaceId(), recordFileUploadRequest.getSessionId()),
				// bucket path
				FileType.RECORD
			);

			switch (uploadResult.getErrorCode()) {
				case ERR_FILE_ASSUME_DUMMY:
				case ERR_FILE_UNSUPPORTED_EXTENSION:
				case ERR_FILE_SIZE_LIMIT:
					throw new RemoteServiceException(uploadResult.getErrorCode());
				case ERR_SUCCESS:
					objectName = uploadResult.getResult();
					break;
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			throw new RemoteServiceException(ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
		}

		RecordFile recordFile = buildRecordFile(recordFileUploadRequest, objectName);
		if (ObjectUtils.isEmpty(recordFileRepository.save(recordFile))) {
			throw new RemoteServiceException(ErrorCode.ERR_FILE_UPLOAD_FAILED);
		}

		return recordFileMapper.toDto(recordFile);
	}

	@Transactional
	public RoomProfileUpdateResponse profileUpload(
		String workspaceId,
		String sessionId,
		RoomProfileUpdateRequest roomProfileUpdateRequest
	) {

		// Storage check
		FileStorageCheckResponse storageCheckResult = checkStorageCapacity(workspaceId);
		if (storageCheckResult.getErrorCode() != ErrorCode.ERR_SUCCESS) {
			throw new RemoteServiceException(storageCheckResult.getErrorCode());
		}

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

		if (!room.getLeaderId().equals(roomProfileUpdateRequest.getUuid())) {
			throw new RemoteServiceException(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
		}

		String profileUrl = IFileManagementService.DEFAULT_ROOM_PROFILE;
		if (!ObjectUtils.isEmpty(roomProfileUpdateRequest.getProfile())) {
			try {
				UploadResult uploadResult = fileManagementService.uploadProfile(
					roomProfileUpdateRequest.getProfile(),
					generateDirPath(workspaceId, sessionId)
				);
				switch (uploadResult.getErrorCode()) {
					case ERR_FILE_ASSUME_DUMMY:
					case ERR_FILE_UNSUPPORTED_EXTENSION:
					case ERR_FILE_SIZE_LIMIT:
						throw new RemoteServiceException(uploadResult.getErrorCode());
					case ERR_SUCCESS:
						profileUrl = uploadResult.getResult();
						break;
				}
				fileManagementService.deleteProfile(room.getProfile());
			} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
				log.info("{}", exception.getMessage());
				throw new RemoteServiceException(ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
			}
		}

		RoomProfileUpdateResponse roomProfileUpdateResponse = new RoomProfileUpdateResponse();
		roomProfileUpdateResponse.setSessionId(sessionId);
		roomProfileUpdateResponse.setProfile(profileUrl);
		roomProfileUpdateResponse.setUsedStoragePer(storageCheckResult.getUsedStoragePer());
		room.setProfile(profileUrl);

		File file = buildProfileFile(roomProfileUpdateRequest, workspaceId, sessionId, profileUrl);
		if (ObjectUtils.isEmpty(roomRepository.save(room)) || ObjectUtils.isEmpty(fileRepository.save(file))) {
			throw new RemoteServiceException(ErrorCode.ERR_PROFILE_UPLOAD_FAILED);
		}

		return roomProfileUpdateResponse;
	}

	@Transactional
	public ResultResponse deleteProfile(
		String workspaceId,
		String sessionId
	) {

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

		try {
			fileManagementService.deleteProfile(room.getProfile());
			room.setProfile(IFileManagementService.DEFAULT_ROOM_PROFILE);
			roomRepository.save(room);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			exception.printStackTrace();
		}

		return ResultResponse.builder()
			.result(true)
			.build();
	}

	@Transactional(readOnly = true)
	public FilePreSignedResponse downloadFileUrl(
		String workspaceId,
		String sessionId,
		String userId,
		String objectName
	) {

		File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_FILE_NOT_FOUND));

		log.info("file download: {}", file.getObjectName());
		try {
			String url = fileManagementService.filePreSignedUrl(
				generateDirPath(workspaceId, sessionId), // bucket path
				objectName,
				EXPIRY,
				file.getName(),
				file.getFileType()
			);
			return FilePreSignedResponse.builder()
				.workspaceId(file.getWorkspaceId())
				.sessionId(file.getSessionId())
				.name(file.getName())
				.objectName(file.getObjectName())
				.contentType(file.getContentType())
				.fileType(file.getFileType())
				.fileConvertStatus(file.getFileConvertStatus())
				.url(url)
				.expiry(EXPIRY)
				.build();
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			throw new RemoteServiceException(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
	}

	@Transactional(readOnly = true)
	public String downloadThumbnailFileUrl(File targetFile) {
		log.info("file download: {}", targetFile.getObjectName());
		try {
			return
				fileManagementService.filePreSignedUrl(
					generateDirPath(
						targetFile.getWorkspaceId(),    // bucket path
						targetFile.getSessionId()
					),
					targetFile.getObjectName(),
					EXPIRY,
					targetFile.getName(),
					FileType.FILE
				);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			throw new RemoteServiceException(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
	}

	@Transactional(readOnly = true)
	public ApiResponse<String> downloadThumbnailFileUrl(
		String workspace, String sessionId, String objectName, String name
	) {
		log.info("file download: {}", objectName);
		try {
			return new ApiResponse<>(
				fileManagementService.filePreSignedUrl(
					generateDirPath(workspace, sessionId),    // bucket path
					objectName,
					EXPIRY,
					name,
					FileType.FILE
				)
			);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			return new ApiResponse<>(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
	}

	@Transactional(readOnly = true)
	public FilePreSignedResponse downloadRecordFileUrl(
		String workspaceId,
		String sessionId,
		String userId,
		String objectName
	) {

		RecordFile recordFile = recordFileRepository.findByWorkspaceIdAndSessionIdAndObjectName(
				workspaceId, sessionId, objectName)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_FILE_NOT_FOUND));

		log.info("recordFile download: {}", recordFile.getObjectName());
		try {
			return FilePreSignedResponse.builder()
				.workspaceId(recordFile.getWorkspaceId())
				.sessionId(recordFile.getSessionId())
				.name(recordFile.getName())
				.objectName(recordFile.getObjectName())
				.contentType(recordFile.getContentType())
				.url(
					fileManagementService.filePreSignedUrl(
						generateDirPath(workspaceId, sessionId),    // bucket path
						objectName,
						EXPIRY,
						recordFile.getName(),
						FileType.RECORD
					))
				.expiry(EXPIRY)
				.build();
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			throw new RemoteServiceException(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
	}

	@Transactional(readOnly = true)
	public FileInfoListResponse getFileInfoList(
		String workspaceId,
		String sessionId,
		boolean isDeleted,
		PageRequest pageable
	) {

		Page<File> filePage = fileRepository.findByWorkspaceIdAndSessionIdAndDeletedAndFileType(
			workspaceId, sessionId, isDeleted, pageable);

		List<FileInfoResponse> fileInfoList = filePage.toList()
			.stream()
			.map(fileInfoMapper::toDto)
			.collect(Collectors.toList());

		PageMetadataResponse pageMeta = PagingUtils.pagingBuilder(
			true,
			pageable,
			filePage.getNumberOfElements(),
			filePage.getTotalPages(),
			filePage.getNumberOfElements(),
			filePage.isLast()
		);

		return new FileInfoListResponse(fileInfoList, pageMeta);
	}

	@Transactional(readOnly = true)
	public FileDetailInfoListResponse getRecordFileInfoList(
		String workspaceId,
		String sessionId,
		String userId,
		boolean isDeleted,
		PageRequest pageable
	) {

		Page<RecordFile> recordFilePage = recordFileRepository.findByWorkspaceIdAndSessionIdAndDeletedUsePaging(
			workspaceId, sessionId, isDeleted, pageable);

		List<FileDetailInfoResponse> fileDetailInfoList = new ArrayList<>();
		for (RecordFile recordFile : recordFilePage.toList()) {
			log.info("getRecordFileInfoList : {}", recordFile.getObjectName());
			ApiResponse<UserInfoResponse> feignResponse = accountRestService.getUserInfoByUserId(recordFile.getUuid());

			FileUserInfoResponse fileUserInfoResponse = fileUserInfoMapper.toDto(feignResponse.getData());
			FileDetailInfoResponse fileDetailInfoResponse = recordFileDetailMapper.toDto(recordFile);

			log.info("getRecordFileInfoList : {}", fileUserInfoResponse.toString());
			fileDetailInfoResponse.setFileUserInfo(fileUserInfoResponse);
			fileDetailInfoList.add(fileDetailInfoResponse);
		}

		PageMetadataResponse pageMeta = PagingUtils.pagingBuilder(
			true,
			pageable,
			recordFilePage.getNumberOfElements(),
			recordFilePage.getTotalPages(),
			recordFilePage.getNumberOfElements(),
			recordFilePage.isLast()
		);

		return new FileDetailInfoListResponse(fileDetailInfoList, pageMeta);
	}

	@Transactional
	public FileDeleteResponse removeFile(
		String workspaceId,
		String sessionId,
		String userId,
		String objectName
	) {
		File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_FILE_NOT_FOUND));

		file.setDeleted(true);
		fileRepository.save(file);

		//remove object
		boolean result = false;
		try {
			String stringBuilder = workspaceId + "/"
				+ sessionId + "/"
				+ file.getObjectName();
			result = fileManagementService.removeObject(stringBuilder);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			exception.printStackTrace();
			log.info("{}", exception.getMessage());
			throw new RemoteServiceException(ErrorCode.ERR_FILE_DELETE_EXCEPTION);
		}

		if (!result) {
			throw new RemoteServiceException(ErrorCode.ERR_FILE_DELETE_FAILED);
		}

		return FileDeleteResponse.builder()
			.workspaceId(file.getWorkspaceId())
			.sessionId(file.getSessionId())
			.objectName(file.getName())
			.build();
	}

	@Transactional
	public void deleteFiles(String workspaceId, String sessionId) {
		fileRepository.deleteAllByWorkspaceIdAndSessionId(workspaceId, sessionId);
	}

	private MultipartFile convertBufferImgToMultipartFile(
		FileUploadRequest fileUploadRequest,
		BufferedImage targetImg
	) {
		MultipartFile convertedImg = null;
		try {
			java.io.File outputFile = new java.io.File(
				Objects.requireNonNull(fileUploadRequest.getFile().getOriginalFilename()));
			ImageIO.write(targetImg, "png", outputFile);

			FileItem fileItem = new DiskFileItem(
				"mainFile",
				//Files.probeContentType(outputFile.toPath()),
				"image/png",
				false,
				outputFile.getName(),
				(int)outputFile.length(),
				outputFile.getParentFile()
			);

			InputStream input = new FileInputStream(outputFile);
			OutputStream os = fileItem.getOutputStream();
			IOUtils.copy(input, os);

			convertedImg = new CommonsMultipartFile(fileItem);
		} catch (Exception e) {
			log.info("Exception : convertBufferImgToMultipartFile");
		}
		return convertedImg;
	}

	@Transactional
	FileUploadResult saveShareFile(
		FileUploadRequest fileUploadRequest,
		FileType fileType
	) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

		switch (fileType) {
			case SHARE:
				// 비밀번호가 있는 PDF 일 경우
				if (Objects.equals(fileUploadRequest.getFile().getContentType(), "application/pdf")) {
					try {
						java.io.File file = convertMultiFileToFile(fileUploadRequest.getFile());
						PDDocument document = PDDocument.load(file);
						document.close();
					} catch (Exception e) {
						log.info("This PDF File is locked");
						return FileUploadResult.builder()
							.errorCode(ErrorCode.ERR_PDF_PASSWORD)
							.build();
					}
				}
			case OBJECT:
		}

		// 공유 파일 업로드 (to Storage)
		UploadResult uploadResult = fileManagementService.upload(
			fileUploadRequest.getFile(),
			generateDirPath(fileUploadRequest.getWorkspaceId(), fileUploadRequest.getSessionId()),
			fileType
		);
		if (uploadResult.getErrorCode() != ErrorCode.ERR_SUCCESS) {
			return FileUploadResult.builder()
				.errorCode(ErrorCode.ERR_FILE_UPLOAD_EXCEPTION)
				.build();
		}

		// 공유 파일 정보 저장 (to DB)
		int width = 0;
		int height = 0;

		if (fileType == FileType.SHARE) {
			if (!(Objects.equals(fileUploadRequest.getFile().getContentType(), "application/pdf"))
				|| Objects.equals(fileUploadRequest.getFile().getContentType(), "application/octet-stream")) {
				BufferedImage image = ImageIO.read(fileUploadRequest.getFile().getInputStream());
				width = image.getWidth();
				height = image.getHeight();
			}
		}

		File file = buildShareFile(fileUploadRequest, uploadResult.getResult(), width, height);
		fileRepository.save(file);
		return new FileUploadResult(uploadResult.getResult(), file, uploadResult.getErrorCode());
	}

	@Transactional
	FileUploadResult saveShareFileThumbnail(
		FileUploadRequest fileUploadRequest,
		MultipartFile targetFile,
		String objectName
	) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

		objectName = objectName + "_thumbnail";
		UploadResult uploadResult = fileManagementService.upload(
			targetFile,
			generateDirPath(fileUploadRequest.getWorkspaceId(), fileUploadRequest.getSessionId()),
			FileType.FILE,
			objectName
		);
		if (uploadResult.getErrorCode() != ErrorCode.ERR_SUCCESS) {
			new ApiResponse<>(ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
		}

		File file = buildThumbnailFile(fileUploadRequest, objectName, targetFile.getSize());

		fileRepository.save(file);
		return new FileUploadResult(file, uploadResult.getErrorCode());
	}

	@Transactional
	public ShareFileUploadResponse uploadShareFile(
		FileUploadRequest fileUploadRequest,
		HttpServletRequest httpServletRequest,
		FileType fileType
	) throws Exception {

		// Check Extension
		String fileExtension = Files.getFileExtension(
			Objects.requireNonNull(fileUploadRequest.getFile().getOriginalFilename())).toLowerCase();
		if (!checkFileExtension(fileExtension, fileType)) {
			throw new RemoteServiceException(ErrorCode.ERR_FILE_UNSUPPORTED_EXTENSION);
		}

		// Check file size
		if (fileUploadRequest.getFile().getSize() == 0) {
			throw new RemoteServiceException(ErrorCode.ERR_FILE_EMPTY);
		}

		// Check Storage
		FileStorageCheckResponse storageCheckResult = checkStorageCapacity(fileUploadRequest.getWorkspaceId());
		if (storageCheckResult.getErrorCode() != ErrorCode.ERR_SUCCESS) {
			throw new RemoteServiceException(storageCheckResult.getErrorCode());
		}

		ShareFileUploadResponse shareFileUploadResponse;
		switch (fileType) {
			case SHARE:
				// Check File Orientation (Only AOS)
				boolean checkAos = false;
				if ("mobile".equalsIgnoreCase(httpServletRequest.getHeader("client"))
					&& !Objects.equals(fileUploadRequest.getFile().getContentType(), "application/pdf")
				) {
					MultipartFile orientationAppliedFile = fileOrientation(fileUploadRequest);
					fileUploadRequest.setFile(orientationAppliedFile);
					checkAos = true;
				}

				// Save upload file
				FileUploadResult fileUploadResult = saveShareFile(fileUploadRequest, fileType);
				if (fileUploadResult.getErrorCode() != ErrorCode.ERR_SUCCESS) {
					throw new RemoteServiceException(fileUploadResult.getErrorCode());
				}

				// Make Thumbnail Image
				FileBufferImageResponse bufferImageResponse = makeThumbnail(fileUploadRequest.getFile(), checkAos);
				if (bufferImageResponse.getErrorCode() != ErrorCode.ERR_SUCCESS) {
					throw new RemoteServiceException(bufferImageResponse.getErrorCode());
				}

				// BufferImg to MultiFile (thumbnail)
				MultipartFile thumbnailFile = convertBufferImgToMultipartFile(
					fileUploadRequest, bufferImageResponse.getBufferedImage());
				FileUploadResult thumbnailUploadResult = saveShareFileThumbnail(
					fileUploadRequest, thumbnailFile, fileUploadResult.getObjectName());
				if (thumbnailUploadResult.getErrorCode() != ErrorCode.ERR_SUCCESS) {
					throw new RemoteServiceException(thumbnailUploadResult.getErrorCode());
				}
				// Make result response
				shareFileUploadResponse = shareUploadFileMapper.toDto(fileUploadResult.getFile());
				shareFileUploadResponse.setThumbnailDownloadUrl(
					downloadThumbnailFileUrl(thumbnailUploadResult.getFile()));
				shareFileUploadResponse.setDeleted(fileUploadResult.getFile().isDeleted());
				return shareFileUploadResponse;
			case OBJECT:
				if ("gltf".equals(fileExtension) || "glb".equals(fileExtension)) {
					FileUploadResult objectFileUploadResult = saveShareFile(fileUploadRequest, fileType);
					if (objectFileUploadResult.getErrorCode() != ErrorCode.ERR_SUCCESS) {
						throw new RemoteServiceException(objectFileUploadResult.getErrorCode());
					}
					shareFileUploadResponse = shareUploadFileMapper.toDto(objectFileUploadResult.getFile());
					return shareFileUploadResponse;
				}

				String objectName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));
				Long glftId = fileRepository.save(fileUtil.buildFileOnly3dObject(
					fileUploadRequest,
					objectName + ".gltf",
					FileConvertStatus.CONVERTING,
					0L
				)).getId();
				Long binId = fileRepository.save(fileUtil.buildFileOnly3dObject(
					fileUploadRequest,
					BUFFER_BIN_FILE + "_" + FilenameUtils.removeExtension(objectName),
					FileConvertStatus.CONVERTING,
					0L
				)).getId();
				asyncTaskService.covertObj2Gltf(fileUploadRequest, objectName, glftId, binId);
				shareFileUploadResponse = shareUploadFileMapper.toDto(
					buildObjectFile(fileUploadRequest, objectName + GLTF_EXTENSION));
				shareFileUploadResponse.setDeleted(false);
				return shareFileUploadResponse;
			default:
				log.info("This share file type is not supported");
				throw new RemoteServiceException(ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
		}
	}

	@Transactional
	public FileDeleteResponse removeShareFile(
		String workspaceId,
		String sessionId,
		String leaderUserId,
		String objectName
	) {
		Member leaderInfo = memberRepository.findRoomLeaderBySessionId(workspaceId, sessionId).orElse(null);
		if (!ObjectUtils.isEmpty(leaderInfo) && !leaderUserId.equals(leaderInfo.getUuid())) {
			throw new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID);
		}

		File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_FILE_NOT_FOUND));

		if (file.getFileType() == FileType.OBJECT) {
			File binFile = fileRepository.findByWorkspaceIdAndSessionIdAndObjectName(
				workspaceId, sessionId, BUFFER_BIN_FILE + "_" + FilenameUtils.getBaseName(file.getObjectName())).orElse(null);
			binFile.setDeleted(true);
			fileRepository.save(binFile);
		}

		file.setDeleted(true);
		fileRepository.save(file);

		//remove object
		boolean result;
		try {

			String objectPathToName;
			switch (file.getFileType()) {
				case SHARE:
					objectPathToName = generateDirPath(workspaceId, sessionId) + "file/" + file.getObjectName();
					result = fileManagementService.removeObject(objectPathToName);
					break;
				case OBJECT:
					objectPathToName = generateDirPath(workspaceId, sessionId) + "file/" + FilenameUtils.getBaseName(
						file.getObjectName()) + "/" + file.getObjectName();
					String binFilePathToName =
						generateDirPath(workspaceId, sessionId) + "file/" + FilenameUtils.getBaseName(
							file.getObjectName()) + "/" + BUFFER_BIN_FILE;
					result = fileManagementService.removeObject(objectPathToName) & fileManagementService.removeObject(
						binFilePathToName);
					break;
				default:
					throw new RemoteServiceException(ErrorCode.ERR_FILE_DELETE_FAILED);
			}
			if (!result) {
				new ApiResponse<>(ErrorCode.ERR_FILE_DELETE_FAILED);
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			exception.printStackTrace();
			log.info("{}", exception.getMessage());
			throw new RemoteServiceException(ErrorCode.ERR_FILE_DELETE_EXCEPTION);
		}

		return FileDeleteResponse.builder()
			.workspaceId(file.getWorkspaceId())
			.sessionId(file.getSessionId())
			.objectName(file.getName())
			.build();
	}

	@Transactional
	public FileDeleteResponse removeShareFiles(
		String workspaceId,
		String sessionId,
		String leaderUserId
	) {
		// Leader id check
		Member leaderInfo = memberRepository.findRoomLeaderBySessionId(workspaceId, sessionId)
			.orElseThrow(() -> new RemoteServiceException(ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID));

		List<File> files = fileRepository.findShareFilesAll(workspaceId, sessionId);
		boolean result = true;
		for (File file : files) {
			file.setDeleted(true);
			fileRepository.save(file);
			switch (file.getFileType()) {
				case SHARE:
					try {
						String stringBuilder = "workspace" + "/"
							+ workspaceId + "/"
							+ "remote" + "/"
							+ sessionId + "/"
							+ "file" + "/"
							+ file.getObjectName();
						result = result && fileManagementService.removeObject(stringBuilder);
					} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
						exception.printStackTrace();
						log.info("{}", exception.getMessage());
						throw new RemoteServiceException(ErrorCode.ERR_FILE_DELETE_EXCEPTION);
					}
				case OBJECT:
					try {
						String gltfFilePath = "workspace" + "/"
							+ workspaceId + "/"
							+ "remote" + "/"
							+ sessionId + "/"
							+ "file" + "/"
							+ FilenameUtils.getBaseName(file.getObjectName()) + "/"
							+ file.getObjectName();
						result = result && fileManagementService.removeObject(gltfFilePath);
						String binFilePath = "workspace" + "/"
							+ workspaceId + "/"
							+ "remote" + "/"
							+ sessionId + "/"
							+ "file" + "/"
							+ FilenameUtils.getBaseName(file.getObjectName()) + "/"
							+ BUFFER_BIN_FILE;
						result = result && fileManagementService.removeObject(binFilePath);
					} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
						exception.printStackTrace();
						log.info("{}", exception.getMessage());
						throw new RemoteServiceException(ErrorCode.ERR_FILE_DELETE_EXCEPTION);
					}
			}
		}

		if (!result) {
			throw new RemoteServiceException(ErrorCode.ERR_FILE_DELETE_FAILED);
		}

		return FileDeleteResponse.builder()
			.workspaceId(workspaceId)
			.sessionId(sessionId)
			.build();
	}

	@Transactional(readOnly = true)
	public ShareFileInfoListResponse getShareFileInfoList(
		String workspaceId,
		String sessionId,
		boolean paging,
		Pageable pageable,
		FileType fileType
	) {
		Page<File> shareFilePage = fileRepository.findShareFileByWorkspaceAndSessionIdAndFileType(
			workspaceId, sessionId, paging, pageable, fileType);

		List<ShareFileInfoResponse> shareFileInfoResponses = new ArrayList<>();
		if (!shareFilePage.isEmpty()) {
			shareFileInfoResponses = shareFilePage.stream()
				.map(shareFileInfoMapper::toDto)
				.collect(Collectors.toList());
		}

		if (fileType == FileType.SHARE) {
			for (ShareFileInfoResponse shareFileInfoResponse : shareFileInfoResponses) {
				ApiResponse<String> downloadUrl = downloadThumbnailFileUrl(
					workspaceId,
					sessionId,
					shareFileInfoResponse.getObjectName() + "_thumbnail",
					shareFileInfoResponse.getName()
				);
				shareFileInfoResponse.setThumbnailDownloadUrl(downloadUrl.getData());
			}
		}

		PageMetadataResponse pageMeta = PagingUtils.pagingBuilder(
			paging,
			pageable,
			shareFilePage.getNumberOfElements(),
			shareFilePage.getTotalPages(),
			shareFilePage.getTotalElements(),
			shareFilePage.isLast()
		);

		return new ShareFileInfoListResponse(shareFileInfoResponses, pageMeta);
	}

	private FileBufferImageResponse makePdfThumbnail(MultipartFile targetFile) {
		BufferedImage pdfThumbnail;
		try {
			java.io.File file = convertMultiFileToFile(targetFile);
			PDDocument document = PDDocument.load(file);
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			pdfThumbnail = pdfRenderer.renderImageWithDPI(0, 100, ImageType.RGB);
			document.close();
		} catch (Exception e) {
			log.info("makePdfThumbnail Error message : " + e.getMessage());
			return FileBufferImageResponse.builder()
				.bufferedImage(null)
				.contentType("application/pdf")
				.errorCode(ErrorCode.ERR_FILE_UPLOAD_FAILED)
				.build();
		}
		return FileBufferImageResponse.builder()
			.bufferedImage(pdfThumbnail)
			.contentType("application/pdf")
			.errorCode(ErrorCode.ERR_SUCCESS)
			.build();
	}

	private FileBufferImageResponse makeThumbnail(
		MultipartFile targetFile,
		boolean checkAos
	) {
		FileBufferImageResponse bufferImageResponse;
		try {
			if (Objects.equals(targetFile.getContentType(), "application/pdf")) {
				bufferImageResponse = makePdfThumbnail(targetFile);
				if (bufferImageResponse.getErrorCode() != ErrorCode.ERR_SUCCESS) {
					bufferImageResponse = buildFileBufferImage(
						null,
						0,
						0,
						targetFile.getContentType(),
						ErrorCode.ERR_MAKE_THUMBNAIL
					);
				}
			} else {
				java.io.File fileData = convertMultiFileToFile(targetFile);
				if (checkAos) {
					bufferImageResponse = buildFileBufferImage(
						applyImageOrientation(fileData, getOrientation(fileData)),
						THUMBNAIL_WIDTH,
						THUMBNAIL_HEIGHT,
						targetFile.getContentType(),
						ErrorCode.ERR_SUCCESS
					);
				} else {
					InputStream in = targetFile.getInputStream();
					bufferImageResponse = buildFileBufferImage(
						ImageIO.read(in),
						THUMBNAIL_WIDTH,
						THUMBNAIL_HEIGHT,
						targetFile.getContentType(),
						ErrorCode.ERR_SUCCESS
					);
					in.close();
				}
			}
		} catch (Exception e) {
			bufferImageResponse = buildFileBufferImage(
				null,
				0,
				0,
				targetFile.getContentType(),
				ErrorCode.ERR_MAKE_THUMBNAIL
			);
		}
		return bufferImageResponse;
	}

	public java.io.File convertMultiFileToFile(MultipartFile multipartFile) throws IOException {
		java.io.File file = new java.io.File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(multipartFile.getBytes());
		fos.close();
		return file;
	}

	public String downloadGuideFileUrl(String objectName) {
		try {
			String url = fileManagementService.filePreSignedUrl("guide", objectName, EXPIRY);
			if (Strings.isBlank(url)) {
				throw new RemoteServiceException(ErrorCode.ERR_FILE_NOT_FOUND);
			}
			return url;
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			throw new RemoteServiceException(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
	}

	@Transactional
	public void removeFiles(String sessionId) {
		try {
			Room room = roomRepository.findBySessionId(sessionId).orElse(null);
			if (!ObjectUtils.isEmpty(room)) {
				List<String> listName = new LinkedList<>();
				List<File> files = fileRepository.findByWorkspaceIdAndSessionId(room.getWorkspaceId(), sessionId);
				for (File file : files) {
					listName.add(file.getObjectName());
				}
				if (!listName.isEmpty()) {
					String dirPath = generateDirPath(room.getWorkspaceId(), sessionId);
					fileManagementService.removeBucket(null, dirPath, listName, FileType.FILE);
				}
				deleteFiles(room.getWorkspaceId(), sessionId);
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	@Transactional
	public void removeFiles(String workspaceId, String sessionId) {
		log.info("ROOM removeFiles {}, {}", workspaceId, sessionId);
		try {
			List<String> listName = new ArrayList<>();
			List<File> files = fileRepository.findByWorkspaceIdAndSessionId(workspaceId, sessionId);
			for (File file : files) {
				listName.add(file.getObjectName());
			}

			if (!listName.isEmpty()) {
				String dirPath = generateDirPath(workspaceId, sessionId);
				fileManagementService.removeBucket(null, dirPath, listName, FileType.FILE);
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}
		deleteFiles(workspaceId, sessionId);
	}

	public FileStorageInfoResponse checkRemoteStorageCapacity(String workspaceId) {

		long profileStorageSize = 0L;
		long attachFileStorageSize = 0L;
		long shareFileStorageSize = 0L;

		List<File> files = fileRepository.findByWorkspaceIdAndDeleted(workspaceId, false);
		for (File file : files) {
			switch (file.getFileType()) {
				case PROFILE: {
					profileStorageSize = profileStorageSize + file.getSize();
				}
				case FILE: {
					attachFileStorageSize = attachFileStorageSize + file.getSize();
				}
				case SHARE: {
					shareFileStorageSize = shareFileStorageSize + file.getSize();
				}
				break;
			}
		}

		profileStorageSize = (profileStorageSize / 1024) / 1024;
		attachFileStorageSize = (attachFileStorageSize / 1024) / 1024;
		shareFileStorageSize = (shareFileStorageSize / 1024) / 1024;

		return
			FileStorageInfoResponse.builder()
				.workspaceId(workspaceId)
				.profileStorageSize(profileStorageSize)
				.attachFileStorageSize(attachFileStorageSize)
				.shareFileStorageSize(shareFileStorageSize)
				.totalRemoteUseStorageSize(profileStorageSize + attachFileStorageSize + shareFileStorageSize)
				.build();
	}

	public FileStorageCheckResponse checkStorageCapacity(String workspaceId) {

		ErrorCode errorCode = ErrorCode.ERR_SUCCESS;

		int OVER_STORAGE = 100;
		int MIN_STORAGE = 100;

		ApiResponse<WorkspaceLicensePlanInfoResponse> licensePlanInfo = licenseRestService.getWorkspacePlan(
			workspaceId);
		double residualStorageValue =
			(double)licensePlanInfo.getData().getMaxStorageSize() - (double)licensePlanInfo.getData()
				.getCurrentUsageStorage();
		double usedStoragePer =
			((double)licensePlanInfo.getData().getCurrentUsageStorage() / (double)licensePlanInfo.getData()
				.getMaxStorageSize()) * 100;

		log.info("Max storage size : " + (double)licensePlanInfo.getData().getMaxStorageSize());
		log.info("Current use storage size : " + (double)licensePlanInfo.getData().getCurrentUsageStorage());

		if (usedStoragePer >= OVER_STORAGE || residualStorageValue <= MIN_STORAGE) {
			errorCode = ErrorCode.ERR_STORAGE_CAPACITY_FULL;
		}

		log.info("Storage capacity result code : " + errorCode.getCode());

		return FileStorageCheckResponse.builder()
			.errorCode(errorCode)
			.usedStoragePer((int)usedStoragePer)
			.build();
	}

	private boolean checkFileExtension(String fileExtension, FileType fileType) {
		switch (fileType) {
			case SHARE:
				for (String shareFileExtension : shareFileAllowExtensionList) {
					if (fileExtension.equals(shareFileExtension)) {
						return true;
					}
				}
				break;
			case OBJECT:
				for (String objectFileExtension : objectFileAllowExtensionList) {
					if (fileExtension.equals(objectFileExtension)) {
						return true;
					}
				}
				break;
		}
		return false;
	}

	private File buildFile(FileUploadRequest fileUploadRequest, String objectName, FileType fileType) {
		return File.builder()
			.workspaceId(fileUploadRequest.getWorkspaceId())
			.sessionId(fileUploadRequest.getSessionId())
			.uuid(fileUploadRequest.getUserId())
			.name(fileUploadRequest.getFile().getOriginalFilename())
			.objectName(objectName)
			.contentType(fileUploadRequest.getFile().getContentType())
			.size(fileUploadRequest.getFile().getSize())
			.fileType(fileType)
			.fileConvertStatus(FileConvertStatus.NON_TARGET)
			.height(0)
			.width(0)
			.build();
	}

	private File buildProfileFile(
		RoomProfileUpdateRequest fileUploadRequest, String workspaceId, String sessionId, String profileUrl
	) {
		return File.builder()
			.workspaceId(workspaceId)
			.sessionId(sessionId)
			.uuid(fileUploadRequest.getUuid())
			.name(fileUploadRequest.getProfile().getOriginalFilename())
			.objectName(profileUrl)
			.contentType(fileUploadRequest.getProfile().getContentType())
			.size(fileUploadRequest.getProfile().getSize())
			.fileType(FileType.PROFILE)
			.fileConvertStatus(FileConvertStatus.NON_TARGET)
			.height(0)
			.width(0)
			.build();
	}

	private RecordFile buildRecordFile(RecordFileUploadRequest recordFileUploadRequest, String objectName) {
		return RecordFile.builder()
			.workspaceId(recordFileUploadRequest.getWorkspaceId())
			.sessionId(recordFileUploadRequest.getSessionId())
			.uuid(recordFileUploadRequest.getUserId())
			.name(recordFileUploadRequest.getFile().getOriginalFilename())
			.objectName(objectName)
			.contentType(recordFileUploadRequest.getFile().getContentType())
			.size(recordFileUploadRequest.getFile().getSize())
			.durationSec(recordFileUploadRequest.getDurationSec())
			.build();
	}

	private File buildShareFile(FileUploadRequest fileUploadRequest, String objectName, int width, int height) {
		return File.builder()
			.workspaceId(fileUploadRequest.getWorkspaceId())
			.sessionId(fileUploadRequest.getSessionId())
			.uuid(fileUploadRequest.getUserId())
			.name(fileUploadRequest.getFile().getOriginalFilename())
			.objectName(objectName)
			.contentType(fileUploadRequest.getFile().getContentType())
			.size(fileUploadRequest.getFile().getSize())
			.fileType(FileType.SHARE)
			.fileConvertStatus(FileConvertStatus.NON_TARGET)
			.height(height)
			.width(width)
			.build();
	}

	private File buildObjectFile(FileUploadRequest fileUploadRequest, String objectName) {
		return File.builder()
			.workspaceId(fileUploadRequest.getWorkspaceId())
			.sessionId(fileUploadRequest.getSessionId())
			.uuid(fileUploadRequest.getUserId())
			.name(fileUploadRequest.getFile().getOriginalFilename())
			.objectName(objectName)
			.contentType(fileUploadRequest.getFile().getContentType())
			.size(fileUploadRequest.getFile().getSize())
			.fileType(FileType.OBJECT)
			.fileConvertStatus(FileConvertStatus.NON_TARGET)
			.height(0)
			.width(0)
			.build();
	}

	private File buildThumbnailFile(FileUploadRequest fileUploadRequest, String objectName, long size) {
		return File.builder()
			.workspaceId(fileUploadRequest.getWorkspaceId())
			.sessionId(fileUploadRequest.getSessionId())
			.uuid(fileUploadRequest.getUserId())
			.name(fileUploadRequest.getFile().getOriginalFilename())
			.objectName(objectName)
			.contentType("image/png")
			.size(size)
			.fileType(FileType.SHARE)
			.fileConvertStatus(FileConvertStatus.NON_TARGET)
			.width(THUMBNAIL_WIDTH)
			.height(THUMBNAIL_HEIGHT)
			.build();
	}

	private FileBufferImageResponse buildFileBufferImage(
		BufferedImage bufferedImage,
		int width,
		int height,
		String contentType,
		ErrorCode errorCode
	) {
		if (bufferedImage == null) {
			return FileBufferImageResponse.builder()
				.bufferedImage(null)
				.contentType(contentType)
				.errorCode(errorCode)
				.build();
		} else {
			return FileBufferImageResponse.builder()
				.bufferedImage(Scalr.resize(bufferedImage, width, height))
				.contentType(contentType)
				.errorCode(errorCode)
				.build();
		}
	}

	private MultipartFile fileOrientation(FileUploadRequest fileUploadRequest) throws Exception {
		java.io.File fileData = convertMultiFileToFile(fileUploadRequest.getFile());
		BufferedImage bufferedImage = applyImageOrientation(fileUploadRequest.getFile(), getOrientation(fileData));
		ImageIO.write(bufferedImage, "png", fileData);
		return convertBufferImgToMultipartFile(fileUploadRequest, bufferedImage);
	}

	private BufferedImage applyImageOrientation(java.io.File file, int orientation) throws Exception {
		BufferedImage bufferedImage = ImageIO.read(file);
		if (orientation == 1) { // 정위치
			return bufferedImage;
		} else if (orientation == 6) {
			return rotateImage(bufferedImage
				, 90);
		} else if (orientation == 3) {
			return rotateImage(bufferedImage, 180);
		} else if (orientation == 8) {
			return rotateImage(bufferedImage, 270);
		} else {
			return bufferedImage;
		}
	}

	private BufferedImage applyImageOrientation(MultipartFile file, int orientation) throws Exception {
		BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
		if (orientation == 1) { // 정위치
			return bufferedImage;
		} else if (orientation == 6) {
			return rotateImage(bufferedImage, 90);
		} else if (orientation == 3) {
			return rotateImage(bufferedImage, 180);
		} else if (orientation == 8) {
			return rotateImage(bufferedImage, 270);
		} else {
			return bufferedImage;
		}
	}

	private int getOrientation(java.io.File file) throws Exception {
		int orientation = 1;
		Metadata metadata = ImageMetadataReader.readMetadata(file);
		Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
		if (directory != null) {
			orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
		}
		return orientation;
	}

	private BufferedImage rotateImage(BufferedImage targetImage, int radians) {
		BufferedImage newImage;
		if (radians == 90 || radians == 270) {
			newImage = new BufferedImage(targetImage.getHeight(), targetImage.getWidth(), targetImage.getType());
		} else if (radians == 180) {
			newImage = new BufferedImage(targetImage.getWidth(), targetImage.getHeight(), targetImage.getType());
		} else {
			return targetImage;
		}

		Graphics2D graphics = (Graphics2D)newImage.getGraphics();
		graphics.rotate(Math.toRadians(radians), newImage.getWidth() / 2, newImage.getHeight() / 2);
		graphics.translate(
			(newImage.getWidth() - targetImage.getWidth()) / 2, (newImage.getHeight() - targetImage.getHeight()) / 2);
		graphics.drawImage(targetImage, 0, 0, targetImage.getWidth(), targetImage.getHeight(), null);

		return newImage;
	}
}
