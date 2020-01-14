package com.virnect.workspace.global.common;

import lombok.*;

import java.util.Map;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-09
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ResponseMessage {
    Map<String, Object> data;
    int code;
    String message;

    @Builder
    public ResponseMessage(Map<String, Object> data, Integer code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }
}

