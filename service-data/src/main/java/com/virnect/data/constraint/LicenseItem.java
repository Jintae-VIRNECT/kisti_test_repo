package com.virnect.data.constraint;

import java.util.List;

import static com.virnect.data.constraint.LicenseConstants.*;

public enum LicenseItem {
    ITEM_PRODUCT(64, LICENSE_PRODUCT),
    @Deprecated
    ITEM_BASIC(6, LICENSE_BASIC),
    @Deprecated
    ITEM_BUSINESS(6, LICENSE_BUSINESS),
    @Deprecated
    ITEM_PERMANENT(9, LICENSE_PERMANENT);

    private int userCapacity;
    private String itemName;

    LicenseItem(final int userCapacity, final String itemName) {
        this.userCapacity = userCapacity;
        this.itemName = itemName;
    }

    public int getUserCapacity() {
        return userCapacity;
    }

    public String getItemName() {
        return itemName;
    }
}
