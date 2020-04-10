package com.virnect.content.domain;

/**
 * Project: PF-SMIC_CUSTOM
 * DATE: 2020-02-04
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public enum ContentType {
    AUGMENTED_REALITY("증강현실"),
    ASSISTED_REALITY("보조현실"),
    CROCESS_PLATFORM("크로스플랫폼"),
    MIXED_REALITY("혼합현실");

    private String message;

    ContentType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
