package com.virnect.serviceserver.serviceremote.dto.mapper.group;

import org.mapstruct.Mapper;

import com.virnect.data.domain.group.RemoteGroup;
import com.virnect.data.dto.response.group.RemoteGroupResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface RemoteGroupMapper extends GenericMapper<RemoteGroupResponse, RemoteGroup> {
}

