package com.virnect.message.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    @NotBlank(message = "보내는 사람의 이메일 주소는 필수 값입니다.")
    @ApiModelProperty(value = "보낸 사람 이메일")
    private String senderEmail;

    @NotBlank(message = "보내는 사람의 이름은 필수 값입니다.")
    @ApiModelProperty(value = "보낸 사람 이름")
    private String senderName;

    @NotBlank(message = "보내는 사람의 기업/기관명은 필수 값입니다.")
    @ApiModelProperty(value = "보낸 사람 이름")
    private String senderCompanyName;

    @NotBlank(message = "보내는 사람의 부서는 필수 값입니다.")
    @ApiModelProperty(value = "보낸 사람 이름")
    private String senderCompanyPart;

    @NotBlank(message = "보내는 사람의 직책은 필수 값입니다.")
    @ApiModelProperty(value = "보낸 사람 이름")
    private String senderCompanyRole;

    @NotBlank(message = "보내는 사람의 전화번호는 필수 값입니다.")
    @ApiModelProperty(value = "보낸 사람 이름")
    private String senderPhone;
}
