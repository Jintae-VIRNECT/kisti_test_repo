package com.virnect.content.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.dao.content.ContentRepository;
import com.virnect.content.dao.contentdonwloadlog.ContentDownloadLogRepository;
import com.virnect.content.dao.project.ProjectDownloadLogRepository;
import com.virnect.content.dao.project.ProjectRepository;
import com.virnect.content.dto.request.FileResourceUploadRequest;
import com.virnect.content.dto.response.ContentResourceUsageInfoResponse;
import com.virnect.content.dto.response.FileResourceUploadResponse;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.common.FileResourceType;
import com.virnect.content.global.error.ErrorCode;
import com.virnect.content.infra.file.upload.FileUploadService;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-09-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
	private final FileUploadService fileUploadService;
	private final ContentRepository contentRepository;
	private final ProjectRepository projectRepository;
	private final ContentDownloadLogRepository contentDownloadLogRepository;
	private final ProjectDownloadLogRepository projectDownloadLogRepository;
	private static final String PROJECT_DIRECTORY = "project";
	private static final String CONTENT_DIRECTORY = "contents";
	private static final String REPORT_DIRECTORY = "report";
	private static final String REPORT_FILE_EXTENSION = ".png";

	public FileResourceUploadResponse uploadFileResource(FileResourceUploadRequest fileResourceUploadRequest) {
		log.info("[FILE RESOURCE UPLOAD] REQ : {}", fileResourceUploadRequest.toString());

		String filename = UUID.randomUUID().toString();
		String fileResourceDir = "";
		if (fileResourceUploadRequest.getType() == FileResourceType.CONTENT) {
			fileResourceDir = CONTENT_DIRECTORY;
		}
		if (fileResourceUploadRequest.getType() == FileResourceType.PROJECT) {
			fileResourceDir = PROJECT_DIRECTORY;
		}
		if (fileResourceUploadRequest.getType() == FileResourceType.TARGET_IMAGE) {
			filename = String.format(
				"%s_%s", LocalDate.now().toString(), RandomStringUtils.randomAlphanumeric(10).toLowerCase());
			fileResourceDir = REPORT_DIRECTORY;
		}
		if (!StringUtils.hasText(fileResourceDir)) {
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		}
		String uploadedPath = fileUploadService.uploadByFileInputStream(
			fileResourceUploadRequest.getFile(), fileResourceDir, filename);
		return new FileResourceUploadResponse(uploadedPath, LocalDateTime.now());
	}

	@Transactional(readOnly = true)
	public ApiResponse<ContentResourceUsageInfoResponse> getContentResourceUsageInfo(
		String workspaceId, LocalDateTime startDate, LocalDateTime endDate
	) {
		long contentStorageUsage = contentRepository.calculateTotalStorageAmountByWorkspaceId(workspaceId);
		long contentDownloadHit = contentDownloadLogRepository.calculateResourceUsageAmountByWorkspaceIdAndStartDateAndEndDate(
			workspaceId, startDate, endDate);

		long projectStorageUsage = projectRepository.calculateTotalStorageAmountByWorkspaceId(workspaceId);
		long projectDownloadHit = projectDownloadLogRepository.calculateResourceUsageAmountByWorkspaceIdAndStartDateAndEndDate(
			workspaceId, startDate, endDate);

		long storageUsage = contentStorageUsage + projectStorageUsage;
		long downloadHit = contentDownloadHit + projectDownloadHit;

		ContentResourceUsageInfoResponse contentResourceUsageInfoResponse = new ContentResourceUsageInfoResponse();
		contentResourceUsageInfoResponse.setWorkspaceId(workspaceId);
		contentResourceUsageInfoResponse.setStorageUsage(storageUsage);
		contentResourceUsageInfoResponse.setTotalHit(downloadHit);
		contentResourceUsageInfoResponse.setContentStorageUsage(contentStorageUsage);
		contentResourceUsageInfoResponse.setContentTotalHit(contentDownloadHit);
		contentResourceUsageInfoResponse.setProjectStorageUsage(projectStorageUsage);
		contentResourceUsageInfoResponse.setProjectTotalHit(projectDownloadHit);
		contentResourceUsageInfoResponse.setUsageReportDate(LocalDateTime.now());

		return new ApiResponse<>(contentResourceUsageInfoResponse);
	}
}
