package com.virnect.data.api;

import com.virnect.data.ApiResponse;
import com.virnect.data.dto.request.CompanyRequest;
import com.virnect.data.dto.request.CompanyResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping("/remote")
public interface IAdminRestAPI {
    @ApiOperation(value = "Create Company Information ", notes = "회사별 서비스 정보를 생성 합니다.")
    @PostMapping(value = "admin/company")
    ResponseEntity<ApiResponse<CompanyResponse>> createCompanyRequestHandler(
            @RequestBody @Valid CompanyRequest companyRequest,
            BindingResult result);
}
