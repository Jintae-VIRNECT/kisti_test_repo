package com.virnect.serviceserver.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.serviceserver.application.AdminService;
import com.virnect.serviceserver.dto.request.company.CompanyRequest;
import com.virnect.serviceserver.dto.request.company.CompanyResponse;
import com.virnect.serviceserver.error.ErrorCode;
import com.virnect.serviceserver.error.exception.RestServiceException;
import com.virnect.serviceserver.global.common.ApiResponse;
import com.virnect.serviceserver.infra.utils.LogMessage;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class AdminRestController {
    private static final String TAG = AdminRestController.class.getSimpleName();
    private static final String REST_PATH = "/remote/admin";
    //private static final String REST_COMPANY_PATH = "/remote/company";

    //private final UtilDataRepository utilDataRepository;

    private final AdminService adminService;

    @ApiOperation(value = "Create Company Information ", notes = "회사별 서비스 정보를 생성 합니다.")
    @PostMapping(value = "admin/company")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompanyRequestHandler(
        @RequestBody @Valid CompanyRequest companyRequest,
        BindingResult result
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: POST " + REST_PATH,
            "createCompanyRequestHandler"
        );

        // check company request handler
        if (result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                LogMessage.formedError(
                    TAG,
                    "REST API: POST " + REST_PATH,
                    "createCompanyRequestHandler",
                    LogMessage.PARAMETER_ERROR,
                    message.toString()
                )
            );
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<CompanyResponse> responseData = adminService.createCompany(companyRequest);
        return ResponseEntity.ok(responseData);
		/*ApiResponse<CompanyResponse> apiResponse = utilDataRepository.generateCompany(companyRequest);
		return ResponseEntity.ok(apiResponse);*/
    }
}
