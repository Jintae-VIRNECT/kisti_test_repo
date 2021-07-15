package com.virnect.serviceserver.servicedashboard.dto.mapper.file;

import org.mapstruct.Mapper;

import com.virnect.data.domain.file.File;
import com.virnect.data.infra.utils.GenericMapper;
import com.virnect.serviceserver.servicedashboard.dto.response.FileInfoResponse;

@Mapper(componentModel = "spring")
public interface DashboardFileInfoMapper extends GenericMapper<FileInfoResponse, File> {
}
