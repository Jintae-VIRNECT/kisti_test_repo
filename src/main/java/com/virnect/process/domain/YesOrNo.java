package com.virnect.process.domain;

/**
 * Project: PF-SMIC_CUSTOM
 * DATE: 2020-02-04
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public enum YesOrNo {
    YES("YES"),
    NO("NO");

    private String message;

    YesOrNo(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "YesOrNo{" +
                "message='" + message + '\'' +
                '}';
    }
}
