package com.virnect.download.global.common;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.virnect.download.domain.App;
import com.virnect.download.dto.response.AdminAppUploadResponse;
import com.virnect.download.dto.response.AppUploadResponse;

/**
 * Project: PF-Download
 * DATE: 2021-11-29
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Mapper(componentModel = "spring")
public interface ResponseMapper {
	@Mapping(target = "version", source = "versionName")
	@Mapping(target = "deviceType", expression = "java(app.getDevice().getType())")
	@Mapping(target = "operationSystem", expression = "java(app.getOs().getName())")
	@Mapping(target = "productName", expression = "java(app.getDevice().getProduct().getName())")
	@Mapping(target = "deviceModel", expression = "java(app.getDevice().getModel())")
	AdminAppUploadResponse appToAdminAppUploadResponse(App app);
}
