package com.virnect.serviceserver.serviceremote.dto.mapper.company;

import org.mapstruct.Mapper;

import com.virnect.data.domain.Company;
import com.virnect.data.dto.response.company.CompanyInfoResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper extends GenericMapper<CompanyInfoResponse, Company> {
}
