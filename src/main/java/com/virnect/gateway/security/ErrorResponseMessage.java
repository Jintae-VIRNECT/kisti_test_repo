package com.virnect.gateway.security;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

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
