package com.virnect.serviceserver.serviceremote.dto.mapper;

import org.mapstruct.Mapper;

import com.virnect.data.dto.response.file.FileUserInfoResponse;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface FileUserInfoMapper extends GenericMapper<FileUserInfoResponse, UserInfoResponse> {
}
