package com.virnect.serviceserver.serviceremote.dto.mapper.group;

import com.virnect.data.domain.group.RemoteGroupMember;
import com.virnect.data.dto.response.group.RemoteGroupMemberResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.infra.utils.GenericMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkspaceToRemoteGroupMapper extends GenericMapper<RemoteGroupMemberResponse, WorkspaceMemberInfoResponse> {
}
