package com.virnect.data.constraint;

import java.util.List;

import static com.virnect.data.constraint.CompanyConstants.COMPANY_KINTEX;
import static com.virnect.data.constraint.CompanyConstants.COMPANY_VIRNECT;
import static com.virnect.data.constraint.LicenseConstants.*;

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
        this.itemName = itemName;
        this.companyCode = companyCode;
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
            default:
                throw new IllegalStateException("Unexpected value: " + companyCode);
        }
        return licenseItem;
    }
}
