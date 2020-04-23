package com.virnect.content.domain;

import lombok.ToString;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.08
 */
@ToString
public enum TargetType {
    QR("QR");

    private String message;

    TargetType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
