package com.virnect.serviceserver.serviceremote.dto.mapper.group;

import org.mapstruct.Mapper;

import com.virnect.data.dto.response.group.RemoteGroupMemberResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface WorkspaceToRemoteGroupMemberMapper extends GenericMapper<RemoteGroupMemberResponse, WorkspaceMemberInfoResponse> {
}
