package com.virnect.download.api;

import com.virnect.download.application.UploadService;
import com.virnect.download.dto.request.TempAppUploadRequest;
import com.virnect.download.dto.response.AppInfoResponse;
import com.virnect.download.global.common.ApiResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

/**
 * Project: PF-Download
 * DATE: 2020-11-27
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@RestController
@RequestMapping("/download")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UploadController {
    private final UploadService uploadService;

    @ApiOperation(
            value = "(임시)어플리케이션 등록",
            notes = "제품별 어플리케이션을 등록합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productName", value = "제품명(make,remote,view)", dataType = "string", defaultValue = "make", required = true)
    })
    @PostMapping("/app/upload")
    public ResponseEntity<ApiResponse<AppInfoResponse>> uploadApplication(@ModelAttribute @Valid TempAppUploadRequest tempAppUploadRequest) throws IOException {
        AppInfoResponse response = uploadService.uploadApplication(tempAppUploadRequest);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

}
