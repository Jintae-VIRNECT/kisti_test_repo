package com.virnect.download.api;

import com.virnect.download.application.DownloadService;
import com.virnect.download.dto.response.AppInfoListResponse;
import com.virnect.download.exception.DownloadException;
import com.virnect.download.global.common.ApiResponse;
import com.virnect.download.global.error.ErrorCode;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;

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

    @ApiOperation(
            value = "어플리케이션 다운로드",
            notes = "어플리케이션 파일을 다운로드 또는 리다이렉트 합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "어플리케이션 식별자", dataType = "string", defaultValue = "05d1-8795535abb40", required = true)
    })
    @GetMapping("/app/{uuid}")
    public ResponseEntity<ApiResponse<Boolean>> downloadApp(@PathVariable("uuid") String uuid) throws IOException, URISyntaxException {
        if (!StringUtils.hasText(uuid)) {
            throw new DownloadException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<Boolean> apiResponse = this.downloadService.downloadApp(uuid);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "가이드 다운로드",
            notes = "가이드 파일을 다운로드 합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "어플리케이션 식별자", dataType = "string", defaultValue = "05d1-8795535abb40", required = true)
    })
    @GetMapping("/guide/{uuid}")
    public ResponseEntity<ApiResponse<Boolean>> downloadGuide(@PathVariable("uuid") String uuid) throws IOException {
        if (!StringUtils.hasText(uuid)) {
            throw new DownloadException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<Boolean> apiResponse = this.downloadService.downloadGuide(uuid);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(
            value = "어플리케이션 조회",
            notes = "제품 별 가장 최신 버전의 다운로드 항목을 조회합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productName", value = "제품명(make,remote,view)", dataType = "string", defaultValue = "make", required = true)
    })
    @GetMapping("/list/{productName}")
    public ResponseEntity<ApiResponse<AppInfoListResponse>> getAppList(@PathVariable("productName") String productName, @ApiIgnore Locale locale) {
        if (!StringUtils.hasText(productName)) {
            throw new DownloadException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<AppInfoListResponse> apiResponse = this.downloadService.getAppList(productName, locale);
        return ResponseEntity.ok(apiResponse);
    }
}
