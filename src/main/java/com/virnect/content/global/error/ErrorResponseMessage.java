package com.virnect.content.global.error;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: SMIC_CUSTOM
 * DATE: 2020-01-09
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Error Response Message Object
 */

@Getter
@NoArgsConstructor
public class ErrorResponseMessage {
    private int code;
    private String message;
    private Map<String, Object> data;

    public ErrorResponseMessage(final ErrorCode error) {
        this.code = error.getCode();
        this.message = error.getMessage();
        data = new HashMap<>();
    }
}
