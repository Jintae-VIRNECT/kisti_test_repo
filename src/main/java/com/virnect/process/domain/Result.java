package com.virnect.process.domain;

/**
 * Project: PF-SMIC_CUSTOM
 * DATE: 2020-01-28
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Details Process Result Enumerated Class
 */
public enum Result {
    /*
    스마트툴과 작업에서 두가지 의미로 사용됨.
    스마트툴에서 체결 후 보내주는 결과값 : 정상, 비정상
    작업 수행여부 : 수행, 미수행
     */
    OK("정상, 수행"),
    NOK("비정상, 미수행");

    private String message;

    Result(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SmartToolResult{" +
                "message='" + message + '\'' +
                '}';
    }
}
