package com.virnect.serviceserver.serviceremote.dto.mapper;

import org.mapstruct.Mapper;

import com.virnect.data.domain.file.File;
import com.virnect.data.dto.response.file.FileInfoResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface FileInfoMapper extends GenericMapper<FileInfoResponse, File> {
}
