package com.virnect.serviceserver.servicedashboard.application;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.application.record.RecordRestService;
import com.virnect.data.application.account.AccountRestService;
import com.virnect.data.application.workspace.WorkspaceRestService;
import com.virnect.data.dao.file.FileRepository;
import com.virnect.data.dao.file.RecordFileRepository;
import com.virnect.data.domain.file.File;
import com.virnect.data.domain.file.FileType;
import com.virnect.data.domain.file.RecordFile;
import com.virnect.data.dto.rest.ListRecordingFilesResponse;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.file.IFileManagementService;
import com.virnect.serviceserver.servicedashboard.dto.mapper.file.DashboardFileInfoMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.file.DashboardFilePreSignedMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.file.DashboardFileUserInfoMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.record.DashboardRecordFileDetailMapper;
import com.virnect.serviceserver.servicedashboard.dto.mapper.record.DashboardRecordFilePreSignedMapper;
import com.virnect.serviceserver.servicedashboard.dto.response.FileDeleteResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FileDetailInfoListResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FileDetailInfoResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FileInfoListResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FileInfoResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FilePreSignedResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FileUserInfoResponse;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional
public class DashboardFileService {

	private final int EXPIRY = 60 * 60 * 24; //one day

	private final WorkspaceRestService workspaceRestService;
	private final AccountRestService accountRestService;
	private final IFileManagementService fileManagementService;
	private final RecordRestService recordRestService;
	private final FileRepository fileRepository;
	private final DashboardFileUserInfoMapper dashboardFileUserInfoMapper;
	private final RecordFileRepository recordFileRepository;

	private final DashboardFileInfoMapper dashboardFileInfoMapper;
	private final DashboardRecordFileDetailMapper dashboardRecordFileDetailMapper;
	private final DashboardFilePreSignedMapper dashboardFilePreSignedMapper;
	private final DashboardRecordFilePreSignedMapper dashboardRecordFilePreSignedMapper;

	String generateDirPath(String... args) {
		StringBuilder stringBuilder;
		stringBuilder = new StringBuilder();
		for (String argument : args) {
			stringBuilder.append(argument).append("/");
		}
		return stringBuilder.toString();
	}

	public FileInfoListResponse getAttachedFileList(
		String workspaceId,
		String sessionId,
		boolean delete
	) {
		List<FileInfoResponse> fileInfoList;
		try {
			List<File> files = fileRepository.findByWorkspaceIdAndSessionIdAndDeleted(workspaceId, sessionId, delete);
			fileInfoList = files
				.stream()
				.map(dashboardFileInfoMapper::toDto)
				.sorted((Comparator.comparing(FileInfoResponse::getCreatedDate)))
				.collect(Collectors.toList());

			for (FileInfoResponse file : fileInfoList) {
				ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMember(workspaceId, file.getUuid());
				file.setNickName(workspaceMemberInfo.getData().getNickName());
			}
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
		return new FileInfoListResponse(fileInfoList);
	}

	public FileDetailInfoListResponse getLocalRecordFileList(
		String workspaceId,
		String sessionId,
		boolean deleted
	) {
		List<FileDetailInfoResponse> fileDetailInfoList = new ArrayList<>();
		try {
			List<RecordFile> recordFiles = recordFileRepository.findByWorkspaceIdAndSessionIdAndDeleted(workspaceId, sessionId, deleted);
			for (RecordFile recordFile : recordFiles) {
				ApiResponse<UserInfoResponse> feignResponse = accountRestService.getUserInfoByUserId(recordFile.getUuid());

				FileUserInfoResponse fileUserInfoResponse = dashboardFileUserInfoMapper.toDto(feignResponse.getData());
				FileDetailInfoResponse fileDetailInfoResponse = dashboardRecordFileDetailMapper.toDto(recordFile);

				fileDetailInfoResponse.setFileUserInfo(fileUserInfoResponse);
				fileDetailInfoList.add(fileDetailInfoResponse);
			}
			fileDetailInfoList.stream().sorted(Comparator.comparing(FileDetailInfoResponse::getCreatedDate));
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_FILE_NOT_FOUND);
		}
		return new FileDetailInfoListResponse(fileDetailInfoList);
	}

	public ListRecordingFilesResponse getServerRecordFileList(
		String workspaceId,
		String sessionId,
		String userId,
		String order
	) {
		ListRecordingFilesResponse responseData;
		try {
			responseData = recordRestService.getServerRecordFileList(workspaceId, userId, sessionId, order).getData();
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
		return responseData;
	}

	public FilePreSignedResponse getAttachedFileUrl(
		String workspaceId,
		String sessionId,
		String objectName
	) {
		FilePreSignedResponse filePreSignedResponse;
		try {
			File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName).orElse(null);
			if (ObjectUtils.isEmpty(file)) {
				throw new RestServiceException(ErrorCode.ERR_FILE_NOT_FOUND);
			}
			String url = fileManagementService.filePreSignedUrl(
				generateDirPath(workspaceId, sessionId),	// bucket path
				objectName,
				EXPIRY,
				file.getName(),
				FileType.FILE
			);
			filePreSignedResponse = dashboardFilePreSignedMapper.toDto(file);
			filePreSignedResponse.setExpiry(EXPIRY);
			filePreSignedResponse.setUrl(url);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			throw new RestServiceException(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
		return filePreSignedResponse;
	}

	public FilePreSignedResponse getLocalRecordFileUrl(
		String workspaceId,
		String sessionId,
		String objectName
	) {
		FilePreSignedResponse filePreSignedResponse;
		try {
			RecordFile recordFile = recordFileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName).orElse(null);
			if (ObjectUtils.isEmpty(recordFile)) {
				throw new RestServiceException(ErrorCode.ERR_FILE_NOT_FOUND);
			}

			String url = fileManagementService.filePreSignedUrl(
				generateDirPath(workspaceId, sessionId),	// bucket path
				objectName,
				EXPIRY,
				recordFile.getName(),
				FileType.RECORD
			);
			filePreSignedResponse = dashboardRecordFilePreSignedMapper.toDto(recordFile);
			filePreSignedResponse.setExpiry(EXPIRY);
			filePreSignedResponse.setUrl(url);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			throw new RestServiceException(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}

		return filePreSignedResponse;
	}

	public String getServerRecordFileUrl(
		String workspaceId,
		String userId,
		String id
	) {
		String responseUrl;
		try {
			responseUrl = recordRestService.getServerRecordFileDownloadUrl(workspaceId, userId, id).getData();
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
		return responseUrl;
	}

	public FileDeleteResponse deleteAttachedFile(
		String workspaceId,
		String sessionId,
		String objectName
	) {
		File file = fileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName).orElse(null);
		if (ObjectUtils.isEmpty(file)) {
			throw new RestServiceException(ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
		}
		try {
			fileRepository.delete(file);

			String stringBuilder = workspaceId + "/"
				+ sessionId + "/"
				+ "file" + "/"
				+ file.getObjectName();
			if (!fileManagementService.removeObject(stringBuilder)) {
				throw new RestServiceException(ErrorCode.ERR_FILE_DELETE_FAILED);
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			exception.printStackTrace();
		}
		return FileDeleteResponse.builder()
			.workspaceId(file.getWorkspaceId())
			.sessionId(file.getSessionId())
			.fileName(file.getName())
			.build();
	}

	public FileDeleteResponse deleteLocalRecordFileUrl(
		String workspaceId,
		String sessionId,
		String objectName
	) {
		RecordFile file = recordFileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName).orElse(null);
		if (ObjectUtils.isEmpty(file)) {
			throw new RestServiceException(ErrorCode.ERR_FILE_NOT_FOUND);
		}
		try {
			recordFileRepository.delete(file);

			String stringBuilder = workspaceId + "/"
				+ sessionId + "/"
				+ "record" + "/"
				+ file.getObjectName();
			if (!fileManagementService.removeObject(stringBuilder)) {
				throw new RestServiceException(ErrorCode.ERR_FILE_DELETE_FAILED);
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
			exception.printStackTrace();
		}
		return FileDeleteResponse.builder()
			.workspaceId(file.getWorkspaceId())
			.sessionId(file.getSessionId())
			.fileName(file.getName())
			.build();
	}

	public Object deleteServerRecordFileUrl(
		String workspaceId,
		String userId,
		String id
	) {
		Object responseData;
		try {
			responseData = recordRestService.deleteServerRecordFile(
				workspaceId,
				userId,
				id
			).getData();
		} catch (Exception exception) {
			throw new RestServiceException(ErrorCode.ERR_FILE_DELETE_EXCEPTION);
		}
		return responseData;
	}

}
