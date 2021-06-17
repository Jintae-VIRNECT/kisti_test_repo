package com.virnect.data.dto.constraint;

import static com.virnect.data.dto.constraint.CompanyConstants.*;
import static com.virnect.data.dto.constraint.LicenseConstants.*;

public enum LicenseItem {
    ITEM_PRODUCT(6, COMPANY_VIRNECT, LICENSE_PRODUCT),
    ITEM_KINTEX(22, COMPANY_KINTEX, LICENSE_UNOFFICIAL),

    @Deprecated
    ITEM_BASIC(6, COMPANY_VIRNECT, LICENSE_BASIC),
    @Deprecated
    ITEM_BUSINESS(6, COMPANY_VIRNECT, LICENSE_BUSINESS),
    @Deprecated
    ITEM_PERMANENT(9, COMPANY_VIRNECT, LICENSE_PERMANENT);

    private int userCapacity;
    private int companyCode;
    private String itemName;

    LicenseItem(final int userCapacity, final int companyCode, final String itemName) {
        this.userCapacity = userCapacity;
        this.companyCode = companyCode;
        this.itemName = itemName;
    }

    public int getUserCapacity() {
        return userCapacity;
    }

    public String getItemName() {
        return itemName;
    }

    public int getCompanyCode() {
        return companyCode;
    }

    public static LicenseItem getLicenseItem(int companyCode) {
        LicenseItem licenseItem = null;
        switch (companyCode) {
            case COMPANY_VIRNECT:
                licenseItem = LicenseItem.ITEM_PRODUCT;
                break;
            case COMPANY_KINTEX:
                licenseItem = LicenseItem.ITEM_KINTEX;
                break;
        }
        return licenseItem;
    }
}
