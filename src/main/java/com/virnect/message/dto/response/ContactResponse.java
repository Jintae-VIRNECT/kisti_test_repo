package com.virnect.message.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Project: PF-Message
 * DATE: 2020-02-17
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ContactResponse {

    @ApiModelProperty(value = "Contact 메일 발송 결과")
    private final boolean result;
}
