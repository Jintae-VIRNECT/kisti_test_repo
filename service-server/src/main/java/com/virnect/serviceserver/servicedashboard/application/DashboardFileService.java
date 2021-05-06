package com.virnect.serviceserver.servicedashboard.application;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.serviceserver.servicedashboard.dto.request.FileDataRequest;
import com.virnect.serviceserver.servicedashboard.dto.response.FileDeleteResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FileDetailInfoListResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FileDetailInfoResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FileInfoListResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FileInfoResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FilePreSignedResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FileUserInfoResponse;
import com.virnect.data.application.record.RecordRestService;
import com.virnect.data.application.user.UserRestService;
import com.virnect.data.application.workspace.WorkspaceRestService;
import com.virnect.data.dao.file.FileRepository;
import com.virnect.data.dao.file.RecordFileRepository;
import com.virnect.data.domain.file.File;
import com.virnect.data.domain.file.FileType;
import com.virnect.data.domain.file.RecordFile;
import com.virnect.data.dto.rest.RecordServerFileInfoListResponse;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.file.IFileManagementService;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional
public class DashboardFileService {

	private final ModelMapper modelMapper;

	private final WorkspaceRestService workspaceRestService;
	private final UserRestService userRestService;
	private final IFileManagementService fileManagementService;
	private final RecordRestService recordRestService;
	private final FileRepository fileRepository;
	private final RecordFileRepository recordFileRepository;

	String generateDirPath(String... args) {
		StringBuilder stringBuilder;
		stringBuilder = new StringBuilder();
		for (String argument : args) {
			stringBuilder.append(argument).append("/");
		}
		return stringBuilder.toString();
	}

	/**
	 * 첨부파일 목록 요청 처리
	 *
	 * @param fileDataRequest - 파일 요청 데이터
	 * @return - 첨부파일 목록
	 */
	public FileInfoListResponse getAttachedFileList(FileDataRequest fileDataRequest) {

		List<FileInfoResponse> fileInfoList;

		try {
			/*List<File> files;

			if (fileDataRequest.isDeleted()) {
				files = fileRepository.findByWorkspaceIdAndSessionIdAndDeleted(
					fileDataRequest.getWorkspaceId(),
					fileDataRequest.getSessionId(),
					fileDataRequest.isDeleted()
				);
			} else {
				files = fileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsFalse(
					fileDataRequest.getWorkspaceId(),
					fileDataRequest.getSessionId()
				);
			}*/

			List<File> files = fileRepository.findByWorkspaceIdAndSessionIdAndDeleted(
				fileDataRequest.getWorkspaceId(),
				fileDataRequest.getSessionId(),
				fileDataRequest.isDeleted()
			);

			fileInfoList = files
				.stream()
				.map(file -> modelMapper.map(file, FileInfoResponse.class))
				.sorted((Comparator.comparing(FileInfoResponse::getCreatedDate)))
				.collect(Collectors.toList());

			for (FileInfoResponse file : fileInfoList) {
				ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo
					= workspaceRestService.getWorkspaceMemberInfo(fileDataRequest.getWorkspaceId(), file.getUuid());
				file.setNickName(workspaceMemberInfo.getData().getNickName());
			}
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_ATTACHED_FILE_FOUND);
		}
		return new FileInfoListResponse(fileInfoList);
	}

	/**
	 * 로컬 녹화 파일 목록 요청 처리
	 *
	 * @param fileDataRequest - 파일 요청 데이터
	 * @return - 로컬 녹화 파일 목록
	 */
	public FileDetailInfoListResponse getLocalRecordFileList(FileDataRequest fileDataRequest) {

		List<FileDetailInfoResponse> fileDetailInfoList = new ArrayList<>();

		/*List<RecordFile> recordFiles;

		if (fileDataRequest.isDeleted()) {
			recordFiles = recordFileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsTrue(
				fileDataRequest.getWorkspaceId(),
				fileDataRequest.getSessionId()
			);
		} else {
			recordFiles = recordFileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsFalse(
				fileDataRequest.getWorkspaceId(),
				fileDataRequest.getSessionId()
			);
		}*/

		List<RecordFile> recordFiles = recordFileRepository.findByWorkspaceIdAndSessionIdAndDeleted(
			fileDataRequest.getWorkspaceId(),
			fileDataRequest.getSessionId(),
			fileDataRequest.isDeleted()
		);

		try {
			for (RecordFile recordFile : recordFiles) {
				ApiResponse<UserInfoResponse> feignResponse = userRestService.getUserInfoByUserId(recordFile.getUuid());
				FileUserInfoResponse fileUserInfoResponse = modelMapper.map(
					feignResponse.getData(), FileUserInfoResponse.class);
				FileDetailInfoResponse fileDetailInfoResponse = modelMapper.map(
					recordFile, FileDetailInfoResponse.class);
				fileDetailInfoResponse.setFileUserInfo(fileUserInfoResponse);
				fileDetailInfoList.add(fileDetailInfoResponse);
			}
			fileDetailInfoList
				.stream()
				.sorted(Comparator.comparing(FileDetailInfoResponse::getCreatedDate));
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_FILE_FIND_LIST_FAILED);
		}
		return new FileDetailInfoListResponse(fileDetailInfoList);
	}

	/**
	 * 로컬 녹화 파일 목록 요청 처리
	 *
	 * @param fileDataRequest - 파일 요청 데이터
	 * @return - 로컬 녹화 파일 목록
	 */
	public RecordServerFileInfoListResponse getServerRecordFileList(FileDataRequest fileDataRequest) {
		RecordServerFileInfoListResponse responseData;
		try {
			responseData = recordRestService.getServerRecordFileList(
				fileDataRequest.getWorkspaceId(),
				fileDataRequest.getUserId(),
				fileDataRequest.getSessionId(),
				fileDataRequest.getOrder()
			).getData();
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_SERVER_RECORD_FILE_FOUND);
		}
		return responseData;
	}

	/**
	 * 첨부파일 URL 요청 처리
	 *
	 * @param fileDataRequest - 파일 요청 데이터
	 * @return - 첨부파일 URL 정보
	 */
	public FilePreSignedResponse getAttachedFileUrl(FileDataRequest fileDataRequest) {

		FilePreSignedResponse filePreSignedResponse;

		File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectName(
			fileDataRequest.getWorkspaceId(),
			fileDataRequest.getSessionId(),
			fileDataRequest.getObjectName()
		).orElse(null);

		if (file != null) {
			try {
				StringBuilder stringBuilder;
				stringBuilder = new StringBuilder();
				stringBuilder.append(fileDataRequest.getWorkspaceId()).append("/")
					.append(fileDataRequest.getSessionId()).append("/")
					.append(file.getObjectName());

				String bucketPath = generateDirPath(fileDataRequest.getWorkspaceId(), fileDataRequest.getSessionId());

				int expiry = 60 * 60 * 24; //one day

				String url = fileManagementService.filePreSignedUrl(
					bucketPath,
					fileDataRequest.getObjectName(),
					expiry,
					file.getName(),
					FileType.FILE
				);
				filePreSignedResponse = modelMapper.map(file, FilePreSignedResponse.class);
				filePreSignedResponse.setExpiry(expiry);
				filePreSignedResponse.setUrl(url);

			} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
				throw new RestServiceException(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
			}
		} else {
			throw new RestServiceException(ErrorCode.ERR_FILE_NOT_FOUND);
		}

		return filePreSignedResponse;
	}

	/**
	 * 로컬 녹화 파일 다운로드 URL 요청 처리
	 *
	 * @param fileDataRequest - 파일 요청 데이터
	 * @return - 로컬 녹화 파일  URL 정보
	 */
	public FilePreSignedResponse getLocalRecordFileUrl(FileDataRequest fileDataRequest) {

		FilePreSignedResponse filePreSignedResponse;

		RecordFile recordFile = recordFileRepository.findByWorkspaceIdAndSessionIdAndObjectName(
			fileDataRequest.getWorkspaceId(),
			fileDataRequest.getSessionId(),
			fileDataRequest.getObjectName()
		).orElse(null);

		if (recordFile != null) {

			try {
				StringBuilder stringBuilder;
				stringBuilder = new StringBuilder();
				stringBuilder.append(fileDataRequest.getWorkspaceId()).append("/")
					.append(fileDataRequest.getSessionId()).append("/")
					.append(recordFile.getObjectName());
				// upload to file storage
				String bucketPath = generateDirPath(fileDataRequest.getWorkspaceId(), fileDataRequest.getSessionId());
				int expiry = 60 * 60 * 24; //one day

				String url = fileManagementService.filePreSignedUrl(
					bucketPath,
					fileDataRequest.getObjectName(),
					expiry,
					recordFile.getName(),
					FileType.RECORD
				);

				filePreSignedResponse = modelMapper.map(recordFile, FilePreSignedResponse.class);
				filePreSignedResponse.setExpiry(expiry);
				filePreSignedResponse.setUrl(url);
			} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
				throw new RestServiceException(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
			}
		} else {
			throw new RestServiceException(ErrorCode.ERR_FILE_NOT_FOUND);
		}
		return filePreSignedResponse;
	}

	/**
	 * 서버 녹화 파일 다운로드 URL 요청 처리
	 *
	 * @param fileDataRequest - 파일 요청 데이터
	 * @return - 서버 녹화 파일  URL 정보
	 */
	public String getServerRecordFileUrl(FileDataRequest fileDataRequest) {
		String responseUrl;
		try {
			responseUrl = recordRestService.getServerRecordFileDownloadUrl(
				fileDataRequest.getWorkspaceId(),
				fileDataRequest.getUserId(),
				fileDataRequest.getId()
			).getData();
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_SERVER_RECORD_URL_FOUND);
		}
		return responseUrl;
	}

	/**
	 * 첨부파일 삭제 요청 처리
	 *
	 * @param fileDataRequest - 파일 요청 데이터
	 * @return - 첨부파일 삭제 결과
	 */
	public FileDeleteResponse deleteAttachedFile(FileDataRequest fileDataRequest) {

		FileDeleteResponse fileDeleteResponse = new FileDeleteResponse();

		File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectName(
			fileDataRequest.getWorkspaceId(),
			fileDataRequest.getSessionId(),
			fileDataRequest.getObjectName()
		).orElse(null);

		if (file != null) {
			fileRepository.delete(file);

			boolean result = false;
			try {
				StringBuilder stringBuilder;
				stringBuilder = new StringBuilder();
				stringBuilder.append(fileDataRequest.getWorkspaceId()).append("/")
					.append(fileDataRequest.getSessionId()).append("/")
					.append("file").append("/")
					.append(file.getObjectName());
				result = fileManagementService.removeObject(stringBuilder.toString());
			} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
				exception.printStackTrace();
			}
			if (result) {
				fileDeleteResponse.setWorkspaceId(file.getWorkspaceId());
				fileDeleteResponse.setSessionId(file.getSessionId());
				fileDeleteResponse.setFileName(file.getName());
			} else {
				throw new RestServiceException(ErrorCode.ERR_FILE_DELETE_FAILED);
			}
		} else {
			throw new RestServiceException(ErrorCode.ERR_FILE_NOT_FOUND);
		}

		return fileDeleteResponse;
	}

	/**
	 * 로컬 녹화 파일 삭제 요청 처리
	 *
	 * @param fileDataRequest - 파일 요청 데이터
	 * @return - 삭제 결과
	 */
	public FileDeleteResponse deleteLocalRecordFileUrl(FileDataRequest fileDataRequest) {

		FileDeleteResponse fileDeleteResponse = new FileDeleteResponse();

		RecordFile file = recordFileRepository.findByWorkspaceIdAndSessionIdAndObjectName(
			fileDataRequest.getWorkspaceId(),
			fileDataRequest.getSessionId(),
			fileDataRequest.getObjectName()
		).orElse(null);

		recordFileRepository.delete(file);

		//remove object
		boolean result = false;

		try {
			StringBuilder stringBuilder;
			stringBuilder = new StringBuilder();
			stringBuilder.append(fileDataRequest.getWorkspaceId()).append("/")
				.append(fileDataRequest.getSessionId()).append("/")
				.append("record").append("/")
				.append(file.getObjectName());
			result = fileManagementService.removeObject(stringBuilder.toString());
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			exception.printStackTrace();
		}
		if (result) {
			fileDeleteResponse.setWorkspaceId(file.getWorkspaceId());
			fileDeleteResponse.setSessionId(file.getSessionId());
			fileDeleteResponse.setFileName(file.getName());
		} else {
			throw new RestServiceException(ErrorCode.ERR_FILE_DELETE_FAILED);
		}
		return fileDeleteResponse;
	}

	/**
	 * 서버 녹화 파일 삭제 요청 처리
	 *
	 * @param fileDataRequest - 파일 요청 데이터
	 * @return - 삭제 결과
	 */
	public Object deleteServerRecordFileUrl(FileDataRequest fileDataRequest) {
		Object responseData;
		try {
			responseData = recordRestService.deleteServerRecordFile(
				fileDataRequest.getWorkspaceId(),
				fileDataRequest.getUserId(),
				fileDataRequest.getId()
			).getData();
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_SERVER_RECORD_DELETE);
		}
		return responseData;
	}

}
