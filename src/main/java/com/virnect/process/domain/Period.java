package com.virnect.process.domain;

import lombok.ToString;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-28
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Details Process Period Enumerated Class
 */
@ToString
public enum Period {
    BEFORE("일정전"),
    BETWEEN("일정중"),
    AFTER("일정후");

    private String message;

    Period(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
