package com.virnect.data.dto.mapper;

import org.mapstruct.Mapper;

import com.virnect.data.dto.response.member.MemberInfoResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface MemberWorkspaceMapper extends GenericMapper<MemberInfoResponse, WorkspaceMemberInfoResponse> {
}
