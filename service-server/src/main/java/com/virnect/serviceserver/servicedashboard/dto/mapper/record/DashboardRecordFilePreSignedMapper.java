package com.virnect.serviceserver.servicedashboard.dto.mapper.record;

import org.mapstruct.Mapper;

import com.virnect.data.domain.file.RecordFile;
import com.virnect.data.infra.utils.GenericMapper;
import com.virnect.serviceserver.servicedashboard.dto.response.FilePreSignedResponse;

@Mapper(componentModel = "spring")
public interface DashboardRecordFilePreSignedMapper extends GenericMapper<FilePreSignedResponse, RecordFile> {
}
