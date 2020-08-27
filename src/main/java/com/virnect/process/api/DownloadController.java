package com.virnect.process.api;

import com.virnect.process.application.DownloadService;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.error.ErrorCode;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-08-27
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class DownloadController {
    private final DownloadService downloadService;

    @ApiOperation(value = "콘텐츠UUID로 다운로드", tags = "Download")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentUUID", value = "콘텐츠 식별자", dataType = "string", paramType = "path", required = true, defaultValue = "e1bd3914-2b69-475f-9f9d-117477dfae05"),
            @ApiImplicitParam(name = "memberUUID", value = "사용자 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "498b1839dc29ed7bb2ee90ad6985c608")
    })
    @GetMapping("/download/contentUUID/{contentUUID}")
    public ResponseEntity<byte[]> contentDownloadForUUIDRequestHandler(
            @PathVariable("contentUUID") String contentUUID
            , @RequestParam("memberUUID") String memberUUID
    ) throws IOException {
        if (contentUUID.isEmpty() || memberUUID.isEmpty()) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        log.info("[DOWNLOAD] USER: [{}] => contentUUID: [{}]", memberUUID, contentUUID);

        return this.downloadService.contentDownloadForUUIDHandler(contentUUID, memberUUID);
    }

    @ApiOperation(value = "타겟 데이터로 다운로드", tags = "Download")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetData", value = "타겟 데이터", dataType = "string", paramType = "query", required = true, defaultValue = "a73cca48-dc17-4de8-a1aa-c19316cf773b"),
            @ApiImplicitParam(name = "memberUUID", value = "사용자 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "498b1839dc29ed7bb2ee90ad6985c608")
    })
    @GetMapping("/download")
    public ResponseEntity<byte[]> contentDownloadForTargetHandler(
            @RequestParam("targetData") String targetData
            , @RequestParam("memberUUID") String memberUUID
    ) throws IOException {
        if (targetData.isEmpty() || memberUUID.isEmpty()) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        log.info("[DOWNLOAD] USER: [{}] => targetData: [{}]", memberUUID, targetData);

        return this.downloadService.contentDownloadForTargetHandler(targetData, memberUUID);
    }
}
