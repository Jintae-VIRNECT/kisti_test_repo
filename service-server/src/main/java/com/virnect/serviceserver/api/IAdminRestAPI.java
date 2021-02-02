package com.virnect.serviceserver.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.ApiOperation;

import com.virnect.serviceserver.global.common.ApiResponse;
import com.virnect.serviceserver.dto.request.company.CompanyRequest;
import com.virnect.serviceserver.dto.request.company.CompanyResponse;

@RequestMapping("/remote")
public interface IAdminRestAPI {
    @ApiOperation(value = "Create Company Information ", notes = "회사별 서비스 정보를 생성 합니다.")
    @PostMapping(value = "admin/company")
    ResponseEntity<ApiResponse<CompanyResponse>> createCompanyRequestHandler(
            @RequestBody @Valid CompanyRequest companyRequest,
            BindingResult result);
}
