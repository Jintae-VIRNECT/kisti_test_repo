package com.virnect.serviceserver.serviceremote.dto.mapper.member;

import org.mapstruct.Mapper;

import com.virnect.data.domain.member.RemoteGroupMember;
import com.virnect.data.dto.response.member.RemoteGroupMemberInfoResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface RemoteGroupMemberMapper extends GenericMapper<RemoteGroupMemberInfoResponse, RemoteGroupMember> {
}
