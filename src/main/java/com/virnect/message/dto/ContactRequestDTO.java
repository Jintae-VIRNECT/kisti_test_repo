package com.virnect.message.dto;

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
public class ContactRequestDTO {
    @NotEmpty
    private String[] category;// 솔루션문의, 채용문의.. etc

    @NotBlank
    private String content;// 내용

    @NotBlank
    private String subject;// 제목

    @NotBlank
    private String senderEmail;// 보낸이

    @NotBlank
    private String senderName;// 보낸 사람
}
