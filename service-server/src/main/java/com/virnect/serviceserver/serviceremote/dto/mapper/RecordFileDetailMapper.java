package com.virnect.serviceserver.serviceremote.dto.mapper;

import org.mapstruct.Mapper;

import com.virnect.data.domain.file.RecordFile;
import com.virnect.data.dto.response.file.FileDetailInfoResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface RecordFileDetailMapper extends GenericMapper<FileDetailInfoResponse, RecordFile> {
}
