package com.virnect.serviceserver.serviceremote.dto.mapper.member;

import org.mapstruct.Mapper;

import com.virnect.data.domain.member.RemoteGroup;
import com.virnect.data.dto.response.member.RemoteGroupInfoResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface RemoteGroupMapper extends GenericMapper<RemoteGroupInfoResponse, RemoteGroup> {
}
