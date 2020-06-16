package com.virnect.message.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Project: PF-Message
 * DATE: 2020-04-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class MailSendRequest {
    @ApiModelProperty(value = "받는 사람 이메일 주소", example = "[\"ljk@virnect.com\"]", required = true, position = 0)
    @NotNull
    private List<String> receivers;

    @ApiModelProperty(value = "보내는 사람 사람 이메일 주소", example = "no-reply@virnect.com", required = true, position = 1)
    @NotBlank
    private String sender;

    @ApiModelProperty(value = "메일 제목", example = "제목", required = true, position = 2)
    @NotBlank
    private String subject;

    @ApiModelProperty(value = "메일 html", example = "", required = true, position = 3)
    @NotBlank
    private String html;
}
