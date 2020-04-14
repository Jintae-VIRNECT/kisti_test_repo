package com.virnect.process.domain;

import lombok.ToString;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-28
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Details Process RateRange Enumerated Class
 */
@ToString
public enum RateRange {
    MIN("0"),
    MIDDLE("1~99"),
    MAX("100");

    private String message;

    RateRange(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
