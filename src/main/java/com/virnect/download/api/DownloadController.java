package com.virnect.download.api;

import com.virnect.download.application.DownloadService;
import com.virnect.download.dto.response.AppResponse;
import com.virnect.download.dto.response.AppUploadResponse;
import com.virnect.download.global.common.ApiResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Project: base
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Sample Basic Controller
 */
@Slf4j
@RestController
@RequestMapping("/download")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DownloadController {
    private final DownloadService downloadService;
/*

    @ApiOperation(
            value = "어플리케이션 업로드",
            hidden = true
    )
    @GetMapping("/upload/{productName}")
    public ResponseEntity<ApiResponse<AppUploadResponse>> uploadFile(@RequestPart("file") MultipartFile file, @PathVariable("productName") String productName) throws IOException {
        ApiResponse<AppUploadResponse> apiResponse = this.downloadService.uploadFile(file);
        return ResponseEntity.ok(apiResponse);
    }
*/

    @ApiOperation(
            value = "어플리케이션 다운로드",
            notes = "가장 최근에 업로드 된 파일이 다운로드 됩니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productName", value = "제품명", dataType = "string", defaultValue = "make", required = true)
    })
    @GetMapping("/{productName}")
    public ResponseEntity<ApiResponse<String>> downloadFile(@PathVariable("productName") String productName) {
        ApiResponse<String> apiResponse = this.downloadService.downloadFile(productName);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "어플리케이션 조회",
            notes = "가장 최근에 업로드 된 파일을 조회합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productName", value = "제품명", dataType = "string", defaultValue = "make", required = true)
    })
    @GetMapping("/list/{productName}")
    public ResponseEntity<ApiResponse<AppResponse>> findFile(@PathVariable("productName") String productName) {
        ApiResponse<AppResponse> apiResponse = this.downloadService.findFile(productName);
        return ResponseEntity.ok(apiResponse);
    }
}
