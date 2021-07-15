package com.virnect.serviceserver.serviceremote.dto.mapper.file;

import org.mapstruct.Mapper;

import com.virnect.data.domain.file.File;
import com.virnect.data.dto.response.file.ShareFileUploadResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface ShareUploadFileMapper extends GenericMapper<ShareFileUploadResponse, File> {
}
