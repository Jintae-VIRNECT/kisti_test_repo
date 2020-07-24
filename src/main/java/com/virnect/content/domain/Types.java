package com.virnect.content.domain;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.10
 */
public enum Types {
    AUGMENTED_REALITY("증강현실"),
    ASSISTED_REALITY("보조현실"),
    CROCESS_PLATFORM("크로스플랫폼"),
    MIXED_REALITY("혼합현실");

    private String message;

    Types(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
