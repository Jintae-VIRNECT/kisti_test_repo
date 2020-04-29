package com.virnect.workspace.application;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * Project: PF-Workspace
 * DATE: 2020-04-23
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@FeignClient(name = "license-server", fallbackFactory = LicenseRestFallbackFactory.class)
public interface LicenseRestService {
}
