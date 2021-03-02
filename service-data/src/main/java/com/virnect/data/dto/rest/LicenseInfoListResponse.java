package com.virnect.data.dto.rest;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LicenseInfoListResponse {
    private List<LicenseInfoResponse> licenseInfoList;
}
