package com.virnect.serviceserver.api;

import com.virnect.serviceserver.global.common.ApiResponse;
import com.virnect.serviceserver.dto.request.company.CompanyRequest;
import com.virnect.serviceserver.dto.request.company.CompanyResponse;
import com.virnect.serviceserver.error.ErrorCode;
import com.virnect.serviceserver.error.exception.RestServiceException;
import com.virnect.serviceserver.dao.UtilDataRepository;
import com.virnect.serviceserver.infra.utils.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminRestController implements IAdminRestAPI {
    private static final String TAG = AdminRestController.class.getSimpleName();
    private static final String REST_PATH = "/remote/admin";
    //private static final String REST_COMPANY_PATH = "/remote/company";

    private final UtilDataRepository utilDataRepository;

    @Override
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompanyRequestHandler(@Valid CompanyRequest companyRequest, BindingResult result) {
        LogMessage.formedInfo(
                TAG,
                "REST API: POST " + REST_PATH,
                "createCompanyRequestHandler"
        );

        // check company request handler
        if(result.hasErrors()) {
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

        ApiResponse<CompanyResponse> apiResponse = this.utilDataRepository.generateCompany(companyRequest);
        return ResponseEntity.ok(apiResponse);
    }
}
