package com.virnect.download.api;

import com.virnect.download.application.DownloadService;
import com.virnect.download.global.common.ApiResponse;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

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

    @ApiOperation(value = "파일 업로드(exe)")
    @GetMapping
    public ResponseEntity<ApiResponse<Boolean>> uploadFile(@RequestPart("file") MultipartFile file) throws IOException {
        ApiResponse<Boolean> apiResponse = this.downloadService.uploadFile(file);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{fileName}")
    public String downloadFile(@PathVariable("fileName") final String fileName) {
        return "[" + LocalDateTime.now() + "] [" + fileName + "]";
    }
}
