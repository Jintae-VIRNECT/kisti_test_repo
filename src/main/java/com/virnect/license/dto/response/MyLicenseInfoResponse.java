package com.virnect.license.dto.response;

import com.virnect.license.domain.LicenseStatus;
import com.virnect.license.domain.LicenseType;
import com.virnect.license.domain.ProductName;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

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
public class MyLicenseInfoResponse {
    private long id;
    private String serialKey;
    private LicenseStatus status;
    private ProductName productName;
    private LicenseType licenseType;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
