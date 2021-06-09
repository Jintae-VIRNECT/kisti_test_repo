package com.virnect.serviceserver.servicedashboard.dto.mapper.member;

import org.mapstruct.Mapper;

import com.virnect.data.domain.member.Member;
import com.virnect.data.infra.utils.GenericMapper;
import com.virnect.serviceserver.servicedashboard.dto.response.MemberInfoResponse;

@Mapper(componentModel = "spring")
public interface DashboardMemberInfoMapper extends GenericMapper<MemberInfoResponse, Member> {
}
