package com.virnect.content.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.application.license.LicenseRestService;
import com.virnect.content.application.workspace.WorkspaceRestService;
import com.virnect.content.dao.content.ContentRepository;
import com.virnect.content.domain.Content;
import com.virnect.content.domain.YesOrNo;
import com.virnect.content.dto.request.DownloadLogAddRequest;
import com.virnect.content.dto.response.DownloadLogAddResponse;
import com.virnect.content.dto.rest.LicenseInfoResponse;
import com.virnect.content.dto.rest.WorkspaceUserListResponse;
import com.virnect.content.dto.rest.MyLicenseInfoListResponse;
import com.virnect.content.dto.rest.MyLicenseInfoResponse;
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
    private final LicenseRestService licenseRestService;
    private final WorkspaceRestService workspaceRestService;

    public ResponseEntity<byte[]> contentDownloadForUUIDHandler(String contentUUID, String memberUUID, String workspaceUUID, String range) {
        // 0. 컨텐츠 데이터 조회
        Content content = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

        //1. 워크스페이스 체크
        workspaceValidCheck(memberUUID, workspaceUUID, content.getWorkspaceUUID());
        //2. view 라이선스 체크 및 라이선스 다운로드 가능 수 체크
        licenseMaxDownloadValidCheck(workspaceUUID);
        licenseValidCheck(memberUUID, workspaceUUID);
        //3. 공유상태체크
        contentShardCheck(memberUUID, content);

        ResponseEntity<byte[]> responseEntity = this.fileDownloadService.fileDownload(content.getPath(), range);
        eventPublisher.publishEvent(new ContentDownloadHitEvent(content, memberUUID));
        return responseEntity;
    }

    public ResponseEntity<byte[]> contentDownloadForTargetHandler(String targetData, String memberUUID, String workspaceUUID) {
        //0. 타겟데이터 체크
        String checkedData = contentService.checkParameterEncoded(targetData);
        Content content = contentRepository.getContentOfTarget(checkedData).orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

        //1. 워크스페이스 체크
        workspaceValidCheck(memberUUID, workspaceUUID, content.getWorkspaceUUID());
        //2. view 라이선스 체크 및 라이선스 다운로드 가능 수 체크
        licenseMaxDownloadValidCheck(workspaceUUID);
        licenseValidCheck(memberUUID, workspaceUUID);
        //3. 공유상태체크
        contentShardCheck(memberUUID, content);

        ResponseEntity<byte[]> responseEntity = this.fileDownloadService.fileDownload(content.getPath(), null);
        eventPublisher.publishEvent(new ContentDownloadHitEvent(content, memberUUID));
        return responseEntity;
    }

    private void workspaceValidCheck(String memberUUID, String workspaceUUID, String contentWorkspaceUUID) {
        if (!contentWorkspaceUUID.equals(workspaceUUID)) {
            log.error("[CONTENT DOWNLOAD][WORKSPACE CHECK] content workspace not matched request workspace. content workspace uuid : [{}], request workspace uuid : [{}]", contentWorkspaceUUID, workspaceUUID);
            throw new ContentServiceException(ErrorCode.ERROR_WORKSPACE);
        }
        WorkspaceUserListResponse workspaceUserListResponse = workspaceRestService.getSimpleWorkspaceUserList(workspaceUUID).getData();
        if (CollectionUtils.isEmpty(workspaceUserListResponse.getMemberInfoList())) {
            log.error("[CONTENT DOWNLOAD][WORKSPACE CHECK] workspace member list is empty");
            throw new ContentServiceException(ErrorCode.ERROR_WORKSPACE);
        }
        boolean containUser = workspaceUserListResponse.getMemberInfoList().stream().anyMatch(memberInfoDTO -> memberInfoDTO.getUuid().equals(memberUUID));
        if (!containUser) {
            log.error("[CONTENT DOWNLOAD][WORKSPACE CHECK] content workspace haven't request user. content workspace uuid : [{}], request user uuid : [{}], containUser : [{}]", contentWorkspaceUUID, memberUUID, containUser);
            throw new ContentServiceException(ErrorCode.ERROR_WORKSPACE);
        }
    }

    private void licenseValidCheck(String memberUUID, String workspaceUUID) {
        MyLicenseInfoListResponse myLicenseInfoListResponse = licenseRestService.getMyLicenseInfoRequestHandler(memberUUID, workspaceUUID).getData();
        if (CollectionUtils.isEmpty(myLicenseInfoListResponse.getLicenseInfoList())) {
            log.error("[CONTENT DOWNLOAD][LICENSE CHECK] my license info list is empty. user uuid : [{}], workspace uuid : [{}]", memberUUID, workspaceUUID);
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD_INVALID_LICENSE);
        }
        boolean containViewLicense = myLicenseInfoListResponse.getLicenseInfoList().stream().map(MyLicenseInfoResponse::getProductName).anyMatch(productName -> productName.equals("VIEW"));
        if (!containViewLicense) {
            log.error("[CONTENT DOWNLOAD][LICENSE CHECK] my license info list is not contain view plan. user uuid : [{}], workspace uuid : [{}], contain view license : [{}]", memberUUID, workspaceUUID, containViewLicense);
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD_INVALID_LICENSE);
        }
    }

    private void contentShardCheck(String memberUUID, Content content) {
        if (content.getShared().equals(YesOrNo.NO) && !content.getUserUUID().equals(memberUUID)) {
            log.error("[CONTENT DOWNLOAD][SHARED CHECK] content shared : [{}], user uuid : [{}], content owner user uuid : [{}]", content.getShared(), memberUUID, content.getUserUUID());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD_INVALID_SHARED);
        }
    }

    protected void licenseMaxDownloadValidCheck(String workspaceUUID) {
        // 라이센스 총 다운로드 횟수
        LicenseInfoResponse licenseInfoResponse = licenseRestService.getWorkspaceLicenseInfo(workspaceUUID).getData();
        Long maxDownload = licenseInfoResponse.getMaxDownloadHit();

        // 현재 워크스페이스의 총 다운로드 횟수
        long sumDownload = licenseInfoResponse.getCurrentUsageDownloadHit();

        if (maxDownload < sumDownload + 1) {
            log.error("[CONTENT DOWNLOAD][LICENSE CHECK] content download count is over workspace max download count. max download count : [{}], content download count(include current request) : [{}]", maxDownload, sumDownload + 1);
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD_INVALID_LICENSE);
        }
    }

    public DownloadLogAddResponse contentDownloadLogForUUIDHandler(DownloadLogAddRequest downloadLogAddRequest) {
        Content content = this.contentRepository.findByUuid(downloadLogAddRequest.getContentUUID())
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));
        eventPublisher.publishEvent(new ContentDownloadHitEvent(content, downloadLogAddRequest.getMemberUUID()));
        return new DownloadLogAddResponse(true);
    }
}
