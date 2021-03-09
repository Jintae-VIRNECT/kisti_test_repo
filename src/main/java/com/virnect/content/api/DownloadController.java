package com.virnect.content.api;

import com.virnect.content.application.DownloadService;
import com.virnect.content.dto.request.DownloadLogAddRequest;
import com.virnect.content.dto.response.DownloadLogAddResponse;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.error.ErrorCode;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

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
            @ApiImplicitParam(name = "memberUUID", value = "다운받는 사용자 고유번호", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "workspaceUUID", value = "다운받는 워크스페이스 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8")
    })
    @GetMapping("/download/contentUUID/{contentUUID}")
    public ResponseEntity<byte[]> contentDownloadForUUIDRequestHandler(
            @PathVariable("contentUUID") String contentUUID,
            @RequestParam(value = "memberUUID") String memberUUID,
            @RequestParam(value = "workspaceUUID") String workspaceUUID
    ) throws IOException {
        log.info("[DOWNLOAD] USER: [{}], WORKSPACE [{}] => contentUUID: [{}]", memberUUID, workspaceUUID, contentUUID);
        if (!StringUtils.hasText(contentUUID) || !StringUtils.hasText(memberUUID) || !StringUtils.hasText(workspaceUUID)) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        return this.downloadService.contentDownloadForUUIDHandler(contentUUID, memberUUID, workspaceUUID);
        //        return ResponseEntity.ok()
        //                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        //                .contentLength(resource.getFile().getAbsoluteFile().length())
        //                .contentType(MediaType.APPLICATION_OCTET_STREAM)
        //                .body(resource);
    }

    @ApiOperation(value = "타겟 데이터로 컨텐츠 다운로드", notes = "컨텐츠 식별자 또는 타겟 데이터를 통해 컨텐츠를 다운로드. 컨텐츠 식별자, 타겟 데이터 둘 중 하나는 필수.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetData", value = "타겟 데이터", dataType = "string", paramType = "query", required = true, defaultValue = "mgbvuA6RhUXL JPrK2Z7YoKi7HEp4K0XmmkLbV7SlBQX NzZxnldwTk/22rpebA4"),
            @ApiImplicitParam(name = "memberUUID", value = "다운받는 사용자 고유번호", dataType = "string", paramType = "query", required = true, defaultValue = "498b1839dc29ed7bb2ee90ad6985c608"),
            @ApiImplicitParam(name = "workspaceUUID", value = "다운받는 워크스페이스 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8")
    })
    @GetMapping("/download")
    public ResponseEntity<byte[]> contentDownloadRequestForTargetHandler(
            @RequestParam(value = "targetData") String targetData,
            @RequestParam(value = "memberUUID") String memberUUID,
            @RequestParam(value = "workspaceUUID") String workspaceUUID
    ) throws IOException {
        log.info("[DOWNLOAD] USER: [{}], WORKSPACE [{}] => targetData: [{}]", memberUUID, workspaceUUID, targetData);
        if (!StringUtils.hasText(targetData) || !StringUtils.hasText(memberUUID) || !StringUtils.hasText(workspaceUUID)) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        return this.downloadService.contentDownloadForTargetHandler(targetData, memberUUID, workspaceUUID);
        //        return ResponseEntity.ok()
        //                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        //                .contentLength(resource.getFile().length())
        //                .contentType(MediaType.APPLICATION_OCTET_STREAM)
        //                .body(resource);
    }

    @ApiOperation(value = "콘텐츠 다운로드 이벤트", tags = "process server only")
    @PostMapping("/download/log")
    public ResponseEntity<ApiResponse<DownloadLogAddResponse>> contentDownloadLogForUUIDHandler(@RequestBody @Valid DownloadLogAddRequest downloadLogAddRequest, BindingResult bindingResult ){
        if (bindingResult.hasErrors()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        DownloadLogAddResponse downloadLogAddResponse = downloadService.contentDownloadLogForUUIDHandler(downloadLogAddRequest);
        return ResponseEntity.ok(new ApiResponse<>(downloadLogAddResponse));
    }
}
