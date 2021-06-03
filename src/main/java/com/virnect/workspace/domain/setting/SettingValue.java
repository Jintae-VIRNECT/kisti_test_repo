package com.virnect.workspace.domain.setting;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public enum SettingValue {
    USE("USE"),
    UNUSED("UNUSED"),
    MASTER("MASTER"),
    MANAGER("MANAGER"),
    MEMBER("MEMBER"),
    YES("YES"),
    NO("NO"),
    FIRST("1~10"),
    SECOND("11~20"),
    ONE("1"),
    TWO("2");


    private String value;
    SettingValue(String value) {
        this.value=value;
    }
}
