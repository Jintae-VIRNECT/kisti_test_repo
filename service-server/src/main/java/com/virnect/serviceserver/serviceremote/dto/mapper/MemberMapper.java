package com.virnect.serviceserver.serviceremote.dto.mapper;

import org.mapstruct.Mapper;

import com.virnect.data.domain.member.Member;
import com.virnect.data.dto.response.member.MemberInfoResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface MemberMapper extends GenericMapper<MemberInfoResponse, Member> {
}
