package com.virnect.remote.application;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dao.file.FileRepository;
import com.virnect.data.dao.file.RecordFileRepository;
import com.virnect.data.dao.room.RoomRepository;
import com.virnect.data.domain.file.File;
import com.virnect.data.domain.file.FileType;
import com.virnect.data.domain.file.RecordFile;
import com.virnect.data.domain.room.Room;
import com.virnect.data.application.user.UserRestService;
import com.virnect.remote.dto.request.file.FileUploadRequest;
import com.virnect.remote.dto.request.file.RecordFileUploadRequest;
import com.virnect.remote.dto.request.file.RoomProfileUpdateRequest;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.remote.dto.response.ResultResponse;
import com.virnect.remote.dto.response.file.FileDeleteResponse;
import com.virnect.remote.dto.response.file.FileDetailInfoListResponse;
import com.virnect.remote.dto.response.file.FileDetailInfoResponse;
import com.virnect.remote.dto.response.file.FileInfoListResponse;
import com.virnect.remote.dto.response.file.FileInfoResponse;
import com.virnect.remote.dto.response.file.FilePreSignedResponse;
import com.virnect.remote.dto.response.file.FileUploadResponse;
import com.virnect.remote.dto.response.file.FileUserInfoResponse;
import com.virnect.remote.dto.response.file.RoomProfileUpdateResponse;
import com.virnect.data.dto.UploadResult;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.file.IFileManagementService;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

	private final IFileManagementService fileManagementService;
	private final UserRestService userRestService;
	private final ModelMapper modelMapper;

	private final FileRepository fileRepository;
	private final RoomRepository roomRepository;
	private final RecordFileRepository recordFileRepository;

	private String generateDirPath(String... args) {
		StringBuilder stringBuilder;
		stringBuilder = new StringBuilder();
		for (String argument : args) {
			stringBuilder.append(argument).append("/");
		}
		return stringBuilder.toString();
	}

	public ApiResponse<FileUploadResponse> uploadFile(FileUploadRequest fileUploadRequest) {

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
			responseData = new ApiResponse<>(
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
			.build();

		File uploadResult = fileRepository.save(file);

		if (uploadResult != null) {
			FileUploadResponse fileUploadResponse = modelMapper.map(file, FileUploadResponse.class);
			responseData = new ApiResponse<>(fileUploadResponse);
		} else {
			responseData = new ApiResponse<>(new FileUploadResponse(), ErrorCode.ERR_FILE_UPLOAD_FAILED);
		}
		return responseData;
	}

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

		if (uploadResult != null) {
			FileUploadResponse fileUploadResponse = modelMapper.map(recordFile, FileUploadResponse.class);
			responseData = new ApiResponse<>(fileUploadResponse);
		} else {
			responseData = new ApiResponse<>(new FileUploadResponse(), ErrorCode.ERR_FILE_UPLOAD_FAILED);
		}
		return responseData;
	}

	public ApiResponse<RoomProfileUpdateResponse> profileUpload(
		String workspaceId,
		String sessionId,
		RoomProfileUpdateRequest roomProfileUpdateRequest
	) {

		ApiResponse<RoomProfileUpdateResponse> responseData;
		RoomProfileUpdateResponse roomProfileUpdateResponse = new RoomProfileUpdateResponse();
		String profileUrl = IFileManagementService.DEFAULT_ROOM_PROFILE;
		Room room = roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);

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

				if (uploadResult != null) {
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

	public ApiResponse<ResultResponse> deleteProfile(
		String workspaceId,
		String sessionId
	) {

		ApiResponse<ResultResponse> responseData;
		ResultResponse resultResponse = new ResultResponse();

		Room room = roomRepository.findRoomByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);
		if (room != null) {
			try {

				fileManagementService.deleteProfile(room.getProfile());
				room.setProfile(IFileManagementService.DEFAULT_ROOM_PROFILE);
				roomRepository.save(room);

			} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
				exception.printStackTrace();
			}
			resultResponse.setResult(true);
			responseData = new ApiResponse(resultResponse);
		} else {
			responseData = new ApiResponse(ErrorCode.ERR_ROOM_NOT_FOUND);
		}
		return responseData;
	}

	public ApiResponse<FilePreSignedResponse> downloadFileUrl(
		String workspaceId,
		String sessionId,
		String userId,
		String objectName
	) {
		ApiResponse<FilePreSignedResponse> responseData;
		File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName).orElse(null);
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
				responseData = new ApiResponse(filePreSignedResponse);
			} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
				log.info("{}", exception.getMessage());
				responseData = new ApiResponse(new FilePreSignedResponse(), ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
			}
		} else {
			responseData = new ApiResponse(new FilePreSignedResponse(), ErrorCode.ERR_FILE_NOT_FOUND);
		}
		return responseData;
	}

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

	public ApiResponse<FileInfoListResponse> getFileInfoList(
		String workspaceId,
		String sessionId,
		String userId,
		boolean isDeleted,
		PageRequest pageable
	) {

		ApiResponse<FileInfoListResponse> responseData;

		Page<File> filePage;
		if (isDeleted) {
			filePage = fileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsTrue(
				workspaceId, sessionId, pageable);
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

	public ApiResponse<FileDeleteResponse> removeFile(
		String workspaceId,
		String sessionId,
		String userId,
		String objectName
	) {

		ApiResponse<FileDeleteResponse> responseData;
		File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName).orElse(null);
		file.setDeleted(true);
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
			responseData = new ApiResponse<>(new FileDeleteResponse(), ErrorCode.ERR_FILE_DELETE_EXCEPTION);
		}
		if (result) {
			FileDeleteResponse fileDeleteResponse = new FileDeleteResponse();
			fileDeleteResponse.setWorkspaceId(file.getWorkspaceId());
			fileDeleteResponse.setSessionId(file.getSessionId());
			fileDeleteResponse.setFileName(file.getName());
			responseData = new ApiResponse<>(fileDeleteResponse);
		} else {
			responseData = new ApiResponse<>(new FileDeleteResponse(), ErrorCode.ERR_FILE_DELETE_FAILED);
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

	public Page<File> getFileList(String workspaceId, String sessionId, Pageable pageable, boolean isDeleted) {
		if(isDeleted)
			return this.fileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsTrue(workspaceId, sessionId, pageable);
		else
			return this.fileRepository.findByWorkspaceIdAndSessionId(workspaceId, sessionId, pageable);
	}

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
}
