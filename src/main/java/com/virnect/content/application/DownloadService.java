package com.virnect.content.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.dao.content.ContentRepository;
import com.virnect.content.domain.Content;
import com.virnect.content.event.ContentDownloadHitEvent;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.error.ErrorCode;
import com.virnect.content.infra.file.download.FileDownloadService;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-08-25
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadService {
    private final ContentRepository contentRepository;
    private final FileDownloadService fileDownloadService;
    private final ContentService contentService;
    private final ApplicationEventPublisher eventPublisher;

    public ResponseEntity<byte[]> contentDownloadForUUIDHandler(final String contentUUID, final String memberUUID) {
        // 1. 컨텐츠 데이터 조회
        Content content = this.contentRepository.findByUuid(contentUUID)
            .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

        // 워크스페이스 총 다운로드 수와 라이선스의 다운로드 가능 수 체크
        contentService.checkLicenseDownload(content.getWorkspaceUUID());

        ResponseEntity<byte[]> responseEntity = this.fileDownloadService.fileDownload(content.getPath());
        eventPublisher.publishEvent(new ContentDownloadHitEvent(content, memberUUID));
        return responseEntity;
    }

    public ResponseEntity<byte[]> contentDownloadForTargetHandler(final String targetData, final String memberUUID) {
        String checkedData = contentService.checkParameterEncoded(targetData);

        Content content = this.contentRepository.getContentOfTarget(checkedData);
        // 컨텐츠 데이터 조회

        if (content == null)
            throw new ContentServiceException(ErrorCode.ERR_MISMATCH_TARGET);

        // 워크스페이스 총 다운로드 수와 라이선스의 다운로드 가능 수 체크
        contentService.checkLicenseDownload(content.getWorkspaceUUID());

        ResponseEntity<byte[]> responseEntity = this.fileDownloadService.fileDownload(content.getPath());
        eventPublisher.publishEvent(new ContentDownloadHitEvent(content, memberUUID));
        return responseEntity;
    }
}