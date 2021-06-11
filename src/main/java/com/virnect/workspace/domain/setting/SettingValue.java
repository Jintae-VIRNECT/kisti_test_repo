package com.virnect.workspace.domain.setting;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public enum SettingValue {
    USE("ON"),
    UNUSED("OFF"),
    MASTER("MASTER"),
    MANAGER("MANAGER"),
    MEMBER("MEMBER"),
    MASTER_OR_MANEGER("MASTER|MANAGER"),
    MASTER_OR_MANGER_OR_MEMBER("MASTER|MANAGER|MEMBER"),
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    RANDOM("N"),
    FORCE_ON("FORCE_ON"),
    FORCE_OFF("FORCE_OFF"),
    SCREEN_RESOLUTION_480("480p"),
    SCREEN_RESOLUTION_720("720p"),
    SCREEN_RESOLUTION_1080("1080p"),
    FPS_25("25FPS"),
    FPS_30("30FPS"),
    FPS_60("60FPS"),
    FPS_80("80FPS"),
    FPS_120("120FPS"),
    FPS_144("144FPS"),
    BITRATE_2500("2,500Kbps(3Mbps)"),
    BITRATE_3500("3,500Kbps(4Mbps)"),
    BITRATE_5000("5,000Kbps(6Mbps)"),
    BITRATE_6000("6,000Kbps(8Mbps)"),
    BITRATE_12000("12,000Kbps(15Mbps)"),
    READER("READER"),
    FORBID("FORBID"),
    SIZE_10MB("10MB"),
    SIZE_50MB("50MB"),
    SIZE_100MB("100MB"),
    THICKNESS_1("1px"),
    THICKNESS_2("2px"),
    THICKNESS_3("3px"),
    THICKNESS_6("6px"),
    THICKNESS_10("10px"),
    FONT_10("10"),
    FONT_12("12"),
    FONT_14("14"),
    FONT_16("16"),
    FONT_18("18");
    private String value;

    SettingValue(String value) {
        this.value = value;
    }
}

