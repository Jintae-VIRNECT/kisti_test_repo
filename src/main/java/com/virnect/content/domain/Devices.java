package com.virnect.content.domain;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.10
 */
public enum Devices {
    SMARTPHONE_ANDROID("안드로이드 스마트폰"),
    TABLET_ANDROID("안드로이드 태블릿"),
    REALWARE_HMT("리얼웨어");

    private String message;

    Devices(String message) { this.message = message; }

    public String getMessage() { return message; }
}
