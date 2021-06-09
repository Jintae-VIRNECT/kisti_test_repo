package com.virnect.serviceserver.servicedashboard.dto.mapper.memberhistroy;

import org.mapstruct.Mapper;

import com.virnect.data.domain.member.MemberHistory;
import com.virnect.data.infra.utils.GenericMapper;
import com.virnect.serviceserver.servicedashboard.dto.response.MemberInfoResponse;

@Mapper(componentModel = "spring")
public interface DashboardMemberHistoryMapper extends GenericMapper<MemberInfoResponse, MemberHistory> {
}
