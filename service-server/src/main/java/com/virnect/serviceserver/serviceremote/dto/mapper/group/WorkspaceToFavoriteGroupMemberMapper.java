package com.virnect.serviceserver.serviceremote.dto.mapper.group;

import org.mapstruct.Mapper;

import com.virnect.data.dto.response.group.FavoriteGroupMemberResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface WorkspaceToFavoriteGroupMemberMapper extends GenericMapper<FavoriteGroupMemberResponse, WorkspaceMemberInfoResponse> {
}
