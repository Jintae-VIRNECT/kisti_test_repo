package com.virnect.license.dto.response;

import com.virnect.license.domain.PlanStatus;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-License
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.20
 */

@Getter
@Setter
@ApiModel
public class MyLicensePlanInfoResponse {
    private long licensePlanId;
    private long maxStorageSize;
    private long maxDownloadHit;
    private long maxCallTime;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private PlanStatus status;
    private String workspaceId;
    private LicensePlanProductInfoResponse licenseProductInfo;
}
