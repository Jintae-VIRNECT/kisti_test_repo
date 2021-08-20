package com.virnect.serviceserver.serviceremote.dto.mapper.group;

import org.mapstruct.Mapper;

import com.virnect.data.domain.group.RemoteGroupMember;
import com.virnect.data.dto.response.group.RemoteGroupMemberResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface RemoteGroupMemberMapper extends GenericMapper<RemoteGroupMemberResponse, RemoteGroupMember> {
}
