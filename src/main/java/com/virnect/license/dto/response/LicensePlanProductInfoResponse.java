package com.virnect.license.dto.response;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-License
 * @email practice1356@gmail.com
 * @description
 * @since 2020.05.11
 */

@Getter
@Setter
@ApiModel
public class LicensePlanProductInfoResponse {
    private long licenseProductId;
    private int quantity;
    private String productName;
    private String licenseType;
    private boolean isCouponProduct;
}
