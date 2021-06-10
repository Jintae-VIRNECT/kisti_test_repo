package com.virnect.serviceserver.serviceremote.dto.mapper.file;

import org.mapstruct.Mapper;

import com.virnect.data.domain.file.File;
import com.virnect.data.dto.response.file.ShareFileInfoResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface ShareFileInfoMapper extends GenericMapper<ShareFileInfoResponse, File> {
}
