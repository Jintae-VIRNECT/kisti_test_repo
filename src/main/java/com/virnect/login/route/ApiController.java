package com.virnect.login.route;

import com.virnect.login.common.ApiResponse;
import com.virnect.login.dto.LoginRequest;
import com.virnect.login.dto.LoginResponse;
import com.virnect.login.dto.request.EmailVerificationRequest;
import com.virnect.login.dto.request.LogoutRequest;
import com.virnect.login.dto.response.EmailVerificationResponse;
import com.virnect.login.dto.response.LogoutResponse;
import com.virnect.login.exception.AccountServiceException;
import com.virnect.login.exception.InvalidParameterException;
import com.virnect.login.global.ErrorCode;
import com.virnect.login.service.AuthRestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Login
 * @email practice1356@gmail.com
 * @description Proxy Api Controller Class
 * @since 2020.03.11
 */
@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class ApiController {
    private final AuthRestService authRestService;

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<LoginResponse>> loginProxy(@RequestBody @Valid LoginRequest loginRequest, BindingResult result) {
        if (result.hasErrors()) {
            throw new AccountServiceException(ErrorCode.ERR_INVALID_PARAMETER);
        }

        log.info("LOGIN-REQUEST: {}", loginRequest);
        ApiResponse<LoginResponse> loginResponse = this.authRestService.loginRequestHandler(loginRequest);
        log.info("LOGIN-RESPONSE: {}", loginResponse);

        return ResponseEntity.ok(loginResponse);
    }


    @PostMapping("/signout")
    public ResponseEntity<ApiResponse<LogoutResponse>> logoutProxy(@RequestBody @Valid LogoutRequest logoutRequest, BindingResult result) {
        if (result.hasErrors()) {
            throw new AccountServiceException(ErrorCode.ERR_INVALID_PARAMETER);
        }
        log.info("LOGOUT-REQUEST: {}", logoutRequest);
        ApiResponse<LogoutResponse> responseMessage = this.authRestService.logoutRequestHandler(logoutRequest);
        log.info("LOGOUT-RESPONSE: {}", logoutRequest);
        return ResponseEntity.ok(responseMessage);
    }

//
//    @PostMapping("/email")
//    public ResponseEntity<ApiResponse<EmailVerificationResponse>> emailAuthenticationRequestHandler(@RequestBody @Valid EmailVerificationRequest authenticationRequest, BindingResult result){
//        if(result.hasErrors()){
////            throw new InvalidParameterExce
//        }
//        return ResponseEntity.ok(new ApiResponse<>(new EmailAuthenticationResponse()));
//    }

}
