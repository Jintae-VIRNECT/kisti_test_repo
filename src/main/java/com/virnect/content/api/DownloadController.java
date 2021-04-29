package com.virnect.content.api;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.application.DownloadService;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.error.ErrorCode;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-08-25
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Controller
@RequestMapping("/contents")
@RequiredArgsConstructor
public class DownloadController {
    private final DownloadService downloadService;

    @ApiOperation(value = "컨텐츠 식별자로 컨텐츠 다운로드", notes = "컨텐츠 식별자를 통해 컨텐츠를 다운로드.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "contentUUID", value = "컨텐츠 식별자", dataType = "string", paramType = "path", required = true),
        @ApiImplicitParam(name = "memberUUID", value = "다운받는 사용자 고유번호", dataType = "string", paramType = "query", required = true)
    })
    @GetMapping("/download/contentUUID/{contentUUID}")
    public ResponseEntity<byte[]> contentDownloadForUUIDRequestHandler(
        @PathVariable("contentUUID") String contentUUID,
        @RequestParam(value = "memberUUID") String memberUUID
    ) throws IOException {
        log.info("[DOWNLOAD] USER: [{}] => contentUUID: [{}]", memberUUID, contentUUID);
        if (contentUUID.isEmpty() || memberUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        return this.downloadService.contentDownloadForUUIDHandler(contentUUID, memberUUID);
        //        return ResponseEntity.ok()
        //                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        //                .contentLength(resource.getFile().getAbsoluteFile().length())
        //                .contentType(MediaType.APPLICATION_OCTET_STREAM)
        //                .body(resource);
    }

    @ApiOperation(value = "타겟 데이터로 컨텐츠 다운로드", notes = "컨텐츠 식별자 또는 타겟 데이터를 통해 컨텐츠를 다운로드. 컨텐츠 식별자, 타겟 데이터 둘 중 하나는 필수.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "targetData", value = "타겟 데이터", dataType = "string", paramType = "query", required = true, defaultValue = "0jXPVGTgaHBUXHFoTJwi0bLcK7XxmdrCXp0%2ft9pkT%2bQ%3d"),
        @ApiImplicitParam(name = "memberUUID", value = "다운받는 사용자 고유번호", dataType = "string", paramType = "query", required = true)
    })
    @GetMapping("/download")
    public ResponseEntity<byte[]> contentDownloadRequestForTargetHandler(
        @RequestParam(value = "targetData") String targetData,
        @RequestParam(value = "memberUUID") String memberUUID
    ) throws IOException {
        log.info("[DOWNLOAD] USER: [{}] => targetData: [{}]", memberUUID, targetData);
        if (targetData.isEmpty() || memberUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        return this.downloadService.contentDownloadForTargetHandler(targetData, memberUUID);
        //        return ResponseEntity.ok()
        //                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        //                .contentLength(resource.getFile().length())
        //                .contentType(MediaType.APPLICATION_OCTET_STREAM)
        //                .body(resource);
    }
}