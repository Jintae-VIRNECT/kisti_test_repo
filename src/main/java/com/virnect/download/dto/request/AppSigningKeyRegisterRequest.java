package com.virnect.download.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ApiModel
public class AppSigningKeyRegisterRequest {
    @NotBlank(message = "패키지명 정보는 반드시 입력되어야합니다.")
    @ApiModelProperty(value = "패키지 명", required = true, example = "com.virnect.mobile")
    private String packageName;
    @NotBlank
    @Size(min = 32, message = "앱 서명키는 최소 32자 이상이여야 합니다.")
    @ApiModelProperty(value = "패키지 명", position = 1, required = true, example = "signingKeyvaluessigningKeyvalues")
    private String signingKey;

    @Override
    public String toString() {
        return "AppInfoUpdateRequest{" +
                "packageName='" + packageName + '\'' +
                ", signingKey='" + signingKey + '\'' +
                '}';
    }
}
