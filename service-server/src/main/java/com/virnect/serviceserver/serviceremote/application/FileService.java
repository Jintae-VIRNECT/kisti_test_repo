package com.virnect.serviceserver.serviceremote.application;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.user.UserRestService;
import com.virnect.data.dao.file.FileRepository;
import com.virnect.data.dao.file.RecordFileRepository;
import com.virnect.data.dao.member.MemberRepository;
import com.virnect.data.dao.room.RoomRepository;
import com.virnect.data.domain.file.File;
import com.virnect.data.domain.file.FileType;
import com.virnect.data.domain.file.RecordFile;
import com.virnect.data.domain.member.Member;
import com.virnect.data.domain.room.Room;
import com.virnect.data.dto.FileUploadResult;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.data.dto.UploadResult;
import com.virnect.serviceserver.serviceremote.dto.mapper.file.FileInfoMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.file.FileUploadMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.file.FileUserInfoMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.file.RecordFileDetailMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.file.RecordFileMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.file.ShareFileInfoMapper;
import com.virnect.serviceserver.serviceremote.dto.mapper.file.ShareUploadFileMapper;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.file.IFileManagementService;
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
import com.virnect.data.dto.response.file.FileUploadResponse;
import com.virnect.data.dto.response.file.FileUserInfoResponse;
import com.virnect.data.dto.response.file.RoomProfileUpdateResponse;
import com.virnect.data.dto.response.file.ShareFileInfoListResponse;
import com.virnect.data.dto.response.file.ShareFileInfoResponse;
import com.virnect.data.dto.response.file.ShareFileUploadResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

	private final int THUMBNAIL_WIDTH = 120;
	private final int THUMBNAIL_HEIGHT = 70;

	private final IFileManagementService fileManagementService;
	private final UserRestService userRestService;

	private final FileUploadMapper fileUploadMapper;
	private final RecordFileMapper recordFileMapper;
	private final FileInfoMapper fileInfoMapper;
	private final FileUserInfoMapper fileUserInfoMapper;
	private final RecordFileDetailMapper recordFileDetailMapper;
	private final ShareUploadFileMapper shareUploadFileMapper;
	private final ShareFileInfoMapper shareFileInfoMapper;

	private final FileRepository fileRepository;
	private final RoomRepository roomRepository;
	private final RecordFileRepository recordFileRepository;

	private final SessionTransactionalService sessionTransactionalService;

	private final MemberRepository memberRepository;

	private String generateDirPath(String... args) {
		StringBuilder stringBuilder;
		stringBuilder = new StringBuilder();
		for (String argument : args) {
			stringBuilder.append(argument).append("/");
		}
		return stringBuilder.toString();
	}

	@Transactional
	public ApiResponse<FileUploadResponse> uploadFile(FileUploadRequest fileUploadRequest, FileType fileType) {

		String bucketPath = generateDirPath(fileUploadRequest.getWorkspaceId(), fileUploadRequest.getSessionId());
		String objectName = null;

		try {
			UploadResult uploadResult = fileManagementService.upload(fileUploadRequest.getFile(), bucketPath, FileType.FILE);
			switch (uploadResult.getErrorCode()) {
				case ERR_FILE_ASSUME_DUMMY:
				case ERR_FILE_UNSUPPORTED_EXTENSION:
				case ERR_FILE_SIZE_LIMIT:
					return new ApiResponse<>(new FileUploadResponse(), uploadResult.getErrorCode());
				case ERR_SUCCESS:
					objectName = uploadResult.getResult();
					break;
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			new ApiResponse<>(
				new FileUploadResponse(),
				ErrorCode.ERR_FILE_UPLOAD_EXCEPTION.getCode(),
				exception.getMessage()
			);
		}

		File file = File.builder()
			.workspaceId(fileUploadRequest.getWorkspaceId())
			.sessionId(fileUploadRequest.getSessionId())
			.uuid(fileUploadRequest.getUserId())
			.name(fileUploadRequest.getFile().getOriginalFilename())
			.objectName(objectName)
			.contentType(fileUploadRequest.getFile().getContentType())
			.size(fileUploadRequest.getFile().getSize())
			.fileType(fileType)
			.build();

		File uploadResult = fileRepository.save(file);
		if (ObjectUtils.isEmpty(uploadResult)) {
			return new ApiResponse<>(ErrorCode.ERR_FILE_UPLOAD_FAILED);
		}

		FileUploadResponse fileUploadResponse = fileUploadMapper.toDto(file);

		return new ApiResponse<>(fileUploadResponse);
	}

	@Transactional
	public ApiResponse<FileUploadResponse> uploadRecordFile(RecordFileUploadRequest recordFileUploadRequest) {

		// upload to file storage
		String bucketPath = generateDirPath(recordFileUploadRequest.getWorkspaceId(), recordFileUploadRequest.getSessionId());
		String objectName = null;

		try {
			UploadResult uploadResult = fileManagementService.upload(
				recordFileUploadRequest.getFile(),
				bucketPath,
				FileType.RECORD
			);

			switch (uploadResult.getErrorCode()) {
				case ERR_FILE_ASSUME_DUMMY:
				case ERR_FILE_UNSUPPORTED_EXTENSION:
				case ERR_FILE_SIZE_LIMIT:
					return new ApiResponse<>(uploadResult.getErrorCode());
				case ERR_SUCCESS:
					objectName = uploadResult.getResult();
					break;
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			return new ApiResponse<>(ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
		}

		RecordFile recordFile = RecordFile.builder()
			.workspaceId(recordFileUploadRequest.getWorkspaceId())
			.sessionId(recordFileUploadRequest.getSessionId())
			.uuid(recordFileUploadRequest.getUserId())
			.name(recordFileUploadRequest.getFile().getOriginalFilename())
			.objectName(objectName)
			.contentType(recordFileUploadRequest.getFile().getContentType())
			.size(recordFileUploadRequest.getFile().getSize())
			.durationSec(recordFileUploadRequest.getDurationSec())
			.build();

		if (ObjectUtils.isEmpty(recordFileRepository.save(recordFile))) {
			return new ApiResponse<>(ErrorCode.ERR_FILE_UPLOAD_FAILED);
		}

		FileUploadResponse fileUploadResponse = recordFileMapper.toDto(recordFile);

		return new ApiResponse<>(fileUploadResponse);
	}

	@Transactional
	public ApiResponse<RoomProfileUpdateResponse> profileUpload(
		String workspaceId,
		String sessionId,
		RoomProfileUpdateRequest roomProfileUpdateRequest
	) {

		RoomProfileUpdateResponse roomProfileUpdateResponse = new RoomProfileUpdateResponse();
		String profileUrl = IFileManagementService.DEFAULT_ROOM_PROFILE;

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId).orElse(null);
		if (ObjectUtils.isEmpty(room)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_NOT_FOUND);
		}

		if (!room.getLeaderId().equals(roomProfileUpdateRequest.getUuid())) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
		}

		if (!ObjectUtils.isEmpty(roomProfileUpdateRequest.getProfile())) {
			try {
				UploadResult uploadResult = fileManagementService.uploadProfile(
					roomProfileUpdateRequest.getProfile(),
					null
				);
				switch (uploadResult.getErrorCode()) {
					case ERR_FILE_ASSUME_DUMMY:
					case ERR_FILE_UNSUPPORTED_EXTENSION:
					case ERR_FILE_SIZE_LIMIT:
						return new ApiResponse<>(uploadResult.getErrorCode());
					case ERR_SUCCESS:
						profileUrl = uploadResult.getResult();
						break;
				}
				fileManagementService.deleteProfile(room.getProfile());
			} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
				log.info("{}", exception.getMessage());
				return new ApiResponse<>(ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
			}
		}

		roomProfileUpdateResponse.setSessionId(sessionId);
		roomProfileUpdateResponse.setProfile(profileUrl);
		room.setProfile(profileUrl);

		Room uploadResult = roomRepository.save(room);
		if (ObjectUtils.isEmpty(uploadResult)) {
			new ApiResponse<>(ErrorCode.ERR_PROFILE_UPLOAD_FAILED);
		}

		return new ApiResponse<>(roomProfileUpdateResponse);
	}

	@Transactional
	public ApiResponse<ResultResponse> deleteProfile(
		String workspaceId,
		String sessionId
	) {

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId).orElse(null);
		if (ObjectUtils.isEmpty(room)) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_NOT_FOUND);
		}

		try {
			fileManagementService.deleteProfile(room.getProfile());
			room.setProfile(IFileManagementService.DEFAULT_ROOM_PROFILE);
			roomRepository.save(room);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			exception.printStackTrace();
		}
		ResultResponse resultResponse = new ResultResponse();
		resultResponse.setResult(true);

		return new ApiResponse<>(resultResponse);
	}

	@Transactional(readOnly = true)
	public ApiResponse<FilePreSignedResponse> downloadFileUrl(
		String workspaceId,
		String sessionId,
		String userId,
		String objectName,
		FileType fileType
	) {

		File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectNameAndFileType(workspaceId, sessionId, objectName, fileType).orElse(null);
		if (ObjectUtils.isEmpty(file)) {
			return new ApiResponse<>(ErrorCode.ERR_FILE_NOT_FOUND);
		}

		log.info("file download: {}", file.getObjectName());
		try {
			StringBuilder stringBuilder;
			stringBuilder = new StringBuilder();
			stringBuilder.append(workspaceId).append("/")
				.append(sessionId).append("/")
				.append(file.getObjectName());

			// upload to file storage
			String bucketPath = generateDirPath(workspaceId, sessionId);

			int expiry = 60 * 60 * 24; //one day
			String url = fileManagementService.filePreSignedUrl(
				bucketPath, objectName, expiry, file.getName(), FileType.FILE);
			FilePreSignedResponse filePreSignedResponse = new FilePreSignedResponse();
			filePreSignedResponse.setWorkspaceId(file.getWorkspaceId());
			filePreSignedResponse.setSessionId(file.getSessionId());
			filePreSignedResponse.setName(file.getName());
			filePreSignedResponse.setObjectName(file.getObjectName());
			filePreSignedResponse.setContentType(file.getContentType());
			filePreSignedResponse.setUrl(url);
			filePreSignedResponse.setExpiry(expiry);
			return new ApiResponse<>(filePreSignedResponse);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			return new ApiResponse<>(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
	}

	@Transactional(readOnly = true)
	public ApiResponse<String> downloadFileUrl(File targetFile) {
		log.info("file download: {}", targetFile.getObjectName());
		try {
			StringBuilder stringBuilder;
			stringBuilder = new StringBuilder();
			stringBuilder.append(targetFile.getWorkspaceId()).append("/")
				.append(targetFile.getSessionId()).append("/")
				.append(targetFile.getObjectName());

			// upload to file storage
			String bucketPath = generateDirPath(targetFile.getWorkspaceId(), targetFile.getSessionId());

			int expiry = 60 * 60 * 24; //one day
			String url = fileManagementService.filePreSignedUrl(
				bucketPath, targetFile.getObjectName(), expiry, targetFile.getName(), FileType.FILE);

			return new ApiResponse<>(url);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			return new ApiResponse<>(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
	}

	@Transactional(readOnly = true)
	public ApiResponse<String> downloadFileUrl(String workspace, String sessionId, String objectName, String name) {

		log.info("file download: {}", objectName);
		try {
			StringBuilder stringBuilder;
			stringBuilder = new StringBuilder();
			stringBuilder.append(workspace).append("/")
				.append(sessionId).append("/")
				.append(objectName);
			// upload to file storage
			String bucketPath = generateDirPath(workspace, sessionId);

			int expiry = 60 * 60 * 24; //one day
			String url = fileManagementService.filePreSignedUrl(
				bucketPath, objectName, expiry, name, FileType.FILE);
			return new ApiResponse<>(url);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			return new ApiResponse<>(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
	}

	@Transactional(readOnly = true)
	public ApiResponse<FilePreSignedResponse> downloadRecordFileUrl(
		String workspaceId,
		String sessionId,
		String userId,
		String objectName
	) {

		RecordFile recordFile = recordFileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName).orElse(null);
		if (ObjectUtils.isEmpty(recordFile)) {
			return new ApiResponse<>(ErrorCode.ERR_FILE_NOT_FOUND);
		}

		log.info("recordFile download: {}", recordFile.getObjectName());
		try {
			StringBuilder stringBuilder;
			stringBuilder = new StringBuilder();
			stringBuilder.append(workspaceId).append("/")
				.append(sessionId).append("/")
				.append(recordFile.getObjectName());

			// upload to file storage
			String bucketPath = generateDirPath(workspaceId, sessionId);

			int expiry = 60 * 60 * 24; //one day
			String url = fileManagementService.filePreSignedUrl(
				bucketPath, objectName, expiry, recordFile.getName(), FileType.RECORD);
			FilePreSignedResponse filePreSignedResponse = new FilePreSignedResponse();
			filePreSignedResponse.setWorkspaceId(recordFile.getWorkspaceId());
			filePreSignedResponse.setSessionId(recordFile.getSessionId());
			filePreSignedResponse.setName(recordFile.getName());
			filePreSignedResponse.setObjectName(recordFile.getObjectName());
			filePreSignedResponse.setContentType(recordFile.getContentType());
			filePreSignedResponse.setUrl(url);
			filePreSignedResponse.setExpiry(expiry);
			return new ApiResponse<>(filePreSignedResponse);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			return new ApiResponse<>(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
	}

	@Transactional(readOnly = true)
	public ApiResponse<FileInfoListResponse> getFileInfoList(
		String workspaceId,
		String sessionId,
		String userId,
		boolean isDeleted,
		PageRequest pageable,
		FileType fileType
	) {

		Page<File> filePage;
		if (isDeleted) {
			filePage = fileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsTrueAndFileType(
				workspaceId, sessionId, pageable, fileType);
		} else {
			filePage = fileRepository.findByWorkspaceIdAndSessionId(workspaceId, sessionId, pageable);
		}

		PageMetadataResponse pageMeta = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.numberOfElements(filePage.getNumberOfElements())
			.totalPage(filePage.getTotalPages())
			.totalElements(filePage.getNumberOfElements())
			.last(filePage.isLast())
			.build();

		List<FileInfoResponse> fileInfoList = filePage.toList()
			.stream()
			.map(file -> fileInfoMapper.toDto(file))
			.collect(Collectors.toList());

		return new ApiResponse<>(new FileInfoListResponse(fileInfoList, pageMeta));
	}

	@Transactional(readOnly = true)
	public ApiResponse<FileDetailInfoListResponse> getRecordFileInfoList(
		String workspaceId,
		String sessionId,
		String userId,
		boolean isDeleted,
		PageRequest pageable
	) {

		Page<RecordFile> recordFilePage;
		if (isDeleted) {
			recordFilePage = recordFileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsTrue(workspaceId, sessionId, pageable);
		} else {
			recordFilePage = recordFileRepository.findByWorkspaceIdAndSessionId(workspaceId, sessionId, pageable);
		}

		List<FileDetailInfoResponse> fileDetailInfoList = new ArrayList<>();
		for (RecordFile recordFile : recordFilePage.toList()) {
			log.info("getRecordFileInfoList : {}", recordFile.getObjectName());
			ApiResponse<UserInfoResponse> feignResponse = userRestService.getUserInfoByUserId(recordFile.getUuid());

			FileUserInfoResponse fileUserInfoResponse = fileUserInfoMapper.toDto(feignResponse.getData());
			FileDetailInfoResponse fileDetailInfoResponse = recordFileDetailMapper.toDto(recordFile);

			log.info("getRecordFileInfoList : {}", fileUserInfoResponse.toString());
			fileDetailInfoResponse.setFileUserInfo(fileUserInfoResponse);
			fileDetailInfoList.add(fileDetailInfoResponse);
		}

		PageMetadataResponse pageMeta = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.numberOfElements(recordFilePage.getNumberOfElements())
			.totalPage(recordFilePage.getTotalPages())
			.totalElements(recordFilePage.getTotalElements())
			.last(recordFilePage.isLast())
			.build();

		return new ApiResponse<>(new FileDetailInfoListResponse(fileDetailInfoList, pageMeta));
	}

	@Transactional
	public ApiResponse<FileDeleteResponse> removeFile(
		String workspaceId,
		String sessionId,
		String userId,
		String objectName,
		FileType fileType
	) {

		File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectNameAndFileType(workspaceId, sessionId, objectName, fileType).orElse(null);
		if (ObjectUtils.isEmpty(file)) {
			return new ApiResponse<>(ErrorCode.ERR_FILE_NOT_FOUND);
		}

		Objects.requireNonNull(file).setDeleted(true);
		fileRepository.save(file);

		//remove object
		boolean result = false;
		try {
			StringBuilder stringBuilder;
			stringBuilder = new StringBuilder();
			stringBuilder.append(workspaceId).append("/")
				.append(sessionId).append("/")
				.append(file.getObjectName());
			result = fileManagementService.removeObject(stringBuilder.toString());
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			exception.printStackTrace();
			log.info("{}", exception.getMessage());
			new ApiResponse<>(ErrorCode.ERR_FILE_DELETE_EXCEPTION);
		}

		if (!result) {
			return new ApiResponse<>(ErrorCode.ERR_FILE_DELETE_FAILED);
		}

		FileDeleteResponse fileDeleteResponse = new FileDeleteResponse();
		fileDeleteResponse.setWorkspaceId(file.getWorkspaceId());
		fileDeleteResponse.setSessionId(file.getSessionId());
		fileDeleteResponse.setObjectName(file.getName());

		return new ApiResponse<>(fileDeleteResponse);
	}

	@Transactional
	public void deleteFile(File file, boolean drop) {
		if(drop) {
			fileRepository.delete(file);
		} else {
			file.setDeleted(true);
			fileRepository.save(file);
		}
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
			java.io.File outputFile = new java.io.File(fileUploadRequest.getFile().getOriginalFilename());
			ImageIO.write(targetImg, "png", outputFile);

			FileItem fileItem = new DiskFileItem(
				"mainFile",
				//Files.probeContentType(outputFile.toPath()),
				"image/png",
				false,
				outputFile.getName(),
				(int) outputFile.length(),
				outputFile.getParentFile()
			);

			InputStream input = new FileInputStream(outputFile);
			OutputStream os = fileItem.getOutputStream();
			IOUtils.copy(input, os);

			convertedImg = new CommonsMultipartFile(fileItem);
		} catch (Exception e) {

		}
		return convertedImg;
	}

	@Transactional
	FileUploadResult saveFile(
		FileUploadRequest fileUploadRequest,
		FileType fileType
	) {

		String bucketPath = generateDirPath(fileUploadRequest.getWorkspaceId(), fileUploadRequest.getSessionId());

		File file = null;
		UploadResult uploadResult = null;
		try {
			uploadResult = fileManagementService.upload(
				fileUploadRequest.getFile(),
				bucketPath,
				FileType.FILE
			);

			Integer width;
			Integer height;

			if (fileUploadRequest.getFile().getContentType().equals("application/pdf")) {
				width = 0;
				height = 0;
			} else {
				BufferedImage image = ImageIO.read(fileUploadRequest.getFile().getInputStream());
				width = image.getWidth();
				height = image.getHeight();
			}

			file = File.builder()
				.workspaceId(fileUploadRequest.getWorkspaceId())
				.sessionId(fileUploadRequest.getSessionId())
				.uuid(fileUploadRequest.getUserId())
				.name(fileUploadRequest.getFile().getOriginalFilename())
				.objectName(uploadResult.getResult())
				.contentType(fileUploadRequest.getFile().getContentType())
				.size(fileUploadRequest.getFile().getSize())
				.fileType(fileType)
				.width(width)
				.height(height)
				.build();
			fileRepository.save(file);

		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			new ApiResponse<>(ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
		}
		return new FileUploadResult(uploadResult.getResult(), file, uploadResult.getErrorCode());
	}

	@Transactional
	FileUploadResult saveThumbnailFile(
		FileUploadRequest fileUploadRequest,
		MultipartFile targetFile,
		FileType fileType,
		String objectName
	) {
		String bucketPath = generateDirPath(fileUploadRequest.getWorkspaceId(), fileUploadRequest.getSessionId());

		File file = null;
		UploadResult uploadResult = null;

		try {
			objectName = objectName + "_thumbnail";
			uploadResult = fileManagementService.upload(
				targetFile,
				bucketPath,
				FileType.FILE,
				objectName
			);
			file = File.builder()
				.workspaceId(fileUploadRequest.getWorkspaceId())
				.sessionId(fileUploadRequest.getSessionId())
				.uuid(fileUploadRequest.getUserId())
				.name(fileUploadRequest.getFile().getOriginalFilename())
				.objectName(objectName)
				.contentType("image/png")
				.size(targetFile.getSize())
				.fileType(fileType)
				.width(THUMBNAIL_WIDTH)
				.height(THUMBNAIL_HEIGHT)
				.build();
			fileRepository.save(file);

		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			new ApiResponse<>(ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
		}

		return new FileUploadResult(file, uploadResult.getErrorCode());
	}


	@Transactional
	public ApiResponse<ShareFileUploadResponse> uploadShareFile(
		FileUploadRequest fileUploadRequest,
		FileType fileType
	) {
		// Make Thumbnail Image
		BufferedImage bufferedImage = makeThumbnail(fileUploadRequest.getFile());
		MultipartFile thumbnailFile = convertBufferImgToMultipartFile(fileUploadRequest, bufferedImage);

		// Save Thumbnail and upload file
		FileUploadResult fileUploadResult = saveFile(fileUploadRequest, FileType.SHARE);
		FileUploadResult thumbnailUploadResult = saveThumbnailFile(fileUploadRequest, thumbnailFile, FileType.SHARE, fileUploadResult.getObjectName());

		if (thumbnailUploadResult.getErrorCode() != ErrorCode.ERR_SUCCESS || fileUploadResult.getErrorCode() != ErrorCode.ERR_SUCCESS) {
			return new ApiResponse<>(fileUploadResult.getErrorCode());
		}

		ShareFileUploadResponse fileUploadResponse = shareUploadFileMapper.toDto(fileUploadResult.getFile());

		// Get File thumbnail download url
		ApiResponse<String> downloadUrl = downloadFileUrl(thumbnailUploadResult.getFile());
		fileUploadResponse.setThumbnailDownloadUrl(downloadUrl.getData());
		fileUploadResponse.setDeleted(fileUploadResult.getFile().isDeleted());

		return new ApiResponse<>(fileUploadResponse);
	}

	@Transactional
	public ApiResponse<FileDeleteResponse> removeShareFile(
		String workspaceId,
		String sessionId,
		String leaderUserId,
		String objectName,
		FileType fileType
	) {
		FileDeleteResponse fileDeleteResponse = new FileDeleteResponse();

		Member leaderInfo = memberRepository.findRoomLeaderBySessionId(workspaceId, sessionId);
		if (!leaderUserId.equals(leaderInfo.getUuid())) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID);
		}

		File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectNameAndFileType(workspaceId, sessionId, objectName, fileType).orElse(null);
		if (Objects.isNull(file)) {
			return new ApiResponse<>(ErrorCode.ERR_FILE_NOT_FOUND);
		}

		file.setDeleted(true);
		fileRepository.save(file);

		//remove object
		boolean result;
		try {
			StringBuilder stringBuilder;
			stringBuilder = new StringBuilder();
			stringBuilder.append(workspaceId).append("/")
				.append(sessionId).append("/")
				.append(file.getObjectName());
			result = fileManagementService.removeObject(stringBuilder.toString());
			if (!result) {
				new ApiResponse<>(ErrorCode.ERR_FILE_DELETE_FAILED);
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			exception.printStackTrace();
			log.info("{}", exception.getMessage());
			new ApiResponse<>(ErrorCode.ERR_FILE_DELETE_EXCEPTION);
		}

		fileDeleteResponse.setWorkspaceId(file.getWorkspaceId());
		fileDeleteResponse.setSessionId(file.getSessionId());
		fileDeleteResponse.setObjectName(file.getName());
		return new ApiResponse<>(fileDeleteResponse);
	}

	@Transactional
	public ApiResponse<FileDeleteResponse> removeShareFiles(
		String workspaceId,
		String sessionId,
		String leaderUserId,
		FileType fileType
	) {
		// Leader id check
		Member leaderInfo = memberRepository.findRoomLeaderBySessionId(workspaceId, sessionId);
		if (!leaderUserId.equals(leaderInfo.getUuid())) {
			return new ApiResponse<>(ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID);
		}

		List<File> files = fileRepository.findByWorkspaceIdAndSessionIdAndFileType(workspaceId, sessionId, fileType);

		boolean result = true;
		for (File file : files) {
			file.setDeleted(true);
			fileRepository.save(file);
			try {
				StringBuilder stringBuilder;
				stringBuilder = new StringBuilder();
				stringBuilder.append(workspaceId).append("/")
					.append(sessionId).append("/")
					.append(file.getObjectName());
				result = result && fileManagementService.removeObject(stringBuilder.toString());
			} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
				exception.printStackTrace();
				log.info("{}", exception.getMessage());
				new ApiResponse<>(ErrorCode.ERR_FILE_DELETE_EXCEPTION);
			}
		}

		if (!result) {
			return new ApiResponse<>(ErrorCode.ERR_FILE_DELETE_FAILED);
		}

		FileDeleteResponse fileDeleteResponse = new FileDeleteResponse();
		fileDeleteResponse.setWorkspaceId(workspaceId);
		fileDeleteResponse.setSessionId(sessionId);

		return new ApiResponse<>(fileDeleteResponse);
	}

	@Transactional(readOnly = true)
	public ApiResponse<ShareFileInfoListResponse> getShareFileInfoList(
		String workspaceId,
		String sessionId,
		boolean paging,
		Pageable pageable
	) {

		List<ShareFileInfoResponse> shareFileInfoResponses = new ArrayList<>();
		PageMetadataResponse pageMeta;

		Page<File> shareFilePage = fileRepository.findShareFileByWorkspaceAndSessionId(workspaceId, sessionId, paging, pageable);
		if (!shareFilePage.isEmpty()) {
			shareFileInfoResponses = shareFilePage
				.stream()
				.map(file -> shareFileInfoMapper.toDto(file))
				.collect(Collectors.toList());
		}

		for (ShareFileInfoResponse shareFileInfoResponse : shareFileInfoResponses) {
			ApiResponse<String> downloadUrl = downloadFileUrl(
				workspaceId,
				sessionId,
				shareFileInfoResponse.getObjectName()+"_thumbnail",
				shareFileInfoResponse.getName()
			);
			shareFileInfoResponse.setThumbnailDownloadUrl(downloadUrl.getData());
		}

		if (paging) {
			pageMeta = PageMetadataResponse.builder()
				.currentPage(pageable.getPageNumber())
				.currentSize(pageable.getPageSize())
				.numberOfElements(shareFilePage.getNumberOfElements())
				.totalPage(shareFilePage.getTotalPages())
				.totalElements(shareFilePage.getTotalElements())
				.last(shareFilePage.isLast())
				.build();
		} else {
			pageMeta = PageMetadataResponse.builder()
				.currentPage(1)
				.currentSize(1)
				.numberOfElements(shareFileInfoResponses.size())
				.totalPage(1)
				.totalElements(shareFileInfoResponses.size())
				.last(true)
				.build();
		}
		return new ApiResponse<>(new ShareFileInfoListResponse(shareFileInfoResponses, pageMeta));
	}

	private BufferedImage makePdfThumbnail(MultipartFile targetFile) {
		BufferedImage pdfThumbnail = null;
		try {
			java.io.File file = convertFile(targetFile);
			PDDocument document = PDDocument.load(file);
			PDFRenderer pdfRenderer = new PDFRenderer(document);

			pdfThumbnail = pdfRenderer.renderImageWithDPI( 0, 100, ImageType.RGB );
			document.close();
		} catch (Exception e) {
			System.out.println("Error : " + e.toString());
		}
		return pdfThumbnail;
	}

	private BufferedImage makeThumbnail(MultipartFile targetFile) {
		BufferedImage responseImg;
		try {
			if (targetFile.getContentType().equals("application/pdf")) {
				responseImg = makePdfThumbnail(targetFile);
			} else {
				InputStream in = targetFile.getInputStream();
				BufferedImage originalImage = ImageIO.read(in);
				responseImg = Scalr.resize(originalImage, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
				in.close();
			}
		} catch (Exception e) {
			responseImg = null;
		}
		return responseImg;
	}

	private java.io.File convertFile(MultipartFile multipartFile) throws IOException {
		java.io.File file = new java.io.File(multipartFile.getOriginalFilename());
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(multipartFile.getBytes());
		fos.close();
		return file;
	}

	public ApiResponse<String> downloadGuideFileUrl(String objectName) {
		String url;
		try {
			int expiry = 60 * 60 * 24; //one day
			url = fileManagementService.filePreSignedUrl("guide", objectName, expiry);
			if (Strings.isBlank(url)) {
				return new ApiResponse<>(ErrorCode.ERR_FILE_NOT_FOUND);
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			return new ApiResponse<>(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
		return new ApiResponse<>(url);
	}

	@Transactional
	public void removeFiles(String sessionId) {

		String workspaceId = sessionTransactionalService.getRoom(sessionId).getWorkspaceId();
		if (!StringUtils.isEmpty(workspaceId)) {
			try {

				List<String> listName = new LinkedList<>();
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

}
