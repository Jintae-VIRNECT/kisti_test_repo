package com.virnect.process.dto.rest.response.content;

/**
 * Project: PF-SMIC_CUSTOM
 * DATE: 2020-02-03
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Content Status enumerated class
 */
public enum ContentStatus {
    PUBLISH("배포 중"),
    MANAGED("공정 관리 중"),
    WAIT("공정 미등록");

    private String message;

    ContentStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
