package com.virnect.process.domain;

import lombok.ToString;

/**
 * @author hangkee.min (henry)
 * @project PF-ProcessManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.08
 */
@ToString
public enum TargetType {
    QR("QR"),
    VTarget("VTarget"),
    VR("VR");

    private String message;

    TargetType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
