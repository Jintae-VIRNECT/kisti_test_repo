package com.virnect.gateway.error;

public class GatewaySecurityException extends RuntimeException {
    private final ErrorCode errorCode;

    public GatewaySecurityException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
