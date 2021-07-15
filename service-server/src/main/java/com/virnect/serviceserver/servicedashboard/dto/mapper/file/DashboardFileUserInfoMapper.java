package com.virnect.serviceserver.servicedashboard.dto.mapper.file;

import org.mapstruct.Mapper;

import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.infra.utils.GenericMapper;
import com.virnect.serviceserver.servicedashboard.dto.response.FileUserInfoResponse;

@Mapper(componentModel = "spring")
public interface DashboardFileUserInfoMapper extends GenericMapper<FileUserInfoResponse, UserInfoResponse> {
}
