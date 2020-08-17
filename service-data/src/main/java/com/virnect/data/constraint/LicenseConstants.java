package com.virnect.data.constraint;

/**
 *
 */
public class LicenseConstants {
    public static final String PRODUCT_NAME = "REMOTE";

    enum LicenseItem {
        LICENSE_ITEM_BASIC(3, "BASIC"),
        LICENSE_ITEM_BUSINESS(6, "BUSINESS"),
        LICENSE_ITEM_PERMANENT(9, "PERMANENT");

        private int count;
        private String itemName;

        LicenseItem(final int count, final String itemName) {
            this.count = count;
            this.itemName = itemName;
        }

        public int getCount() {
            return count;
        }

        public String getItemName() {
            return itemName;
        }
    }
    public static final String LICENSE_TYPE = "BASIC";

    public static final String STATUS_USE = "USE";
    public static final String STATUS_UN_USE = "UNUSE";
}
