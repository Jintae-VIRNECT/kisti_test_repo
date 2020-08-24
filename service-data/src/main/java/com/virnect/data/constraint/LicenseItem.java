package com.virnect.data.constraint;

import static com.virnect.data.constraint.LicenseConstants.*;

public enum LicenseItem {
    ITEM_BASIC(6, LICENSE_BASIC),
    ITEM_BUSINESS(6, LICENSE_BUSINESS),
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
