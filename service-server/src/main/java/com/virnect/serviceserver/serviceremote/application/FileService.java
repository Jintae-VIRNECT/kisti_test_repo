package com.virnect.serviceserver.serviceremote.application;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.imgscalr.Scalr;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
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
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.file.IFileManagementService;
import com.virnect.serviceserver.serviceremote.dto.request.file.FileUploadRequest;
import com.virnect.serviceserver.serviceremote.dto.request.file.RecordFileUploadRequest;
import com.virnect.serviceserver.serviceremote.dto.request.file.RoomProfileUpdateRequest;
import com.virnect.serviceserver.serviceremote.dto.response.ResultResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.FileDeleteResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.FileDetailInfoListResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.FileDetailInfoResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.FileInfoListResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.FileInfoResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.FilePreSignedResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.FileUploadResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.FileUserInfoResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.RoomProfileUpdateResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.ShareFileInfoListResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.ShareFileInfoResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.ShareFileUploadResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

	private final int THUMBNAIL_WIDTH = 120;
	private final int THUMBNAIL_HEIGHT = 70;

	private final IFileManagementService fileManagementService;
	private final UserRestService userRestService;
	private final ModelMapper modelMapper;

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

		ApiResponse<FileUploadResponse> responseData;

		String bucketPath = generateDirPath(
			fileUploadRequest.getWorkspaceId(), fileUploadRequest.getSessionId());
		String objectName = null;
		try {
			UploadResult uploadResult = fileManagementService.upload(
				fileUploadRequest.getFile(), bucketPath, FileType.FILE);
			ErrorCode errorCode = uploadResult.getErrorCode();
			switch (errorCode) {
						case ERR_FILE_ASSUME_DUMMY:
						case ERR_FILE_UNSUPPORTED_EXTENSION:
						case ERR_FILE_SIZE_LIMIT:
							return new ApiResponse<>(new FileUploadResponse(), errorCode);
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

		if (!ObjectUtils.isEmpty(uploadResult)) {
			FileUploadResponse fileUploadResponse = modelMapper.map(file, FileUploadResponse.class);
			responseData = new ApiResponse<>(fileUploadResponse);
		} else {
			responseData = new ApiResponse<>(new FileUploadResponse(), ErrorCode.ERR_FILE_UPLOAD_FAILED);
		}
		return responseData;
	}

	@Transactional
	public ApiResponse<FileUploadResponse> uploadRecordFile(RecordFileUploadRequest recordFileUploadRequest) {

		ApiResponse<FileUploadResponse> responseData;

		// upload to file storage
		String bucketPath = generateDirPath(
			recordFileUploadRequest.getWorkspaceId(),
			recordFileUploadRequest.getSessionId()
		);

		String objectName = null;
		try {
			UploadResult uploadResult = fileManagementService.upload(
				recordFileUploadRequest.getFile(), bucketPath, FileType.RECORD);
			ErrorCode errorCode = uploadResult.getErrorCode();
			switch (errorCode) {
				case ERR_FILE_ASSUME_DUMMY:
				case ERR_FILE_UNSUPPORTED_EXTENSION:
				case ERR_FILE_SIZE_LIMIT:
					return new ApiResponse<>(new FileUploadResponse(), errorCode);
				case ERR_SUCCESS:
					objectName = uploadResult.getResult();
					break;
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			return new ApiResponse<>(
				new FileUploadResponse(),
				ErrorCode.ERR_FILE_UPLOAD_EXCEPTION.getCode(),
				exception.getMessage()
			);
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

		RecordFile uploadResult = recordFileRepository.save(recordFile);

		if (!ObjectUtils.isEmpty(uploadResult)) {
			FileUploadResponse fileUploadResponse = modelMapper.map(recordFile, FileUploadResponse.class);
			responseData = new ApiResponse<>(fileUploadResponse);
		} else {
			responseData = new ApiResponse<>(new FileUploadResponse(), ErrorCode.ERR_FILE_UPLOAD_FAILED);
		}
		return responseData;
	}

	@Transactional
	public ApiResponse<RoomProfileUpdateResponse> profileUpload(
		String workspaceId,
		String sessionId,
		RoomProfileUpdateRequest roomProfileUpdateRequest
	) {
		ApiResponse<RoomProfileUpdateResponse> responseData;
		RoomProfileUpdateResponse roomProfileUpdateResponse = new RoomProfileUpdateResponse();
		String profileUrl = IFileManagementService.DEFAULT_ROOM_PROFILE;
		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId).orElse(null);
		if (room != null) {
			if (room.getLeaderId().equals(roomProfileUpdateRequest.getUuid())) {
				if (roomProfileUpdateRequest.getProfile() != null) {
					try {
						UploadResult uploadResult = fileManagementService.uploadProfile(
							roomProfileUpdateRequest.getProfile(),
							null
						);
						ErrorCode errorCode = uploadResult.getErrorCode();
						switch (errorCode) {
							case ERR_FILE_ASSUME_DUMMY:
							case ERR_FILE_UNSUPPORTED_EXTENSION:
							case ERR_FILE_SIZE_LIMIT:
								return new ApiResponse<>(new RoomProfileUpdateResponse(), errorCode);
							case ERR_SUCCESS:
								profileUrl = uploadResult.getResult();
								break;
						}

						fileManagementService.deleteProfile(room.getProfile());
					} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
						log.info("{}", exception.getMessage());
						return new ApiResponse<>(
							new RoomProfileUpdateResponse(),
							ErrorCode.ERR_FILE_UPLOAD_EXCEPTION.getCode(),
							exception.getMessage()
						);
					}
				}
				roomProfileUpdateResponse.setSessionId(sessionId);
				roomProfileUpdateResponse.setProfile(profileUrl);
				room.setProfile(profileUrl);

				Room uploadResult = roomRepository.save(room);

				if (!ObjectUtils.isEmpty(uploadResult)) {
					responseData = new ApiResponse<>(roomProfileUpdateResponse);
				} else {
					responseData = new ApiResponse<>(new RoomProfileUpdateResponse(), ErrorCode.ERR_PROFILE_UPLOAD_FAILED);
				}
			} else {
				responseData = new ApiResponse<>(
					new RoomProfileUpdateResponse(), ErrorCode.ERR_ROOM_INVALID_PERMISSION);
			}
		} else {
			responseData = new ApiResponse<>(new RoomProfileUpdateResponse(), ErrorCode.ERR_ROOM_NOT_FOUND);
		}
		return responseData;
	}

	@Transactional
	public ApiResponse<ResultResponse> deleteProfile(
		String workspaceId,
		String sessionId
	) {

		ApiResponse<ResultResponse> responseData;
		ResultResponse resultResponse = new ResultResponse();

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionIdForWrite(workspaceId, sessionId).orElse(null);
		if (room != null) {
			try {

				fileManagementService.deleteProfile(room.getProfile());
				room.setProfile(IFileManagementService.DEFAULT_ROOM_PROFILE);
				roomRepository.save(room);

			} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
				exception.printStackTrace();
			}
			resultResponse.setResult(true);
			responseData = new ApiResponse<>(resultResponse);
		} else {
			responseData = new ApiResponse<>(ErrorCode.ERR_ROOM_NOT_FOUND);
		}
		return responseData;
	}

	@Transactional(readOnly = true)
	public ApiResponse<FilePreSignedResponse> downloadFileUrl(
		String workspaceId,
		String sessionId,
		String userId,
		String objectName,
		FileType fileType
	) {
		ApiResponse<FilePreSignedResponse> responseData;
		//File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName).orElse(null);
		File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectNameAndFileType(workspaceId, sessionId, objectName, fileType).orElse(null);
		if (file != null) {
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
				responseData = new ApiResponse<>(filePreSignedResponse);
			} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
				log.info("{}", exception.getMessage());
				responseData = new ApiResponse<>(new FilePreSignedResponse(), ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
			}
		} else {
			responseData = new ApiResponse<>(new FilePreSignedResponse(), ErrorCode.ERR_FILE_NOT_FOUND);
		}
		return responseData;
	}

	@Transactional(readOnly = true)
	public ApiResponse<String> downloadFileUrl(File targetFile) {
		ApiResponse<String> responseData;
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
				responseData = new ApiResponse<>(url);
			} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
				log.info("{}", exception.getMessage());
				responseData = new ApiResponse<>(null, ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
			}
		return responseData;
	}

	@Transactional(readOnly = true)
	public ApiResponse<String> downloadFileUrl(String workspace, String sessionId, String objectName, String name) {
		ApiResponse<String> responseData;
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
			responseData = new ApiResponse<>(url);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			responseData = new ApiResponse<>(null, ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
		return responseData;
	}

	@Transactional(readOnly = true)
	public ApiResponse<FilePreSignedResponse> downloadRecordFileUrl(
		String workspaceId,
		String sessionId,
		String userId,
		String objectName
	) {

		ApiResponse<FilePreSignedResponse> responseData;

		RecordFile recordFile = recordFileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName)
			.orElse(null);
		if (recordFile != null) {
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
				responseData = new ApiResponse<>(filePreSignedResponse);
			} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
				log.info("{}", exception.getMessage());
				responseData = new ApiResponse<>(new FilePreSignedResponse(), ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
			}
		} else {
			responseData = new ApiResponse<>(new FilePreSignedResponse(), ErrorCode.ERR_FILE_NOT_FOUND);
		}
		return responseData;
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

		ApiResponse<FileInfoListResponse> responseData;

		Page<File> filePage;
		if (isDeleted) {
			filePage = fileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsTrueAndFileType(
				workspaceId, sessionId, pageable, fileType);
		} else {
			filePage = fileRepository.findByWorkspaceIdAndSessionId(workspaceId, sessionId, pageable);
		}
		for (File file : filePage.toList()) {
			log.info("getFileInfoList : {}", file.getObjectName());
		}

		// Page Metadata
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
			.map(file -> modelMapper.map(file, FileInfoResponse.class))
			.collect(Collectors.toList());

		responseData = new ApiResponse<>(new FileInfoListResponse(fileInfoList, pageMeta));

		return responseData;
	}

	@Transactional(readOnly = true)
	public ApiResponse<FileDetailInfoListResponse> getRecordFileInfoList(
		String workspaceId,
		String sessionId,
		String userId,
		boolean isDeleted,
		PageRequest pageable
	) {

		ApiResponse<FileDetailInfoListResponse> responseData;

		Page<RecordFile> recordFilePage;

		if (isDeleted) {
			recordFilePage = recordFileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsTrue(
				workspaceId, sessionId, pageable);
		} else {
			recordFilePage = recordFileRepository.findByWorkspaceIdAndSessionId(workspaceId, sessionId, pageable);
		}

		List<FileDetailInfoResponse> fileDetailInfoList = new ArrayList<>();
		for (RecordFile recordFile : recordFilePage.toList()) {
			log.info("getRecordFileInfoList : {}", recordFile.getObjectName());
			ApiResponse<UserInfoResponse> feignResponse = userRestService.getUserInfoByUserId(
				recordFile.getUuid());
			FileUserInfoResponse fileUserInfoResponse = modelMapper.map(
				feignResponse.getData(), FileUserInfoResponse.class);

			FileDetailInfoResponse fileDetailInfoResponse = modelMapper.map(
				recordFile, FileDetailInfoResponse.class);
			log.info("getRecordFileInfoList : {}", fileUserInfoResponse.toString());
			fileDetailInfoResponse.setFileUserInfo(fileUserInfoResponse);
			fileDetailInfoList.add(fileDetailInfoResponse);
		}

		// Page Metadata
		PageMetadataResponse pageMeta = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.numberOfElements(recordFilePage.getNumberOfElements())
			.totalPage(recordFilePage.getTotalPages())
			.totalElements(recordFilePage.getTotalElements())
			.last(recordFilePage.isLast())
			.build();

		responseData = new ApiResponse<>(new FileDetailInfoListResponse(fileDetailInfoList, pageMeta));

		return responseData;
	}

	@Transactional
	public ApiResponse<FileDeleteResponse> removeFile(
		String workspaceId,
		String sessionId,
		String userId,
		String objectName,
		FileType fileType
	) {

		ApiResponse<FileDeleteResponse> responseData;

		//File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName).orElse(null);
		File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectNameAndFileType(workspaceId, sessionId, objectName, fileType).orElse(null);

		if (!ObjectUtils.isEmpty(file)) {
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
				new ApiResponse<>(new FileDeleteResponse(), ErrorCode.ERR_FILE_DELETE_EXCEPTION);
			}
			if (result) {
				FileDeleteResponse fileDeleteResponse = new FileDeleteResponse();
				fileDeleteResponse.setWorkspaceId(file.getWorkspaceId());
				fileDeleteResponse.setSessionId(file.getSessionId());
				fileDeleteResponse.setObjectName(file.getName());
				responseData = new ApiResponse<>(fileDeleteResponse);
			} else {
				responseData = new ApiResponse<>(new FileDeleteResponse(), ErrorCode.ERR_FILE_DELETE_FAILED);
			}
		} else {
			responseData = new ApiResponse<>(new FileDeleteResponse(), ErrorCode.ERR_FILE_NOT_FOUND);
		}
		return responseData;
	}

	public ApiResponse<String> downloadGuideFileUrl(String objectName) {

		ApiResponse<String> responseData;

		String url;
		try {
			int expiry = 60 * 60 * 24; //one day
			url = fileManagementService.filePreSignedUrl("guide", objectName, expiry);
			if (url == null) {
				responseData = new ApiResponse<>("", ErrorCode.ERR_FILE_NOT_FOUND);
			} else {
				responseData = new ApiResponse<>(url);
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			log.info("{}", exception.getMessage());
			responseData = new ApiResponse<>("", ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
		return responseData;
	}

	@Transactional
	public void removeFiles(String sessionId) {

		String workspaceId = sessionTransactionalService.getRoom(sessionId).getWorkspaceId();
		if (workspaceId != null) {
			try {

				List<String> listName = new LinkedList<>();
				List<File> files = getFileList(workspaceId, sessionId);
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
			List<File> files = getFileList(workspaceId, sessionId);
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

	@Transactional
	public File registerFile(FileUploadRequest fileUploadRequest, String objectName) {
		File file = File.builder()
			.workspaceId(fileUploadRequest.getWorkspaceId())
			.sessionId(fileUploadRequest.getSessionId())
			.uuid(fileUploadRequest.getUserId())
			.name(fileUploadRequest.getFile().getOriginalFilename())
			.objectName(objectName)
			.contentType(fileUploadRequest.getFile().getContentType())
			.size(fileUploadRequest.getFile().getSize())
			.build();

		return fileRepository.save(file);
	}

	@Transactional
	public RecordFile registerRecordFile(RecordFileUploadRequest recordFileUploadRequest, String objectName) {
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

		return recordFileRepository.save(recordFile);
	}

	public File getFileByName(String workspaceId, String sessionId, String name) {
		return this.fileRepository.findByWorkspaceIdAndSessionIdAndName(workspaceId, sessionId, name).orElse(null);
	}

	public File getFileByObjectName(String workspaceId, String sessionId, String objectName) {
		return this.fileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName).orElse(null);
	}

	public RecordFile getRecordFileByObjectName(String workspaceId, String sessionId, String objectName) {
		return this.recordFileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName).orElse(null);
	}

	/*public Page<File> getFileList(String workspaceId, String sessionId, Pageable pageable, boolean isDeleted) {
		if(isDeleted)
			return this.fileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsTrueAndFileType(workspaceId, sessionId, pageable);
		else
			return this.fileRepository.findByWorkspaceIdAndSessionId(workspaceId, sessionId, pageable);
	}*/

	public List<File> getFileList(String workspaceId, String sessionId) {
		return this.fileRepository.findByWorkspaceIdAndSessionId(workspaceId, sessionId);
	}

	public Page<RecordFile> getRecordFileList(String workspaceId, String sessionId, Pageable pageable, boolean isDeleted) {
		if(isDeleted)
			return this.recordFileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsTrue(workspaceId, sessionId, pageable);
		else
			return this.recordFileRepository.findByWorkspaceIdAndSessionId(workspaceId, sessionId, pageable);
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

		FileUploadResult response;

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
			new FileUploadResult(null, ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
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

		FileUploadResult response;

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
			new FileUploadResult(null, ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
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

		if (
			thumbnailUploadResult.getErrorCode() != ErrorCode.ERR_SUCCESS || fileUploadResult.getErrorCode() != ErrorCode.ERR_SUCCESS
		) {
			new ApiResponse<>(new ShareFileUploadResponse(), fileUploadResult.getErrorCode());
		}

		ShareFileUploadResponse fileUploadResponse = modelMapper.map(fileUploadResult.getFile(), ShareFileUploadResponse.class);

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
			new ApiResponse<>(new FileDeleteResponse(), ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID);
		}

		File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectNameAndFileType(workspaceId, sessionId, objectName, fileType).orElse(null);
		if (Objects.isNull(file)) {
			new ApiResponse<>(new FileDeleteResponse(), ErrorCode.ERR_FILE_NOT_FOUND);
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
				new ApiResponse<>(new FileDeleteResponse(), ErrorCode.ERR_FILE_DELETE_FAILED);
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			exception.printStackTrace();
			log.info("{}", exception.getMessage());
			new ApiResponse<>(new FileDeleteResponse(), ErrorCode.ERR_FILE_DELETE_EXCEPTION);
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
		ApiResponse<FileDeleteResponse> responseData;

		// Leader id check
		Member leaderInfo = memberRepository.findRoomLeaderBySessionId(workspaceId, sessionId);
		if (!leaderUserId.equals(leaderInfo.getUuid())) {
			new ApiResponse<>(new FileDeleteResponse(), ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID);
		}

		List<File> files = fileRepository.findByWorkspaceIdAndSessionIdAndFileType(workspaceId, sessionId, fileType);

		boolean result = true;
		for (File file : files) {
			if (!files.isEmpty()) {
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
					new ApiResponse<>(new FileDeleteResponse(), ErrorCode.ERR_FILE_DELETE_EXCEPTION);
				}
			} else {
				new ApiResponse<>(new FileDeleteResponse(), ErrorCode.ERR_FILE_NOT_FOUND);
			}
		}

		if (result) {
			FileDeleteResponse fileDeleteResponse = new FileDeleteResponse();
			fileDeleteResponse.setWorkspaceId(workspaceId);
			fileDeleteResponse.setSessionId(sessionId);
			//fileDeleteResponse.setFileName(file.getName());
			responseData = new ApiResponse<>(fileDeleteResponse);
		} else {
			responseData = new ApiResponse<>(new FileDeleteResponse(), ErrorCode.ERR_FILE_DELETE_FAILED);
		}

		return responseData;
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
			for (File shareFile : shareFilePage) {
				log.info("getFileInfoList : {}", shareFile.getObjectName());
			}
			shareFileInfoResponses = shareFilePage
				.stream()
				.map(file -> modelMapper.map(file, ShareFileInfoResponse.class))
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

	private BufferedImage makeThumbnail(@NotNull MultipartFile targetFile) {
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

	private java.io.File convertFile(@NotNull MultipartFile multipartFile) throws IOException {
		java.io.File file = new java.io.File(multipartFile.getOriginalFilename());
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(multipartFile.getBytes());
		fos.close();
		return file;
	}
}
