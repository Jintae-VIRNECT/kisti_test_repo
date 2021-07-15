package com.virnect.serviceserver.servicedashboard.dto.mapper.record;

import org.mapstruct.Mapper;

import com.virnect.data.domain.file.RecordFile;
import com.virnect.data.infra.utils.GenericMapper;
import com.virnect.serviceserver.servicedashboard.dto.response.FileDetailInfoResponse;

@Mapper(componentModel = "spring")
public interface DashboardRecordFileDetailMapper extends GenericMapper<FileDetailInfoResponse, RecordFile> {
}
