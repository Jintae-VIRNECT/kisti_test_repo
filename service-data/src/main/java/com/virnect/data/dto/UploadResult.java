package com.virnect.data.dto;

import com.virnect.data.error.ErrorCode;

public class UploadResult {
    private String result;
    private ErrorCode errorCode;

    public UploadResult(String result, ErrorCode errorCode) {
        this.result = result;
        this.errorCode = errorCode;
    }

    public String getResult() {
        return result;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
