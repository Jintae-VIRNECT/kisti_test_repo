package com.virnect.download.domain;

/**
 * Project: PF-Download
 * DATE: 2020-11-27
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public enum DeviceType {
    PC("PC"),
    MOBILE("Google Play"),
    REALEWEAR("Realwear"),
    HOLORENSE("Holorense");

    private String decription;

    DeviceType(String decription) {
        this.decription = decription;
    }

    public String getDecription() {
        return decription;
    }
}
