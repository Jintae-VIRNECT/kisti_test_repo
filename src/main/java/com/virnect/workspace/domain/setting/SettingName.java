package com.virnect.workspace.domain.setting;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-01
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public enum SettingName {
    WORKSPACE_INVITE_SETTING(Product.WORKSTATION, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.MANAGER, SettingValue.MANAGER, SettingValue.MEMBER}),
    REMOTE_CONFIG_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.YES, SettingValue.NO});
    private Product product;
    private SettingValue defaultSettingValue;
    private SettingValue[] settingValues;

    SettingName(Product product, SettingValue defaultSettingValue, SettingValue[] settingValues) {
        this.product = product;
        this.defaultSettingValue = defaultSettingValue;
        this.settingValues = settingValues;
    }

    public Product getProduct() {
        return product;
    }

    public SettingValue getDefaultSettingValue() {
        return defaultSettingValue;
    }

    public SettingValue[] getSettingValues() {
        return settingValues;
    }
}
