package com.virnect.download.dto.request;

import com.virnect.download.domain.AppGuideUrl;
import com.virnect.download.domain.AppImageUrl;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Project: PF-Download
 * DATE: 2020-11-27
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class TempAppUploadRequest {
    @ApiModelProperty(value = "앱 구동 시스템 정보(ANDROID, WINDOWS)", example = "ANDROID")
    @NotBlank(message = "앱 구동 시스템 정보는 반드시 입력되어야 합니다.")
    private String operationSystem;
    @ApiModelProperty(value = "앱 제품 정보(REMOTE,MAKE,VIEW)", position = 1, example = "REMOTE")
    private String productName;
    @ApiModelProperty(value = "앱 구동 디바이스 타입(ex: MOBILE, PC, REALWEAR)", position = 2, example = "MOBILE")
    @NotBlank(message = "앱 구동 디바이스 타입 정보는 반드시 입력되어야 합니다.")
    private String deviceType;
    @ApiModelProperty(value = "앱 버전정보(ex: v1.2.25.3)", position = 3, example = "v1.2.25.3")
    @NotBlank(message = "앱 버전 정보는 반드시 입력되어야 합니다.")
    private String versionName;
    @ApiModelProperty(value = "앱 버전정보(ex: 1.2.25.3)", position = 3, example = "1.2.25.3")
    @NotNull(message = "앱 버전 정보는 반드시 입력되어야 합니다.")
    private AppGuideUrl guideUrl;
    @ApiModelProperty(value = "앱 버전정보(ex: 1.2.25.3)", position = 3, example = "1.2.25.3")
    @NotNull(message = "앱 버전 정보는 반드시 입력되어야 합니다.")
    private AppImageUrl imageUrl;
    @ApiModelProperty(value = "업로드 앱 파일", dataType = "__file", position = 3, hidden = true)
    @NotNull(message = "앱 업로드 파일은 반드시 있어야합니다.")
    private MultipartFile uploadAppFile;


}
