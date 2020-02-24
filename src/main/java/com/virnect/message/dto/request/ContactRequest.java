package com.virnect.message.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * Project: PF-Message
 * DATE: 2020-02-12
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class ContactRequest {
    @NotEmpty(message = "문의 유형은 최소 한 개 이상이어야 합니다.")
    @ApiModelProperty(value = "문의 유형")
    private String[] category;

    @NotBlank(message = "내용은 필수 값입니다.")
    @ApiModelProperty(value = "내용")
    private String content;

    @NotBlank(message = "제목은 필수 값입니다.")
    @ApiModelProperty(value = "제목")
    private String subject;

    @ApiModelProperty(value = "문의 상세 내역")
    private SubCategory subCategory;

    @Valid
    @ApiModelProperty(value = "고객 정보")
    private UserInfo userInfo;

    @Getter
    @Setter
    public static class SubCategory {
        @ApiModelProperty(value = "산업군")
        private String industry;

        @ApiModelProperty(value = "현재 직면한 문제")
        private String problem;

        @ApiModelProperty(value = "업무 개선")
        private String improve;

        @ApiModelProperty(value = "사용 경험")
        private String experience;

        @ApiModelProperty(value = "예산 편성 여부")
        private String budget;

        @ApiModelProperty(value = "도입 희망 시기")
        private String introduction;
    }

    @Getter
    @Setter
    public static class UserInfo {
        @NotBlank(message = "고객명은 필수 값입니다.")
        @ApiModelProperty(value = "문의 고객")
        private String userName;

        @NotBlank(message = "고객의 기업/기관명은 필수 값입니다.")
        @ApiModelProperty(value = "기업/기관")
        private String userCompanyName;

        @NotBlank(message = "고객의 부서명은 필수 값입니다.")
        @ApiModelProperty(value = "부서")
        private String userCompanyPart;

        @NotBlank(message = "고객의 직책명은 필수 값입니다.")
        @ApiModelProperty(value = "직책")
        private String userCompanyRole;

        @NotBlank(message = "고객 이메일은 필수 값입니다.")
        @ApiModelProperty(value = "이메일")
        private String userEmail;

        @NotBlank(message = "고객 전화번호는 필수 값입니다.")
        @ApiModelProperty(value = "전화번호")
        private String userPhone;
    }
}
